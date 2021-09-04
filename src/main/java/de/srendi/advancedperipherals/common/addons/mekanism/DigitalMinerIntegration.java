package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

public class DigitalMinerIntegration extends TileEntityIntegrationPeripheral<TileEntityDigitalMiner> {
    
    public DigitalMinerIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "digitalMiner";
    }

    @LuaFunction(mainThread = true)
    public final int getDelay() {
        return tileEntity.getDelay();
    }

    @LuaFunction(mainThread = true)
    public final int getRadius() {
        return tileEntity.getRadius();
    }

    @LuaFunction(mainThread = true)
    public final void setRadius(int newRadius) {
        tileEntity.setRadiusFromPacket(newRadius);
    }

    @LuaFunction(mainThread = true)
    public final int getMinY() {
        return tileEntity.getMinY();
    }

    @LuaFunction(mainThread = true)
    public final void setMinY(int newMinY) {
        tileEntity.setMinYFromPacket(newMinY);
    }

    @LuaFunction(mainThread = true)
    public final int getMaxY() {
        return tileEntity.getMaxY();
    }

    @LuaFunction(mainThread = true)
    public final void setMaxY(int newMaxY) {
        tileEntity.setMaxYFromPacket(newMaxY);
    }

    @LuaFunction(mainThread = true)
    public final void toggleSilkTouch() {
        tileEntity.toggleSilkTouch();
    }

    @LuaFunction(mainThread = true)
    public final void toggleInverse() {
        tileEntity.toggleInverse();
    }

    @LuaFunction(mainThread = true)
    public final void toggleAutoEject() {
        tileEntity.toggleAutoEject();
    }

    @LuaFunction(mainThread = true)
    public final void toggleAutoPull() {
        tileEntity.toggleAutoPull();
    }

    @LuaFunction(mainThread = true)
    public final void start() {
        tileEntity.start();
    }

    @LuaFunction(mainThread = true)
    public final void stop() {
        tileEntity.stop();
    }

    @LuaFunction(mainThread = true)
    public final void reset() {
        tileEntity.reset();
    }

    @LuaFunction(mainThread = true)
    public final double getTotalEnergyFilledPercentage() {
        FloatingLong stored = FloatingLong.ZERO;
        FloatingLong max = FloatingLong.ZERO;
        List<IEnergyContainer> energyContainers = tileEntity.getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            stored = stored.plusEqual(energyContainer.getEnergy());
            max = max.plusEqual(energyContainer.getMaxEnergy());
        }
        return stored.divideToLevel(max);
    }

}
