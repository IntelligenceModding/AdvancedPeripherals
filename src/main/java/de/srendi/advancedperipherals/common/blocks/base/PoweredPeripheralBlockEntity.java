package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PoweredPeripheralBlockEntity<T extends BasePeripheral<?>> extends PeripheralBlockEntity<T> {

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    protected PoweredPeripheralBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    protected abstract int getMaxEnergyStored();

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        lazyEnergyStorage.ifPresent(iEnergyStorage -> compound.putInt("energy", iEnergyStorage.getEnergyStored()));
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        lazyEnergyStorage.ifPresent(iEnergyStorage -> iEnergyStorage.receiveEnergy(compound.getInt("energy") - iEnergyStorage.getEnergyStored(), false));
    }

    @Override
    public <U> @NotNull LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (APConfig.PERIPHERALS_CONFIG.enablePoweredPeripherals.get()) {
                if (!lazyEnergyStorage.isPresent()) {
                    lazyEnergyStorage = LazyOptional.of(() -> new EnergyStorage(this.getMaxEnergyStored()));
                }
                return lazyEnergyStorage.cast();
            }
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyEnergyStorage.invalidate();
    }
}
