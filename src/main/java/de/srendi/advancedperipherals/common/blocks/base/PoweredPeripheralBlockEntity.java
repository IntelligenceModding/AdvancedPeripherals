package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PoweredPeripheralBlockEntity<T extends BasePeripheral<?>> extends PeripheralBlockEntity<T> {

    private final IEnergyStorage energyStorage;

    protected PoweredPeripheralBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        this.energyStorage = APConfig.PERIPHERALS_CONFIG.enablePoweredPeripherals.get() ? EnergyStorage(this.getMaxEnergyStored()) : null;
    }

    protected abstract int getMaxEnergyStored();

    @Override
    public void saveAdditional(@NotNull CompoundTag compound, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        if (energyStorage != null)
            compound.putInt("energy", energyStorage.getEnergyStored());
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag compound, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        if (energyStorage != null)
            energyStorage.receiveEnergy(compound.getInt("energy") - energyStorage.getEnergyStored(), false);
    }

    @Nullable
    @Override
    public IEnergyStorage createEnergyStorageCap(@Nullable Direction side) {
        return energyStorage;
    }
}
