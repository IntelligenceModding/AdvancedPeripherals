package de.srendi.advancedperipherals.common.addons.mekanismgenerators;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.chemical.ChemicalStack;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorLogicAdapter;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class FissionIntegration extends TileEntityIntegrationPeripheral<TileEntityFissionReactorLogicAdapter> {

    public FissionIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "fissionReactor";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getCoolant() {
        Map<String, Object> wrapped = new HashMap<>(2);
        if (getReactor().fluidCoolantTank.isEmpty() && !getReactor().gasCoolantTank.isEmpty()) {
            ChemicalStack<?> stack = getReactor().gasCoolantTank.getStack();
            wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
            wrapped.put("amount", stack.getAmount());
            return wrapped;
        }
        FluidStack stack = getReactor().fluidCoolantTank.getFluid();
        wrapped.put("name", stack.getFluid().getRegistryName() == null ? null : stack.getFluid().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction(mainThread = true)
    public final long getCoolantCapacity() {
        if (getReactor().fluidCoolantTank.isEmpty() && !getReactor().gasCoolantTank.isEmpty()) {
            return getReactor().gasCoolantTank.getCapacity();
        }
        return getReactor().fluidCoolantTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getCoolantNeeded() {
        if (getReactor().fluidCoolantTank.isEmpty() && !getReactor().gasCoolantTank.isEmpty()) {
            return getReactor().gasCoolantTank.getNeeded();
        }
        return getReactor().fluidCoolantTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getCoolantFilledPercentage() {
        if (getReactor().fluidCoolantTank.isEmpty() && !getReactor().gasCoolantTank.isEmpty()) {
            return getReactor().gasCoolantTank.getStored() / (double) getReactor().gasCoolantTank.getCapacity();
        }
        return getReactor().fluidCoolantTank.getFluidAmount() / (double) getReactor().fluidCoolantTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getHeatedCoolant() {
        ChemicalStack<?> stack = getReactor().heatedCoolantTank.getStack();
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction(mainThread = true)
    public final long getHeatedCoolantCapacity() {
        return getReactor().heatedCoolantTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getHeatedCoolantNeeded() {
        return getReactor().heatedCoolantTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getHeatedCoolantFilledPercentage() {
        return getReactor().heatedCoolantTank.getStored() / (double) getHeatedCoolantCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getFuel() {
        return getReactor().fuelTank.getStored();
    }

    @LuaFunction(mainThread = true)
    public final long getFuelCapacity() {
        return getReactor().fuelTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getFuelNeeded() {
        return getReactor().fuelTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getFuelFilledPercentage() {
        return getFuel() / (double) getFuelCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getWaste() {
        return getReactor().wasteTank.getStored();
    }

    @LuaFunction(mainThread = true)
    public final long getWasteCapacity() {
        return getReactor().wasteTank.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getWasteNeeded() {
        return getReactor().wasteTank.getNeeded();
    }

    @LuaFunction(mainThread = true)
    public final double getWasteFilledPercentage() {
        return getWaste() / (double) getWasteCapacity();
    }

    @LuaFunction(mainThread = true)
    public final boolean getStatus() {
        return getReactor().isActive();
    }

    @LuaFunction(mainThread = true)
    public final void scram() {
        getReactor().setActive(false);
    }

    @LuaFunction(mainThread = true)
    public final void activate() {
        getReactor().setActive(true);
    }

    @LuaFunction(mainThread = true)
    public final double getBurnRate() {
        return getReactor().rateLimit;
    }

    @LuaFunction(mainThread = true)
    public final void setBurnRate(double rate) throws LuaException {
        rate = (double) Math.round(rate * 100) / 100;
        int max = getMaxBurnRate();
        if (rate < 0 || rate > max)
            throw new LuaException("Burn Rate '" + rate + "' is out of range must be between 0 and " + max + ". (Inclusive)");

        getReactor().rateLimit = Math.max(Math.min(getMaxBurnRate(), rate), 0);
    }

    @LuaFunction(mainThread = true)
    public final double getActualBurnRate() {
        return getReactor().lastBurnRate;
    }

    @LuaFunction(mainThread = true)
    public final int getMaxBurnRate() {
        return getReactor().fuelAssemblies;
    }

    @LuaFunction(mainThread = true)
    public final double getDamagePercent() {
        return Math.round(getReactor().reactorDamage);
    }

    @LuaFunction(mainThread = true)
    public final double getHeatingRate() {
        return getReactor().lastBoilRate;
    }

    @LuaFunction(mainThread = true)
    public final double getEnvironmentalLoss() {
        return getReactor().lastEnvironmentLoss;
    }

    @LuaFunction(mainThread = true)
    public final double getTemperature() {
        return getReactor().getTotalTemperature();
    }

    @LuaFunction(mainThread = true)
    public final double getHeatCapacity() {
        return getReactor().heatCapacitor.getHeatCapacity();
    }

    @LuaFunction(mainThread = true)
    public final int getFuelAssemblies() {
        return getReactor().fuelAssemblies;
    }

    @LuaFunction(mainThread = true)
    public final int getFuelSurfaceArea() {
        return getReactor().surfaceArea;
    }

    @LuaFunction(mainThread = true)
    public final double getBoilEfficiency() {
        return getReactor().getBoilEfficiency();
    }

    private FissionReactorMultiblockData getReactor() {
        return tileEntity.getMultiblock();
    }
}
