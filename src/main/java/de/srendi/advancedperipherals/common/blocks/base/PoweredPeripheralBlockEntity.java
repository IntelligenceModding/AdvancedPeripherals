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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PoweredPeripheralBlockEntity<T extends BasePeripheral<?>> extends PeripheralBlockEntity<T> {

    private final EnergyStorage energyStorage;
    private LazyOptional<EnergyStorage> energyStorageCap = LazyOptional.empty();

    protected PoweredPeripheralBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        this.energyStorage = APConfig.PERIPHERALS_CONFIG.enablePoweredPeripherals.get() ? new EnergyStorage(this.getMaxEnergyStored()) : null;
    }

    protected abstract int getMaxEnergyStored();

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        if (energyStorage != null) {
            compound.put("energy", energyStorage.serializeNBT());
        }
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (energyStorage != null) {
            energyStorage.deserializeNBT(compound.get("energy"));
        }
    }

    @NotNull
    @Override
    public <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (!this.energyStorageCap.isPresent()) {
                this.energyStorageCap = LazyOptional.of(() -> this.energyStorage);
            }
            return this.energyStorageCap.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyStorageCap.invalidate();
    }
}
