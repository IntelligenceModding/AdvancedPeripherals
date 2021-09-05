package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.math.MathUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.boiler.BoilerMultiblockData;
import mekanism.common.tile.multiblock.TileEntityBoilerValve;
import mekanism.common.util.HeatUtils;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

public class BoilerIntegration extends TileEntityIntegrationPeripheral<TileEntityBoilerValve> {

    public BoilerIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "boiler";
    }

    @LuaFunction(mainThread = true)
    public final int getWater() {
        return getBoiler().waterTank.getFluidAmount();
    }

    @LuaFunction(mainThread = true)
    public final int getWaterCapacity() {
        return getBoiler().waterTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final int getWaterNeeded() {
        return getBoiler().waterTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getWaterFilledPercentage() {
        return getWater() / (double) getWaterCapacity();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getHeatedCoolant() {
        ChemicalStack<?> stack = getBoiler().superheatedCoolantTank.getStack();
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction(mainThread = true)
    public final long getHeatedCoolantCapacity() {
        return getBoiler().superheatedCoolantTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getHeatedCoolantNeeded() {
        return getBoiler().superheatedCoolantTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getHeatedCoolantFilledPercentage() {
        return getBoiler().superheatedCoolantTank.getStored() / (double) getHeatedCoolantCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getSteam() {
        return getBoiler().steamTank.getStored();
    }

    @LuaFunction(mainThread = true)
    public final long getSteamCapacity() {
        return getBoiler().steamTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getSteamNeeded() {
        return getBoiler().steamTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getSteamFilledPercentage() {
        return getSteam() / (double) getSteamCapacity();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getCooledCoolant() {
        ChemicalStack<?> stack = getBoiler().cooledCoolantTank.getStack();
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction(mainThread = true)
    public final long getCooledCoolantCapacity() {
        return getBoiler().cooledCoolantTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getCooledCoolantNeeded() {
        return getBoiler().cooledCoolantTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getCooledCoolantFilledPercentage() {
        return getBoiler().cooledCoolantTank.getStack().getAmount() / (double) getCooledCoolantCapacity();
    }

    @LuaFunction(mainThread = true)
    public final double getEnvironmentalLoss() {
        return getBoiler().lastEnvironmentLoss;
    }

    @LuaFunction(mainThread = true)
    public final double getTemperature() {
        return getBoiler().getTotalTemperature();
    }

    @LuaFunction(mainThread = true)
    public final int getBoilRate() {
        return getBoiler().lastBoilRate;
    }

    @LuaFunction(mainThread = true)
    public final int getMaxBoilRate() {
        return getBoiler().lastMaxBoil;
    }

    @LuaFunction(mainThread = true)
    public final int getSuperheaters() {
        return getBoiler().superheatingElements;
    }

    @LuaFunction(mainThread = true)
    public final long getBoilCapacity() {
        double boilCapacity = MekanismConfig.general.superheatingHeatTransfer.get() * getSuperheaters() / HeatUtils.getWaterThermalEnthalpy();
        return MathUtils.clampToLong(boilCapacity * HeatUtils.getSteamEnergyEfficiency());
    }

    private BoilerMultiblockData getBoiler() {
        return tileEntity.getMultiblock();
    }
}
