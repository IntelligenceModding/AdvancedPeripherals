package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.machine.TileEntityDigitalMiner;

import java.util.List;

public class DigitalMinerIntegration extends Integration<TileEntityDigitalMiner> {

    @Override
    protected Class<TileEntityDigitalMiner> getTargetClass() {
        return TileEntityDigitalMiner.class;
    }

    @Override
    public DigitalMinerIntegration getNewInstance() {
        return new DigitalMinerIntegration();
    }

    @Override
    public String getType() {
        return "digitalMiner";
    }

    @LuaFunction
    public final int getDelay() {
        return getTileEntity().getDelay();
    }

    @LuaFunction
    public final int getRadius() {
        return getTileEntity().getRadius();
    }

    @LuaFunction
    public final void setRadius(int newRadius) {
        getTileEntity().setRadiusFromPacket(newRadius);
    }

    @LuaFunction
    public final int getMinY() {
        return getTileEntity().getMinY();
    }

    @LuaFunction
    public final void setMinY(int newMinY) {
        getTileEntity().setMinYFromPacket(newMinY);
    }

    @LuaFunction
    public final int getMaxY() {
        return getTileEntity().getMaxY();
    }

    @LuaFunction
    public final void setMaxY(int newMaxY) {
        getTileEntity().setMaxYFromPacket(newMaxY);
    }

    @LuaFunction
    public final void toggleSilkTouch() {
        getTileEntity().toggleSilkTouch();
    }

    @LuaFunction
    public final void toggleInverse() {
        getTileEntity().toggleInverse();
    }

    @LuaFunction
    public final void toggleAutoEject() {
        getTileEntity().toggleAutoEject();
    }

    @LuaFunction
    public final void toggleAutoPull() {
        getTileEntity().toggleAutoPull();
    }

    @LuaFunction
    public final void start() {
        getTileEntity().start();
    }

    @LuaFunction
    public final void stop() {
        getTileEntity().stop();
    }

    @LuaFunction
    public final void reset() {
        getTileEntity().reset();
    }

    @LuaFunction
    public final double getTotalEnergyFilledPercentage() {
        FloatingLong stored = FloatingLong.ZERO;
        FloatingLong max = FloatingLong.ZERO;
        List<IEnergyContainer> energyContainers = getTileEntity().getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            stored = stored.plusEqual(energyContainer.getEnergy());
            max = max.plusEqual(energyContainer.getMaxEnergy());
        }
        return stored.divideToLevel(max);
    }

}
