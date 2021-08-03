package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnergyDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import de.srendi.advancedperipherals.common.util.EnergyStorageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergyDetectorTile extends PeripheralTileEntity<EnergyDetectorPeripheral> {

    public int transferRate = 0;
    //storageProxy that will forward the energy to the output but limit it to maxTransferRate
    public EnergyStorageProxy storageProxy = new EnergyStorageProxy(this, AdvancedPeripheralsConfig.energyDetectorMaxFlow);
    LazyOptional<IEnergyStorage> energyStorageCap = LazyOptional.of(() -> storageProxy);
    Direction energyInDirection = Direction.NORTH;
    Direction energyOutDirection = Direction.SOUTH;
    @NotNull
    private Optional<IEnergyStorage> outReceivingStorage = Optional.empty();
    //a zero size, zero transfer energy storage to ensure that cables connect
    private EnergyStorage zeroStorage = new EnergyStorage(0, 0, 0);
    LazyOptional<IEnergyStorage> zeroStorageCap = LazyOptional.of(() -> zeroStorage);

    public EnergyDetectorTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.ENERGY_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected EnergyDetectorPeripheral createPeripheral() {
        return new EnergyDetectorPeripheral("energyDetector", this);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        energyInDirection = getBlockState().getValue(APTileEntityBlock.FACING);
        energyOutDirection = getBlockState().getValue(APTileEntityBlock.FACING).getOpposite();
        if (cap == CapabilityEnergy.ENERGY) {
            if (direction == energyInDirection) {
                return energyStorageCap.cast();
            } else if (direction == energyOutDirection) {
                return zeroStorageCap.cast();
            }
        }
        return super.getCapability(cap, direction);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState state, EnergyDetectorTile blockEntity) {
        if (!level.isClientSide) {
            // this handles the rare edge case that receiveEnergy is called multiple times in one tick
            blockEntity.transferRate = blockEntity.storageProxy.getTransferedInThisTick();
            blockEntity.storageProxy.resetTransferedInThisTick();
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.putInt("rateLimit", storageProxy.getMaxTransferRate());
        return compound;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (level1, blockPos, blockState, blockEntity) -> {
            serverTick(level, blockPos, blockState, (EnergyDetectorTile) blockEntity);
        };
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        storageProxy.setMaxTransferRate(nbt.getInt("rateLimit"));
        super.deserializeNBT(nbt);
    }

    // returns the cached output storage of the receiving block or refetches it if it has been invalidated
    @NotNull
    public Optional<IEnergyStorage> getOutputStorage() {
        // the documentation says that the value of the LazyOptional should be cached locally and invallidated using addListener
        if (!outReceivingStorage.isPresent()) {
            BlockEntity teOut = level.getBlockEntity(worldPosition.relative(energyOutDirection));
            if (teOut == null) {
                return Optional.empty();
            }
            LazyOptional<IEnergyStorage> lazyOptionalOutStorage = teOut.getCapability(CapabilityEnergy.ENERGY, energyOutDirection.getOpposite());
            outReceivingStorage = lazyOptionalOutStorage.resolve();
            lazyOptionalOutStorage.addListener(l -> {
                outReceivingStorage = Optional.empty();
            });
        }
        return outReceivingStorage;
    }
}