package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.blocks.tileentity.EnergyDetectorTile;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.configuration.GeneralConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TileEntityPeripheralOwner;

public class EnergyDetectorPeripheral extends BasePeripheral<TileEntityPeripheralOwner<EnergyDetectorTile>> {

    public static final String TYPE = "energyDetector";

    public EnergyDetectorPeripheral(EnergyDetectorTile tileEntity) {
        super(TYPE, new TileEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.ENABLE_ENERGY_DETECTOR.get();
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRateLimit() {
        return owner.tileEntity.storageProxy.getMaxTransferRate();
    }

    @LuaFunction(mainThread = true)
    public final void setTransferRateLimit(long transferRate) {
        transferRate = Math.min(APConfig.PERIPHERALS_CONFIG.ENERGY_DETECTOR_MAX_FLOW.get(), transferRate);
        owner.tileEntity.storageProxy.setMaxTransferRate((int) transferRate);
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRate() {
        return owner.tileEntity.transferRate;
    }
}
