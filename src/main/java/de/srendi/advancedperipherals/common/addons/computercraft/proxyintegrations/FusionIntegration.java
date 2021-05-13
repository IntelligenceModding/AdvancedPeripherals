package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorLogicAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class FusionIntegration extends ProxyIntegration<TileEntityFusionReactorLogicAdapter> {
    @Override
    protected Class<TileEntityFusionReactorLogicAdapter> getTargetClass() {
        return TileEntityFusionReactorLogicAdapter.class;
    }

    @Override
    protected FusionIntegration getNewInstance() {
        return new FusionIntegration();
    }

    @Override
    protected String getName() {
        return "fusionReactor";
    }

    @LuaFunction
    public final Map<String, Object> getHohlraum() {
        ItemStack stack = getReactor().getStackInSlot(0);
        Map<String, Object> wrapped = new HashMap<>(3);
        wrapped.put("name", stack.getItem().getRegistryName().toString());
        wrapped.put("count", stack.getCount());
        wrapped.put("nbt", NBTUtil.toLua(stack.getOrCreateTag()));
        wrapped.put("tags", getListFromTags(stack.getItem().getTags()));
        return wrapped;
    }

    @LuaFunction
    public final double getPlasmaTemperature() {
        return getReactor().getPlasmaTemp();
    }

    @LuaFunction
    public final double getCaseTemperature() {
        return getReactor().getCaseTemp();
    }

    @LuaFunction
    public final int getWater() {
        return getReactor().waterTank.getFluidAmount();
    }

    @LuaFunction
    public final int getWaterCapacity() {
        return getReactor().waterTank.getCapacity();
    }

    @LuaFunction
    public final int getWaterNeeded() {
        return getReactor().waterTank.getNeeded();
    }

    @LuaFunction
    public final double getWaterFilledPercentage() {
        return getWater() / (double) getWaterCapacity();
    }

    @LuaFunction
    public final long getSteam() {
        return getReactor().steamTank.getStored();
    }

    @LuaFunction
    public final long getSteamCapacity() {
        return getReactor().steamTank.getCapacity();
    }

    @LuaFunction
    public final long getSteamNeeded() {
        return getReactor().steamTank.getNeeded();
    }

    @LuaFunction
    public final double getSteamFilledPercentage() {
        return getSteam() / (double) getSteamCapacity();
    }

    @LuaFunction
    public final long getTritium() {
        return getReactor().tritiumTank.getStored();
    }

    @LuaFunction
    public final long getTritiumCapacity() {
        return getReactor().tritiumTank.getCapacity();
    }

    @LuaFunction
    public final long getTritiumNeeded() {
        return getReactor().tritiumTank.getNeeded();
    }

    @LuaFunction
    public final double getTritiumFilledPercentage() {
        return getTritium() / (double) getTritiumCapacity();
    }

    @LuaFunction
    public final long getDeuterium() {
        return getReactor().deuteriumTank.getStored();
    }

    @LuaFunction
    public final long getDeuteriumCapacity() {
        return getReactor().deuteriumTank.getCapacity();
    }

    @LuaFunction
    public final long getDeuteriumNeeded() {
        return getReactor().deuteriumTank.getNeeded();
    }

    @LuaFunction
    public final double getDeuteriumFilledPercentage() {
        return getDeuterium() / (double) getDeuteriumCapacity();
    }

    @LuaFunction
    public final long getDTFuel() {
        return getReactor().fuelTank.getStored();
    }

    @LuaFunction
    public final long getDTFuelCapacity() {
        return getReactor().fuelTank.getCapacity();
    }

    @LuaFunction
    public final long getDTFuelNeeded() {
        return getReactor().fuelTank.getNeeded();
    }

    @LuaFunction
    public final double getDTFuelFilledPercentage() {
        return getDTFuel() / (double) getDTFuelCapacity();
    }

    @LuaFunction
    public final long getProduction() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getReactor().getPassiveGeneration(false, false));
    }

    @LuaFunction
    public final int getInjectionRate() {
        return getReactor().getInjectionRate();
    }

    @LuaFunction
    public final void setInjectionRate(int rate) {
        getReactor().setInjectionRate(rate);
    }

    @LuaFunction
    public final long getPassiveGeneration(boolean active) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getReactor().getPassiveGeneration(active, false));
    }

    private FusionReactorMultiblockData getReactor() {
        return getTileEntity().getMultiblock();
    }

    public List<String> getListFromTags(Set<ResourceLocation> tags) {
        List<String> list = new ArrayList<>();
        for (ResourceLocation value : tags) {
            list.add(value.getNamespace() + ":" + value.getPath());
        }
        return list;
    }
}
