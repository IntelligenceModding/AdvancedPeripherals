package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.api.chemical.ChemicalStack;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorLogicAdapter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.AbstractDocument;
import java.util.HashMap;
import java.util.Map;

public class FissionIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "fissionReactor");
    }

    @LuaFunction
    public static Map<String, Object> getCoolant(TileEntityFissionReactorLogicAdapter tileEntity) {
        Map<String, Object> wrapped = new HashMap<>(2);
        if (getReactor(tileEntity).fluidCoolantTank.isEmpty() && !getReactor(tileEntity).gasCoolantTank.isEmpty()) {
            ChemicalStack<?> stack = getReactor(tileEntity).gasCoolantTank.getStack();
            wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
            wrapped.put("amount", stack.getAmount());
            return wrapped;
        }
        FluidStack stack = getReactor(tileEntity).fluidCoolantTank.getFluid();
        wrapped.put("name", stack.getFluid().getRegistryName() == null ? null : stack.getFluid().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction
    public static long getCoolantCapacity(TileEntityFissionReactorLogicAdapter tileEntity) {
        if (getReactor(tileEntity).fluidCoolantTank.isEmpty() && !getReactor(tileEntity).gasCoolantTank.isEmpty()) {
            return getReactor(tileEntity).gasCoolantTank.getCapacity();
        }
        return getReactor(tileEntity).fluidCoolantTank.getCapacity();
    }

    @LuaFunction
    public static long getCoolantNeeded(TileEntityFissionReactorLogicAdapter tileEntity) {
        if (getReactor(tileEntity).fluidCoolantTank.isEmpty() && !getReactor(tileEntity).gasCoolantTank.isEmpty()) {
            return getReactor(tileEntity).gasCoolantTank.getNeeded();
        }
        return getReactor(tileEntity).fluidCoolantTank.getNeeded();
    }

    @LuaFunction
    public static double getCoolantFilledPercentage(TileEntityFissionReactorLogicAdapter tileEntity) {
        if (getReactor(tileEntity).fluidCoolantTank.isEmpty() && !getReactor(tileEntity).gasCoolantTank.isEmpty()) {
            return getReactor(tileEntity).gasCoolantTank.getStored() / (double) getReactor(tileEntity).gasCoolantTank.getCapacity();
        }
        return getReactor(tileEntity).fluidCoolantTank.getFluidAmount() / (double) getReactor(tileEntity).fluidCoolantTank.getCapacity();
    }

    @LuaFunction
    public static Map<String, Object> getHeatedCoolant(TileEntityFissionReactorLogicAdapter tileEntity) {
        ChemicalStack<?> stack = getReactor(tileEntity).heatedCoolantTank.getStack();
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction
    public static long getHeatedCoolantCapacity(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).heatedCoolantTank.getCapacity();
    }

    @LuaFunction
    public static long getHeatedCoolantNeeded(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).heatedCoolantTank.getNeeded();
    }

    @LuaFunction
    public static double getHeatedCoolantFilledPercentage(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).heatedCoolantTank.getStored() / (double) getHeatedCoolantCapacity(tileEntity);
    }

    @LuaFunction
    public static long getFuel(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).fuelTank.getStored();
    }

    @LuaFunction
    public static long getFuelCapacity(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).fuelTank.getCapacity();
    }

    @LuaFunction
    public static long getFuelNeeded(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).fuelTank.getNeeded();
    }

    @LuaFunction
    public static double getFuelFilledPercentage(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getFuel(tileEntity) / (double) getFuelCapacity(tileEntity);
    }

    @LuaFunction
    public static long getWaste(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).wasteTank.getStored();
    }

    @LuaFunction
    public static long getWasteCapacity(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).wasteTank.getCapacity();
    }

    @LuaFunction
    public static long getWasteNeeded(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).wasteTank.getNeeded();
    }

    @LuaFunction
    public static double getWasteFilledPercentage(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getWaste(tileEntity) / (double) getWasteCapacity(tileEntity);
    }

    @LuaFunction
    public static boolean getStatus(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).isActive();
    }

    @LuaFunction
    public static void scram(TileEntityFissionReactorLogicAdapter tileEntity) {
        getReactor(tileEntity).setActive(false);
    }

    @LuaFunction
    public static void activate(TileEntityFissionReactorLogicAdapter tileEntity) {
        getReactor(tileEntity).setActive(true);
    }

    @LuaFunction
    public static double getBurnRate(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).rateLimit;
    }

    @LuaFunction
    public static void setBurnRate(TileEntityFissionReactorLogicAdapter tileEntity, double rate) throws LuaException {
        rate = (double) Math.round(rate * 100) / 100;
        int max = getMaxBurnRate(tileEntity);
        if (rate < 0 || rate > max)
            throw new LuaException("Burn Rate '" + rate + "' is out of range must be between 0 and " + max + ". (Inclusive)");

        getReactor(tileEntity).rateLimit = Math.max(Math.min(getMaxBurnRate(tileEntity), rate), 0);
    }

    @LuaFunction
    public static double getActualBurnRate(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).lastBurnRate;
    }

    @LuaFunction
    public static int getMaxBurnRate(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).fuelAssemblies;
    }

    @LuaFunction
    public static double getDamagePercent(TileEntityFissionReactorLogicAdapter tileEntity) {
        return Math.round(getReactor(tileEntity).reactorDamage);
    }

    @LuaFunction
    public static double getHeatingRate(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).lastBoilRate;
    }

    @LuaFunction
    public static double getEnvironmentalLoss(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).lastEnvironmentLoss;
    }

    @LuaFunction
    public static double getTemperature(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).getTotalTemperature();
    }

    @LuaFunction
    public static double getHeatCapacity(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).heatCapacitor.getHeatCapacity();
    }

    @LuaFunction
    public static int getFuelAssemblies(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).fuelAssemblies;
    }

    @LuaFunction
    public static int getFuelSurfaceArea(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).surfaceArea;
    }

    @LuaFunction
    public static double getBoilEfficiency(TileEntityFissionReactorLogicAdapter tileEntity) {
        return getReactor(tileEntity).getBoilEfficiency();
    }

    private static FissionReactorMultiblockData getReactor(TileEntityFissionReactorLogicAdapter tileEntity) {
        return tileEntity.getMultiblock();
    }
}
