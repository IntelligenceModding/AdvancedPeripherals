package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class PoweredPeripheralBlockEntity<T extends BasePeripheral<?>> extends PeripheralBlockEntity<T> {

    private final IEnergyStorage energyStorage;

    protected PoweredPeripheralBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        this.energyStorage = APConfig.PERIPHERALS_CONFIG.enablePoweredPeripherals.get() ? new EnergyStorage(this.getMaxEnergyStored()) : null;
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
    public IEnergyStorage createEnergyStorageCap(@Nullable Direction side) {
        return energyStorage;
    }

    public static class Type<T extends PoweredPeripheralBlockEntity<?>> extends PeripheralBlockEntity.Type<T> {
        public Type(BlockEntitySupplier<? extends T> factory, Set<Block> validBlocks, com.mojang.datafixers.types.Type<?> dataType) {
            super(factory, validBlocks, dataType);
        }

        @Override
        public void registerCapabilities(RegisterCapabilitiesEvent event) {
            super.registerCapabilities(event);
            event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                this,
                (be, side) -> be.createEnergyStorageCap(side)
            );
        }
    }
}
