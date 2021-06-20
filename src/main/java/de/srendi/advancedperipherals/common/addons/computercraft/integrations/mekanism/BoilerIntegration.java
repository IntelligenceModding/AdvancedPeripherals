package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.math.MathUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.tile.multiblock.TileEntityBoilerValve;
import mekanism.common.util.HeatUtils;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BoilerIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "boiler");
    }

    @LuaFunction
    public static int getWater(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).waterTank.getFluidAmount();
    }

    @LuaFunction
    public static int getWaterCapacity(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).waterTank.getCapacity();
    }

    @LuaFunction
    public static int getWaterNeeded(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).waterTank.getNeeded();
    }

    @LuaFunction
    public static double getWaterFilledPercentage(TileEntityBoilerValve tileEntity) {
        return getWater(tileEntity) / (double) getWaterCapacity(tileEntity);
    }

    @LuaFunction
    public static Map<String, Object> getHeatedCoolant(TileEntityBoilerValve tileEntity) {
        ChemicalStack<?> stack = getBoiler(tileEntity).superheatedCoolantTank.getStack();
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction
    public static long getHeatedCoolantCapacity(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).superheatedCoolantTank.getCapacity();
    }

    @LuaFunction
    public static long getHeatedCoolantNeeded(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).superheatedCoolantTank.getNeeded();
    }

    @LuaFunction
    public static double getHeatedCoolantFilledPercentage(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).superheatedCoolantTank.getStored() / (double) getHeatedCoolantCapacity(tileEntity);
    }

    @LuaFunction
    public static long getSteam(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).steamTank.getStored();
    }

    @LuaFunction
    public static long getSteamCapacity(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).steamTank.getCapacity();
    }

    @LuaFunction
    public static long getSteamNeeded(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).steamTank.getNeeded();
    }

    @LuaFunction
    public static double getSteamFilledPercentage(TileEntityBoilerValve tileEntity) {
        return getSteam(tileEntity) / (double) getSteamCapacity(tileEntity);
    }

    @LuaFunction
    public static Map<String, Object> getCooledCoolant(TileEntityBoilerValve tileEntity) {
        ChemicalStack<?> stack = getBoiler(tileEntity).cooledCoolantTank.getStack();
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction
    public static long getCooledCoolantCapacity(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).cooledCoolantTank.getCapacity();
    }

    @LuaFunction
    public static long getCooledCoolantNeeded(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).cooledCoolantTank.getNeeded();
    }

    @LuaFunction
    public static double getCooledCoolantFilledPercentage(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).cooledCoolantTank.getStack().getAmount() / (double) getCooledCoolantCapacity(tileEntity);
    }

    @LuaFunction
    public static double getEnvironmentalLoss(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).lastEnvironmentLoss;
    }

    @LuaFunction
    public static double getTemperature(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).getTotalTemperature();
    }

    @LuaFunction
    public static int getBoilRate(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).lastBoilRate;
    }

    @LuaFunction
    public static int getMaxBoilRate(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).lastMaxBoil;
    }

    @LuaFunction
    public static int getSuperheaters(TileEntityBoilerValve tileEntity) {
        return getBoiler(tileEntity).superheatingElements;
    }

    @LuaFunction
    public static long getBoilCapacity(TileEntityBoilerValve tileEntity) {
        double boilCapacity = MekanismConfig.general.superheatingHeatTransfer.get() * getSuperheaters(tileEntity) / HeatUtils.getWaterThermalEnthalpy();
        return MathUtils.clampToLong(boilCapacity * HeatUtils.getSteamEnergyEfficiency());
    }

    private static BoilerMultiblockData getBoiler(TileEntityBoilerValve tileEntity) {
        return tileEntity.getMultiblock();
    }
}
