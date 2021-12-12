package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.transmitter.TileEntityThermodynamicConductor;
import mekanism.common.tile.transmitter.TileEntityUniversalCable;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ThermodynamicConductorIntegration extends TileEntityIntegrationPeripheral<TileEntityThermodynamicConductor> {

    public ThermodynamicConductorIntegration(TileEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "universalCable";
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
    public double getStored() {
        return tileEntity.getTransmitter().buffer.getHeat();
    }

    @LuaFunction(mainThread = true)
    public double getCapacity() {
        return tileEntity.getTransmitter().getTotalHeatCapacity();
    }

    @LuaFunction(mainThread = true)
    public String getTier() {
        return tileEntity.getTransmitter().tier.toString();
    }

}
