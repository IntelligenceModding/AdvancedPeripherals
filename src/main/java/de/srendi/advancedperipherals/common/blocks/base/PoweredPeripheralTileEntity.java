package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PoweredPeripheralTileEntity<T extends BasePeripheral> extends PeripheralTileEntity<T> {

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    public PoweredPeripheralTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        if (AdvancedPeripheralsConfig.enablePoweredPeripherals) {
            lazyEnergyStorage = LazyOptional.of(() -> new EnergyStorage(this.getMaxEnergyStored()));
        } else {
            lazyEnergyStorage = LazyOptional.empty();
        }
    }

    protected abstract int getMaxEnergyStored();

    @Override
    public CompoundTag save(CompoundTag compound) {
        lazyEnergyStorage.ifPresent(iEnergyStorage -> {
            compound.putInt("energy", iEnergyStorage.getEnergyStored());
        });
        return super.save(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        lazyEnergyStorage.ifPresent(iEnergyStorage -> {
            iEnergyStorage.receiveEnergy(compound.getInt("energy") - iEnergyStorage.getEnergyStored(), false);
        });
    }

    @Override
    public <T1> @NotNull LazyOptional<T1> getCapability(@NotNull Capability<T1> cap, @Nullable Direction direction) {
        if (cap == CapabilityEnergy.ENERGY && lazyEnergyStorage.isPresent()) {
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        this.lazyEnergyStorage.invalidate();
    }

}
