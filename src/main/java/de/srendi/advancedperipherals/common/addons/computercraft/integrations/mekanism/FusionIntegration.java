package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorLogicAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FusionIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "fusionReactor");
    }

    @LuaFunction
    public static Map<String, Object> getHohlraum(TileEntityFusionReactorLogicAdapter tileEntity) {
        ItemStack stack = getReactor(tileEntity).getStackInSlot(0);
        Map<String, Object> wrapped = new HashMap<>(3);
        wrapped.put("name", stack.getItem().getRegistryName().toString());
        wrapped.put("count", stack.getCount());
        wrapped.put("nbt", NBTUtil.toLua(stack.getOrCreateTag()));
        wrapped.put("tags", getListFromTags(stack.getItem().getTags()));
        return wrapped;
    }

    @LuaFunction
    public static double getPlasmaTemperature(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).getPlasmaTemp();
    }

    @LuaFunction
    public static double getCaseTemperature(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).getCaseTemp();
    }

    @LuaFunction
    public static int getWater(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).waterTank.getFluidAmount();
    }

    @LuaFunction
    public static int getWaterCapacity(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).waterTank.getCapacity();
    }

    @LuaFunction
    public static int getWaterNeeded(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).waterTank.getNeeded();
    }

    @LuaFunction
    public static double getWaterFilledPercentage(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getWater(tileEntity) / (double) getWaterCapacity(tileEntity);
    }

    @LuaFunction
    public static long getSteam(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).steamTank.getStored();
    }

    @LuaFunction
    public static long getSteamCapacity(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).steamTank.getCapacity();
    }

    @LuaFunction
    public static long getSteamNeeded(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).steamTank.getNeeded();
    }

    @LuaFunction
    public static double getSteamFilledPercentage(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getSteam(tileEntity) / (double) getSteamCapacity(tileEntity);
    }

    @LuaFunction
    public static long getTritium(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).tritiumTank.getStored();
    }

    @LuaFunction
    public static long getTritiumCapacity(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).tritiumTank.getCapacity();
    }

    @LuaFunction
    public static long getTritiumNeeded(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).tritiumTank.getNeeded();
    }

    @LuaFunction
    public static double getTritiumFilledPercentage(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getTritium(tileEntity) / (double) getTritiumCapacity(tileEntity);
    }

    @LuaFunction
    public static long getDeuterium(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).deuteriumTank.getStored();
    }

    @LuaFunction
    public static long getDeuteriumCapacity(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).deuteriumTank.getCapacity();
    }

    @LuaFunction
    public static long getDeuteriumNeeded(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).deuteriumTank.getNeeded();
    }

    @LuaFunction
    public static double getDeuteriumFilledPercentage(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getDeuterium(tileEntity) / (double) getDeuteriumCapacity(tileEntity);
    }

    @LuaFunction
    public static long getDTFuel(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).fuelTank.getStored();
    }

    @LuaFunction
    public static long getDTFuelCapacity(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).fuelTank.getCapacity();
    }

    @LuaFunction
    public static long getDTFuelNeeded(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).fuelTank.getNeeded();
    }

    @LuaFunction
    public static double getDTFuelFilledPercentage(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getDTFuel(tileEntity) / (double) getDTFuelCapacity(tileEntity);
    }

    @LuaFunction
    public static long getProduction(TileEntityFusionReactorLogicAdapter tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getReactor(tileEntity).getPassiveGeneration(false, false));
    }

    @LuaFunction
    public static int getInjectionRate(TileEntityFusionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).getInjectionRate();
    }

    @LuaFunction
    public static void setInjectionRate(TileEntityFusionReactorLogicAdapter tileEntity, int rate) {
        getReactor(tileEntity).setInjectionRate(rate);
    }

    @LuaFunction
    public static long getPassiveGeneration(TileEntityFusionReactorLogicAdapter tileEntity, boolean active) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getReactor(tileEntity).getPassiveGeneration(active, false));
    }

    private static FusionReactorMultiblockData getReactor(TileEntityFusionReactorLogicAdapter tileEntity) {
        return tileEntity.getMultiblock();
    }

    public static List<String> getListFromTags(Set<ResourceLocation> tags) {
        List<String> list = new ArrayList<>();
        for (ResourceLocation value : tags) {
            list.add(value.toString());
        }
        return list;
    }
}
