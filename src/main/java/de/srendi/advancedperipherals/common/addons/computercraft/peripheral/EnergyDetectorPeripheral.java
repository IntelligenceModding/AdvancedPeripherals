package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.tileentity.EnergyDetectorTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.lib.peripherals.owner.BlockEntityPeripheralOwner;

public class EnergyDetectorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<EnergyDetectorTile>> {

    public static final String TYPE = "energyDetector";

    public EnergyDetectorPeripheral(EnergyDetectorTile tileEntity) {
        super(TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableEnergyDetector;
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRateLimit() {
        return owner.tileEntity.storageProxy.getMaxTransferRate();
    }

    @LuaFunction(mainThread = true)
    public final void setTransferRateLimit(long transferRate) {
        transferRate = Math.min(AdvancedPeripheralsConfig.energyDetectorMaxFlow, transferRate);
        owner.tileEntity.storageProxy.setMaxTransferRate((int) transferRate);
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRate() {
        return owner.tileEntity.transferRate;
    }
}
