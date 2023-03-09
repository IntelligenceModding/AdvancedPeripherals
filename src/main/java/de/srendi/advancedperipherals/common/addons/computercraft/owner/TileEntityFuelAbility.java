package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

public class TileEntityFuelAbility<T extends BlockEntity & IPeripheralTileEntity> extends FuelAbility<BlockEntityPeripheralOwner<T>> {

    public TileEntityFuelAbility(@NotNull BlockEntityPeripheralOwner<T> owner) {
        super(owner);
    }

    @Override
    protected boolean consumeFuel(int count) {
        return owner.tileEntity.getCapability(ForgeCapabilities.ENERGY).map(storage -> {
            int energyCount = count * APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get();
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
        return !APConfig.PERIPHERALS_CONFIG.enablePoweredPeripherals.get();
    }

    @Override
    public int getFuelCount() {
        return owner.tileEntity.getCapability(ForgeCapabilities.ENERGY).map(storage -> storage.getEnergyStored() / APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get()).orElse(0);
    }

    @Override
    public int getFuelMaxCount() {
        return owner.tileEntity.getCapability(ForgeCapabilities.ENERGY).map(storage -> storage.getMaxEnergyStored() / APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get()).orElse(0);
    }

    @Override
    public void addFuel(int count) {
        owner.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(storage -> {
            int energyCount = count * APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get();
            storage.receiveEnergy(energyCount, false);
        });
    }
}
