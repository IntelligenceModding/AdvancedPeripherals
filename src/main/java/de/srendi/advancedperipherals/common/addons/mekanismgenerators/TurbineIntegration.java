package de.srendi.advancedperipherals.common.addons.mekanismgenerators;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.MathUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.content.turbine.TurbineValidator;
import mekanism.generators.common.tile.turbine.TileEntityTurbineValve;
import net.minecraft.tileentity.TileEntity;

public class TurbineIntegration extends TileEntityIntegrationPeripheral<TileEntityTurbineValve> {

    public TurbineIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "turbine";
    }

    @LuaFunction(mainThread = true)
    public final long getSteam() {
        return getTurbine().gasTank.getStored();
    }

    @LuaFunction(mainThread = true)
    public final long getSteamCapacity() {
        return getTurbine().gasTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getSteamNeeded() {
        return getTurbine().gasTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final long getLastSteamInputRate() {
        return getTurbine().lastSteamInput;
    }

    @LuaFunction(mainThread = true)
    public final double getSteamFilledPercentage() {
        return getSteam() / (double) getSteamCapacity();
    }

    @LuaFunction(mainThread = true)
    public final String getDumpingMode() {
        return getTurbine().dumpMode.name();
    }

    @LuaFunction(mainThread = true)
    public final long getProductionRate() {
        FloatingLong energyMultiplier = MekanismConfig.general.maxEnergyPerSteam.get().divide(TurbineValidator.MAX_BLADES)
                .multiply(Math.min(getBlades(), getCoils() * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get()));
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(energyMultiplier.multiply(getTurbine().clientFlow));
    }

    @LuaFunction(mainThread = true)
    public final long getMaxProduction() {
        FloatingLong energyMultiplier = MekanismConfig.general.maxEnergyPerSteam.get().divide(TurbineValidator.MAX_BLADES)
                .multiply(Math.min(getBlades(), getCoils() * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get()));
        double rate = getTurbine().lowerVolume * (getTurbine().getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
        rate = Math.min(rate, getVents() * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(energyMultiplier.multiply(rate));
    }

    @LuaFunction(mainThread = true)
    public final long getFlowRate() {
        return getTurbine().clientFlow;
    }

    @LuaFunction(mainThread = true)
    public final long getMaxFlowRate() {
        double rate = getTurbine().lowerVolume * (getTurbine().getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
        rate = Math.min(rate, getVents() * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
        return MathUtils.clampToLong(rate);
    }

    @LuaFunction(mainThread = true)
    public final long getMaxWaterOutput() {
        return (long) getCondensers() * MekanismGeneratorsConfig.generators.condenserRate.get();
    }

    @LuaFunction(mainThread = true)
    public final int getDispersers() {
        return getTurbine().getDispersers();
    }

    @LuaFunction(mainThread = true)
    public final int getVents() {
        return getTurbine().vents;
    }

    @LuaFunction(mainThread = true)
    public final int getBlades() {
        return getTurbine().blades;
    }

    @LuaFunction(mainThread = true)
    public final int getCoils() {
        return getTurbine().coils;
    }

    @LuaFunction(mainThread = true)
    public final int getCondensers() {
        return getTurbine().condensers;
    }

    private TurbineMultiblockData getTurbine() {
        return tileEntity.getMultiblock();
    }

}
