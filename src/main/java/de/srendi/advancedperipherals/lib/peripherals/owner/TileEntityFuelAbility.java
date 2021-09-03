package de.srendi.advancedperipherals.lib.peripherals.owner;

import de.srendi.advancedperipherals.api.peripherals.IPeripheralTileEntity;
import de.srendi.advancedperipherals.api.peripherals.owner.FuelAbility;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
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
            int energyCount = count * AdvancedPeripheralsConfig.energyToFuelRate;
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
        return owner.tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> storage.getEnergyStored() / AdvancedPeripheralsConfig.energyToFuelRate).orElse(0);
    }

    @Override
    public int getFuelMaxCount() {
        return owner.tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> storage.getMaxEnergyStored() / AdvancedPeripheralsConfig.energyToFuelRate).orElse(0);
    }

    @Override
    public void addFuel(int count) {
        owner.tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(storage -> {
            int energyCount = count * AdvancedPeripheralsConfig.energyToFuelRate;
            storage.receiveEnergy(energyCount, false);
        });
    }
}
