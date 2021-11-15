package de.srendi.advancedperipherals.lib.peripherals.owner;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.configuration.GeneralConfig;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;

public class TileEntityFuelAbility<T extends TileEntity & IPeripheralTileEntity> extends FuelAbility<TileEntityPeripheralOwner<T>> {

    public TileEntityFuelAbility(@NotNull TileEntityPeripheralOwner<T> owner) {
        super(owner);
    }

    @Override
    protected boolean _consumeFuel(int count) {
        return owner.tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> {
            int energyCount = count * APConfig.METAPHYSICS_CONFIG.ENERGY_TO_FUEL_RATE.get();
            int extractedCount = storage.extractEnergy(energyCount, true);
            if (extractedCount == energyCount) {
                storage.extractEnergy(energyCount, false);
                return true;
            }
            return false;
        }).orElse(false);
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return DEFAULT_FUEL_CONSUMING_RATE;
    }

    @Override
    public boolean isFuelConsumptionDisable() {
        return false;
    }

    @Override
    public int getFuelCount() {
        return owner.tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> storage.getEnergyStored() / APConfig.METAPHYSICS_CONFIG.ENERGY_TO_FUEL_RATE.get()).orElse(0);
    }

    @Override
    public int getFuelMaxCount() {
        return owner.tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> storage.getMaxEnergyStored() / APConfig.METAPHYSICS_CONFIG.ENERGY_TO_FUEL_RATE.get()).orElse(0);
    }

    @Override
    public void addFuel(int count) {
        owner.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(storage -> {
            int energyCount = count * APConfig.METAPHYSICS_CONFIG.ENERGY_TO_FUEL_RATE.get();
            storage.receiveEnergy(energyCount, false);
        });
    }
}
