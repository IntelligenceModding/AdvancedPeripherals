package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.GasDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

public class GasDetectorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<GasDetectorEntity>> {

    public static final String TYPE = "gas_detector";

    public GasDetectorPeripheral(GasDetectorEntity tileEntity) {
        super(TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableGasDetector.get();
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRateLimit() {
        return owner.tileEntity.storageProxy.getMaxTransferRate();
    }

    @LuaFunction(mainThread = true)
    public final String getTransferedGas() {
        return owner.tileEntity.lastFlowedGas.getRaw().getRegistryName().toString();
    }

    @LuaFunction(mainThread = true)
    public final void setTransferRateLimit(long transferRate) {
        transferRate = Math.min(APConfig.PERIPHERALS_CONFIG.gasDetectorMaxFlow.get(), transferRate);
        owner.tileEntity.storageProxy.setMaxTransferRate((int) transferRate);
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRate() {
        return owner.tileEntity.transferRate;
    }
}
