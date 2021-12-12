package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.math.FloatingLong;
import mekanism.common.item.ItemNetworkReader;
import mekanism.common.tile.transmitter.TileEntityMechanicalPipe;
import mekanism.common.tile.transmitter.TileEntityUniversalCable;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class MechanicalPipeIntegration extends TileEntityIntegrationPeripheral<TileEntityMechanicalPipe> {

    public MechanicalPipeIntegration(TileEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "mechanicalPipe";
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
    public int getThroughput() {
        return tileEntity.getTransmitter().getTransmitterNetwork().getPrevTransferAmount();
    }

    @LuaFunction(mainThread = true)
    public int getNetworkStored() {
        return tileEntity.getTransmitter().getTransmitterNetwork().fluidTank.getFluidAmount();
    }

    @LuaFunction(mainThread = true)
    public long getNetworkCapacity() {
        return tileEntity.getTransmitter().getTransmitterNetwork().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public int getStored() {
        return tileEntity.getTransmitter().buffer.getFluidAmount();
    }

    @LuaFunction(mainThread = true)
    public long getCapacity() {
        return tileEntity.getTransmitter().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public String getTier() {
        return tileEntity.getTransmitter().tier.toString();
    }

}
