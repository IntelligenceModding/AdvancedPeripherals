package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.MathUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.content.turbine.TurbineValidator;
import mekanism.generators.common.tile.turbine.TileEntityTurbineValve;

public class TurbineIntegration extends ProxyIntegration<TileEntityTurbineValve> {
    @Override
    protected Class<TileEntityTurbineValve> getTargetClass() {
        return TileEntityTurbineValve.class;
    }

    @Override
    public TurbineIntegration getNewInstance() {
        return new TurbineIntegration();
    }

    @Override
    protected String getName() {
        return "turbine";
    }

    @LuaFunction
    public final long getSteam() {
        return getTurbine().gasTank.getStored();
    }

    @LuaFunction
    public final long getSteamCapacity() {
        return getTurbine().gasTank.getCapacity();
    }

    @LuaFunction
    public final long getSteamNeeded() {
        return getTurbine().gasTank.getNeeded();
    }

    @LuaFunction
    public final long getLastSteamInputRate() {
        return getTurbine().lastSteamInput;
    }

    @LuaFunction
    public final double getSteamFilledPercentage() {
        return getSteam() / (double) getSteamCapacity();
    }

    @LuaFunction
    public final String getDumpingMode() {
        return getTurbine().dumpMode.name();
    }

    @LuaFunction
    public final long getProductionRate() {
        FloatingLong energyMultiplier = MekanismConfig.general.maxEnergyPerSteam.get().divide(TurbineValidator.MAX_BLADES)
                .multiply(Math.min(getBlades(), getCoils() * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get()));
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(energyMultiplier.multiply(getTurbine().clientFlow));
    }

    @LuaFunction
    public final long getMaxProduction() {
        FloatingLong energyMultiplier = MekanismConfig.general.maxEnergyPerSteam.get().divide(TurbineValidator.MAX_BLADES)
                .multiply(Math.min(getBlades(), getCoils() * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get()));
        double rate = getTurbine().lowerVolume * (getTurbine().getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
        rate = Math.min(rate, getVents() * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(energyMultiplier.multiply(rate));
    }

    @LuaFunction
    public final long getFlowRate() {
        return getTurbine().clientFlow;
    }

    @LuaFunction
    public final long getMaxFlowRate() {
        double rate = getTurbine().lowerVolume * (getTurbine().getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
        rate = Math.min(rate, getVents() * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
        return MathUtils.clampToLong(rate);
    }

    @LuaFunction
    public final long getMaxWaterOutput() {
        return (long) getCondensers() * MekanismGeneratorsConfig.generators.condenserRate.get();
    }

    @LuaFunction
    public final int getDispersers() {
        return getTurbine().getDispersers();
    }

    @LuaFunction
    public final int getVents() {
        return getTurbine().vents;
    }

    @LuaFunction
    public final int getBlades() {
        return getTurbine().blades;
    }

    @LuaFunction
    public final int getCoils() {
        return getTurbine().coils;
    }

    @LuaFunction
    public final int getCondensers() {
        return getTurbine().condensers;
    }

    private TurbineMultiblockData getTurbine() {
        return getTileEntity().getMultiblock();
    }

}
