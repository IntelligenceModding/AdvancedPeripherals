package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.common.tile.transmitter.TileEntityLogisticalTransporter;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

public class LogisticalTransporterIntegration extends TileEntityIntegrationPeripheral<TileEntityLogisticalTransporter> {

    public LogisticalTransporterIntegration(TileEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "logisticalTransporter";
    }

    @LuaFunction(mainThread = true)
    public int getTransmitters() {
        return tileEntity.getTransmitter().getTransmitterNetwork().transmittersSize();
    }

    @LuaFunction(mainThread = true)
    public int getAcceptors() {
        return tileEntity.getTransmitter().getTransmitterNetwork().getAcceptorCount();
    }

    @LuaFunction(mainThread = true)
    public int getBaseSpeed() {
        return tileEntity.getTransmitter().tier.getBaseSpeed();
    }

    @LuaFunction(mainThread = true)
    public int getBasePull() {
        return tileEntity.getTransmitter().tier.getBasePull();
    }

    @LuaFunction(mainThread = true)
    public int getSpeed() {
        return tileEntity.getTransmitter().tier.getSpeed();
    }

    @LuaFunction(mainThread = true)
    public int getPull() {
        return tileEntity.getTransmitter().tier.getPullAmount();
    }

    @LuaFunction(mainThread = true)
    public String getTier() {
        return tileEntity.getTransmitter().tier.toString();
    }

}
