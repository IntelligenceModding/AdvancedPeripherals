/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnergyDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.EnergyStorageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergyDetectorEntity extends PeripheralBlockEntity<EnergyDetectorPeripheral> {

    public int transferRate = 0;
    // storageProxy that will forward the energy to the output but limit it to
    // maxTransferRate
    public final EnergyStorageProxy storageProxy = new EnergyStorageProxy(this,
            APConfig.PERIPHERALS_CONFIG.energyDetectorMaxFlow.get());
    // a zero size, zero transfer energy storage to ensure that cables connect
    private final EnergyStorage zeroStorage = new EnergyStorage(0, 0, 0);
    private final LazyOptional<IEnergyStorage> energyStorageCap = LazyOptional.of(() -> storageProxy);
    private final LazyOptional<IEnergyStorage> zeroStorageCap = LazyOptional.of(() -> zeroStorage);
    @NotNull private Optional<IEnergyStorage> outReceivingStorage = Optional.empty();

    private Direction energyInDirection = Direction.NORTH;
    private Direction energyOutDirection = Direction.SOUTH;

    public EnergyDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.ENERGY_DETECTOR.get(), pos, state);
    }

    @NotNull @Override
    protected EnergyDetectorPeripheral createPeripheral() {
        return new EnergyDetectorPeripheral(this);
    }

    @NotNull @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        energyInDirection = getBlockState().getValue(BaseBlock.ORIENTATION).front();
        energyOutDirection = getBlockState().getValue(BaseBlock.ORIENTATION).front().getOpposite();
        if (cap == ForgeCapabilities.ENERGY) {
            if (direction == energyInDirection) {
                return energyStorageCap.cast();
            } else if (direction == energyOutDirection) {
                return zeroStorageCap.cast();
            }
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("rateLimit", storageProxy.getMaxTransferRate());
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            // this handles the rare edge case that receiveEnergy is called multiple times
            // in one tick
            transferRate = storageProxy.getTransferedInThisTick();
            storageProxy.resetTransferedInThisTick();
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        storageProxy.setMaxTransferRate(nbt.getInt("rateLimit"));
        super.load(nbt);
    }

    public void invalidateStorages() {
        outReceivingStorage = Optional.empty();
    }

    // returns the cached output storage of the receiving block or fetches it if it
    // has been invalidated
    @NotNull public Optional<IEnergyStorage> getOutputStorage() {
        // the documentation says that the value of the LazyOptional should be cached
        // locally and invalidated using addListener
        if (outReceivingStorage.isEmpty()) {
            BlockEntity teOut = level.getBlockEntity(worldPosition.relative(energyOutDirection));
            if (teOut == null) {
                return Optional.empty();
            }
            LazyOptional<IEnergyStorage> lazyOptionalOutStorage = teOut.getCapability(ForgeCapabilities.ENERGY,
                    energyOutDirection.getOpposite());
            outReceivingStorage = lazyOptionalOutStorage.resolve();
            lazyOptionalOutStorage.addListener(l -> {
                outReceivingStorage = Optional.empty();
            });
        }
        return outReceivingStorage;
    }
}
