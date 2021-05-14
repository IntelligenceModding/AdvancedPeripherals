package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.math.MathUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.tile.multiblock.TileEntityBoilerValve;
import mekanism.common.util.HeatUtils;

import java.util.HashMap;
import java.util.Map;

public class BoilerIntegration extends ProxyIntegration<TileEntityBoilerValve> {
    @Override
    protected Class<TileEntityBoilerValve> getTargetClass() {
        return TileEntityBoilerValve.class;
    }

    @Override
    public BoilerIntegration getNewInstance() {
        return new BoilerIntegration();
    }

    @Override
    protected String getName() {
        return "boiler";
    }

    @LuaFunction
    public final int getWater() {
        return getBoiler().waterTank.getFluidAmount();
    }

    @LuaFunction
    public final int getWaterCapacity() {
        return getBoiler().waterTank.getCapacity();
    }

    @LuaFunction
    public final int getWaterNeeded() {
        return getBoiler().waterTank.getNeeded();
    }

    @LuaFunction
    public final double getWaterFilledPercentage() {
        return getWater() / (double) getWaterCapacity();
    }

    @LuaFunction
    public final Map<String, Object> getHeatedCoolant() {
        ChemicalStack<?> stack = getBoiler().superheatedCoolantTank.getStack();
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction
    public final long getHeatedCoolantCapacity() {
        return getBoiler().superheatedCoolantTank.getCapacity();
    }

    @LuaFunction
    public final long getHeatedCoolantNeeded() {
        return getBoiler().superheatedCoolantTank.getNeeded();
    }

    @LuaFunction
    public final double getHeatedCoolantFilledPercentage() {
        return getBoiler().superheatedCoolantTank.getStored() / (double) getHeatedCoolantCapacity();
    }

    @LuaFunction
    public final long getSteam() {
        return getBoiler().steamTank.getStored();
    }

    @LuaFunction
    public final long getSteamCapacity() {
        return getBoiler().steamTank.getCapacity();
    }

    @LuaFunction
    public final long getSteamNeeded() {
        return getBoiler().steamTank.getNeeded();
    }

    @LuaFunction
    public final double getSteamFilledPercentage() {
        return getSteam() / (double) getSteamCapacity();
    }

    @LuaFunction
    public final Map<String, Object> getCooledCoolant() {
        ChemicalStack<?> stack = getBoiler().cooledCoolantTank.getStack();
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction
    public final long getCooledCoolantCapacity() {
        return getBoiler().cooledCoolantTank.getCapacity();
    }

    @LuaFunction
    public final long getCooledCoolantNeeded() {
        return getBoiler().cooledCoolantTank.getNeeded();
    }

    @LuaFunction
    public final double getCooledCoolantFilledPercentage() {
        return getBoiler().cooledCoolantTank.getStack().getAmount() / (double) getCooledCoolantCapacity();
    }

    @LuaFunction
    public final double getEnvironmentalLoss() {
        return getBoiler().lastEnvironmentLoss;
    }

    @LuaFunction
    public final double getTemperature() {
        return getBoiler().getTotalTemperature();
    }

    @LuaFunction
    public final int getBoilRate() {
        return getBoiler().lastBoilRate;
    }

    @LuaFunction
    public final int getMaxBoilRate() {
        return getBoiler().lastMaxBoil;
    }

    @LuaFunction
    public final int getSuperheaters() {
        return getBoiler().superheatingElements;
    }

    @LuaFunction
    public final long getBoilCapacity() {
        double boilCapacity = MekanismConfig.general.superheatingHeatTransfer.get() * getSuperheaters() / HeatUtils.getWaterThermalEnthalpy();
        return MathUtils.clampToLong(boilCapacity * HeatUtils.getSteamEnergyEfficiency());
    }

    private BoilerMultiblockData getBoiler() {
        return getTileEntity().getMultiblock();
    }
}
