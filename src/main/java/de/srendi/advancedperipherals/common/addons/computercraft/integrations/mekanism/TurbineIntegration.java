package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.MathUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.content.turbine.TurbineValidator;
import mekanism.generators.common.tile.turbine.TileEntityTurbineValve;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TurbineIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "turbine");
    }

    @LuaFunction
    public static long getSteam(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).gasTank.getStored();
    }

    @LuaFunction
    public static long getSteamCapacity(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).gasTank.getCapacity();
    }

    @LuaFunction
    public static long getSteamNeeded(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).gasTank.getNeeded();
    }

    @LuaFunction
    public static long getLastSteamInputRate(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).lastSteamInput;
    }

    @LuaFunction
    public static double getSteamFilledPercentage(TileEntityTurbineValve tileEntity) {
        return getSteam(tileEntity) / (double) getSteamCapacity(tileEntity);
    }

    @LuaFunction
    public static String getDumpingMode(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).dumpMode.name();
    }

    @LuaFunction
    public static long getProductionRate(TileEntityTurbineValve tileEntity) {
        FloatingLong energyMultiplier = MekanismConfig.general.maxEnergyPerSteam.get().divide(TurbineValidator.MAX_BLADES)
                .multiply(Math.min(getBlades(tileEntity), getCoils(tileEntity) * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get()));
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(energyMultiplier.multiply(getTurbine(tileEntity).clientFlow));
    }

    @LuaFunction
    public static long getMaxProduction(TileEntityTurbineValve tileEntity) {
        FloatingLong energyMultiplier = MekanismConfig.general.maxEnergyPerSteam.get().divide(TurbineValidator.MAX_BLADES)
                .multiply(Math.min(getBlades(tileEntity), getCoils(tileEntity) * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get()));
        double rate = getTurbine(tileEntity).lowerVolume * (getTurbine(tileEntity).getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
        rate = Math.min(rate, getVents(tileEntity) * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(energyMultiplier.multiply(rate));
    }

    @LuaFunction
    public static long getFlowRate(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).clientFlow;
    }

    @LuaFunction
    public static long getMaxFlowRate(TileEntityTurbineValve tileEntity) {
        double rate = getTurbine(tileEntity).lowerVolume * (getTurbine(tileEntity).getDispersers() * MekanismGeneratorsConfig.generators.turbineDisperserGasFlow.get());
        rate = Math.min(rate, getVents(tileEntity) * MekanismGeneratorsConfig.generators.turbineVentGasFlow.get());
        return MathUtils.clampToLong(rate);
    }

    @LuaFunction
    public static long getMaxWaterOutput(TileEntityTurbineValve tileEntity) {
        return (long) getCondensers(tileEntity) * MekanismGeneratorsConfig.generators.condenserRate.get();
    }

    @LuaFunction
    public static int getDispersers(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).getDispersers();
    }

    @LuaFunction
    public static int getVents(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).vents;
    }

    @LuaFunction
    public static int getBlades(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).blades;
    }

    @LuaFunction
    public static int getCoils(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).coils;
    }

    @LuaFunction
    public static int getCondensers(TileEntityTurbineValve tileEntity) {
        return getTurbine(tileEntity).condensers;
    }

    private static TurbineMultiblockData getTurbine(TileEntityTurbineValve tileEntity) {
        return tileEntity.getMultiblock();
    }

}
