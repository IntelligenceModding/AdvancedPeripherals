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

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    protected PoweredPeripheralBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        if (APConfig.PERIPHERALS_CONFIG.enablePoweredPeripherals.get()) {
            lazyEnergyStorage = LazyOptional.of(() -> new EnergyStorage(this.getMaxEnergyStored()));
        } else {
            lazyEnergyStorage = LazyOptional.empty();
        }
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
    public <T1> @NotNull LazyOptional<T1> getCapability(@NotNull Capability<T1> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ENERGY && lazyEnergyStorage.isPresent()) {
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyEnergyStorage.invalidate();
    }

}
