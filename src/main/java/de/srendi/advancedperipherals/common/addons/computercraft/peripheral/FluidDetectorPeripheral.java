package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.FluidDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidDetectorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<FluidDetectorEntity>> {

    public static final String TYPE = "fluid_detector";

    public FluidDetectorPeripheral(FluidDetectorEntity tileEntity) {
        super(TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableFluidDetector.get();
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRateLimit() {
        return owner.tileEntity.storageProxy.getMaxTransferRate();
    }

    @LuaFunction(mainThread = true)
    public final String getTransferedFluid() {
        return ForgeRegistries.FLUIDS.getKey(owner.tileEntity.lastFlowedLiquid.getFluid()).toString();
    }

    @LuaFunction(mainThread = true)
    public final void setTransferRateLimit(long transferRate) {
        transferRate = Math.min(APConfig.PERIPHERALS_CONFIG.fluidDetectorMaxFlow.get(), transferRate);
        owner.tileEntity.storageProxy.setMaxTransferRate((int) transferRate);
    }

    @LuaFunction(mainThread = true)
    public final int getTransferRate() {
        return owner.tileEntity.transferRate;
    }
}
