package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DigitalMinerIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "digitalMiner");
    }

    @LuaFunction
    public static int getDelay(TileEntityDigitalMiner tileEntity) {
        return tileEntity.getDelay();
    }

    @LuaFunction
    public static int getRadius(TileEntityDigitalMiner tileEntity) {
        return tileEntity.getRadius();
    }

    @LuaFunction
    public static void setRadius(TileEntityDigitalMiner tileEntity, int newRadius) {
        tileEntity.setRadiusFromPacket(newRadius);
    }

    @LuaFunction
    public static int getMinY(TileEntityDigitalMiner tileEntity) {
        return tileEntity.getMinY();
    }

    @LuaFunction
    public static void setMinY(TileEntityDigitalMiner tileEntity, int newMinY) {
        tileEntity.setMinYFromPacket(newMinY);
    }

    @LuaFunction
    public static int getMaxY(TileEntityDigitalMiner tileEntity) {
        return tileEntity.getMaxY();
    }

    @LuaFunction
    public static void setMaxY(TileEntityDigitalMiner tileEntity, int newMaxY) {
        tileEntity.setMaxYFromPacket(newMaxY);
    }

    @LuaFunction
    public static void toggleSilkTouch(TileEntityDigitalMiner tileEntity) {
        tileEntity.toggleSilkTouch();
    }

    @LuaFunction
    public static void toggleInverse(TileEntityDigitalMiner tileEntity) {
        tileEntity.toggleInverse();
    }

    @LuaFunction
    public static void toggleAutoEject(TileEntityDigitalMiner tileEntity) {
        tileEntity.toggleAutoEject();
    }

    @LuaFunction
    public static void toggleAutoPull(TileEntityDigitalMiner tileEntity) {
        tileEntity.toggleAutoPull();
    }

    @LuaFunction
    public static void start(TileEntityDigitalMiner tileEntity) {
        tileEntity.start();
    }

    @LuaFunction
    public static void stop(TileEntityDigitalMiner tileEntity) {
        tileEntity.stop();
    }

    @LuaFunction
    public static void reset(TileEntityDigitalMiner tileEntity) {
        tileEntity.reset();
    }

    @LuaFunction
    public static double getTotalEnergyFilledPercentage(TileEntityDigitalMiner tileEntity) {
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
