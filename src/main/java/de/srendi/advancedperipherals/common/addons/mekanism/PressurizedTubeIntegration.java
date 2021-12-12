package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.transmitter.TileEntityPressurizedTube;
import mekanism.common.tile.transmitter.TileEntityUniversalCable;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class PressurizedTubeIntegration extends TileEntityIntegrationPeripheral<TileEntityPressurizedTube> {

    public PressurizedTubeIntegration(TileEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "pressurizedTube";
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
    public long getThroughput() {
        try {
            Field field = tileEntity.getClass().getDeclaredField("prevTransferAmount");
            field.setAccessible(true);
            FloatingLong throughput = (FloatingLong) field.get(tileEntity);
            return throughput.getValue();
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            AdvancedPeripherals.debug("Could not access the throughout of the pylon! " + ex.getMessage(), Level.ERROR);
        }
        return 0;
    }

    @LuaFunction(mainThread = true)
    public long getStored() {
        return tileEntity.getTransmitter().chemicalTank.getGasTank().getStored();
    }

    @LuaFunction(mainThread = true)
    public long getNetworkStored() {
        return tileEntity.getTransmitter().getTransmitterNetwork().chemicalTank.getGasTank().getStored();
    }


    @LuaFunction(mainThread = true)
    public long getCapacity() {
        return tileEntity.getTransmitter().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public long getNetworkCapacity() {
        return tileEntity.getTransmitter().getTransmitterNetwork().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public String getTier() {
        return tileEntity.getTransmitter().tier.toString();
    }

}
