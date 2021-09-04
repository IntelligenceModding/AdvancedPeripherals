package de.srendi.advancedperipherals.common.addons.mekanismgenerators;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorLogicAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

public class FusionIntegration extends TileEntityIntegrationPeripheral<TileEntityFusionReactorLogicAdapter> {

    public FusionIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "fusionReactor";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getHohlraum() {
        ItemStack stack = getReactor().getStackInSlot(0);
        Map<String, Object> wrapped = new HashMap<>(3);
        wrapped.put("name", stack.getItem().getRegistryName().toString());
        wrapped.put("count", stack.getCount());
        wrapped.put("nbt", NBTUtil.toLua(stack.getOrCreateTag()));
        wrapped.put("tags", LuaConverter.tagsToList(stack.getItem().getTags()));
        return wrapped;
    }

    @LuaFunction(mainThread = true)
    public final double getPlasmaTemperature() {
        return getReactor().getPlasmaTemp();
    }

    @LuaFunction(mainThread = true)
    public final double getCaseTemperature() {
        return getReactor().getCaseTemp();
    }

    @LuaFunction(mainThread = true)
    public final int getWater() {
        return getReactor().waterTank.getFluidAmount();
    }

    @LuaFunction(mainThread = true)
    public final int getWaterCapacity() {
        return getReactor().waterTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final int getWaterNeeded() {
        return getReactor().waterTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getWaterFilledPercentage() {
        return getWater() / (double) getWaterCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getSteam() {
        return getReactor().steamTank.getStored();
    }

    @LuaFunction(mainThread = true)
    public final long getSteamCapacity() {
        return getReactor().steamTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getSteamNeeded() {
        return getReactor().steamTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getSteamFilledPercentage() {
        return getSteam() / (double) getSteamCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getTritium() {
        return getReactor().tritiumTank.getStored();
    }

    @LuaFunction(mainThread = true)
    public final long getTritiumCapacity() {
        return getReactor().tritiumTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getTritiumNeeded() {
        return getReactor().tritiumTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getTritiumFilledPercentage() {
        return getTritium() / (double) getTritiumCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getDeuterium() {
        return getReactor().deuteriumTank.getStored();
    }

    @LuaFunction(mainThread = true)
    public final long getDeuteriumCapacity() {
        return getReactor().deuteriumTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getDeuteriumNeeded() {
        return getReactor().deuteriumTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getDeuteriumFilledPercentage() {
        return getDeuterium() / (double) getDeuteriumCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getDTFuel() {
        return getReactor().fuelTank.getStored();
    }

    @LuaFunction(mainThread = true)
    public final long getDTFuelCapacity() {
        return getReactor().fuelTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getDTFuelNeeded() {
        return getReactor().fuelTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getDTFuelFilledPercentage() {
        return getDTFuel() / (double) getDTFuelCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getProduction() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getReactor().getPassiveGeneration(false, false));
    }

    @LuaFunction(mainThread = true)
    public final int getInjectionRate() {
        return getReactor().getInjectionRate();
    }

    @LuaFunction(mainThread = true)
    public final void setInjectionRate(int rate) {
        getReactor().setInjectionRate(rate);
    }

    @LuaFunction(mainThread = true)
    public final long getPassiveGeneration(boolean active) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getReactor().getPassiveGeneration(active, false));
    }

    private FusionReactorMultiblockData getReactor() {
        return tileEntity.getMultiblock();
    }
}
