package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
public abstract class PoweredPeripheralTileEntity<T extends BasePeripheral<?>> extends PeripheralTileEntity<T> {

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    public PoweredPeripheralTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        if (AdvancedPeripheralsConfig.enablePoweredPeripherals) {
            lazyEnergyStorage = LazyOptional.of(() -> new EnergyStorage(this.getMaxEnergyStored()));
        } else {
            lazyEnergyStorage = LazyOptional.empty();
        }
    }

    protected abstract int getMaxEnergyStored();

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        lazyEnergyStorage.ifPresent(iEnergyStorage -> {
            compound.putInt("energy", iEnergyStorage.getEnergyStored());
        });
        return super.save(compound);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compound) {
        super.load(blockState, compound);
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
