package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.blocks.tileentity.EnergyDetectorTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;

public class EnergyDetectorPeripheral extends BasePeripheral {

    EnergyDetectorTileEntity tileEntity;

    public EnergyDetectorPeripheral(String type, EnergyDetectorTileEntity tileEntity) {
        super(type, tileEntity);
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableEnergyDetector;
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRateLimit() {
        return tileEntity.maxTransferRate;
    }

    @LuaFunction(mainThread = true)
    public final void setTransferRateLimit(long transferRate) {
        if (transferRate > Integer.MAX_VALUE) {
            transferRate = Integer.MAX_VALUE; //Integer max value is the maximum of the transfer rate limit.
        }
        tileEntity.maxTransferRate = (int) transferRate;
        tileEntity.storageOut.setMaxExtract((int) transferRate);
        tileEntity.storageOut.setCapacity((int) transferRate);
        tileEntity.storageIn.setCapacity((int) transferRate);
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRate() {
        return tileEntity.transferRate;
    }
}
