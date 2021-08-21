package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.tileentity.EnergyDetectorTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;

public class EnergyDetectorPeripheral extends BasePeripheral {

    public static final String TYPE = "energyDetector";

    EnergyDetectorTile tileEntity;

    public EnergyDetectorPeripheral(EnergyDetectorTile tileEntity) {
        super(TYPE, tileEntity);
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableEnergyDetector;
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRateLimit() {
        return tileEntity.storageProxy.getMaxTransferRate();
    }

    @LuaFunction(mainThread = true)
    public final void setTransferRateLimit(long transferRate) {
        transferRate = Math.min(AdvancedPeripheralsConfig.energyDetectorMaxFlow, transferRate);
        tileEntity.storageProxy.setMaxTransferRate((int) transferRate);
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRate() {
        return tileEntity.transferRate;
    }
}
