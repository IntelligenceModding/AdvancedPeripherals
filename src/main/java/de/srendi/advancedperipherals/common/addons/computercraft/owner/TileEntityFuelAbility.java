package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class TileEntityFuelAbility<T extends BlockEntity & IPeripheralTileEntity> extends FuelAbility<BlockEntityPeripheralOwner<T>> {

    public TileEntityFuelAbility(@NotNull BlockEntityPeripheralOwner<T> owner) {
        super(owner);
    }

    @Override
    protected boolean consumeFuel(int count) {
        IEnergyStorage energyStorage = owner.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, owner.getPos(), null);
        if (energyStorage != null) {
            int energyCount = count * APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get();
            int extractedCount = energyStorage.extractEnergy(energyCount, true);
            if (extractedCount == energyCount) {
                energyStorage.extractEnergy(energyCount, false);
                return true;
            }
            return false;

        }
        return false;
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return DEFAULT_FUEL_CONSUMING_RATE;
    }

    @Override
    public boolean isFuelConsumptionDisable() {
        return !APConfig.PERIPHERALS_CONFIG.enablePoweredPeripherals.get();
    }

    @Override
    public int getFuelCount() {
        IEnergyStorage energyStorage = owner.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, owner.getPos(), null);
        if (energyStorage != null)
            return energyStorage.getEnergyStored() / APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get();
        return 0;
    }

    @Override
    public int getFuelMaxCount() {
        IEnergyStorage energyStorage = owner.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, owner.getPos(), null);
        if (energyStorage != null)
            return energyStorage.getEnergyStored() / APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get();
        return 0;
    }

    @Override
    public void addFuel(int count) {
        IEnergyStorage energyStorage = owner.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, owner.getPos(), null);
        if (energyStorage != null)
            energyStorage.receiveEnergy(count * APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get(), false);
    }
}
