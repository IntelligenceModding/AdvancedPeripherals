package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.mekanism;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.api.chemical.ChemicalStack;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorLogicAdapter;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

public class FissionIntegration extends ProxyIntegration<TileEntityFissionReactorLogicAdapter> {
    @Override
    protected Class<TileEntityFissionReactorLogicAdapter> getTargetClass() {
        return TileEntityFissionReactorLogicAdapter.class;
    }

    @Override
    public FissionIntegration getNewInstance() {
        return new FissionIntegration();
    }

    @Override
    protected String getName() {
        return "fissionReactor";
    }

    @LuaFunction
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

    @LuaFunction
    public final long getCoolantCapacity() {
        if (getReactor().fluidCoolantTank.isEmpty() && !getReactor().gasCoolantTank.isEmpty()) {
            return getReactor().gasCoolantTank.getCapacity();
        }
        return getReactor().fluidCoolantTank.getCapacity();
    }

    @LuaFunction
    public final long getCoolantNeeded() {
        if (getReactor().fluidCoolantTank.isEmpty() && !getReactor().gasCoolantTank.isEmpty()) {
            return getReactor().gasCoolantTank.getNeeded();
        }
        return getReactor().fluidCoolantTank.getNeeded();
    }

    @LuaFunction
    public final double getCoolantFilledPercentage() {
        if (getReactor().fluidCoolantTank.isEmpty() && !getReactor().gasCoolantTank.isEmpty()) {
            return getReactor().gasCoolantTank.getStored() / (double) getReactor().gasCoolantTank.getCapacity();
        }
        return getReactor().fluidCoolantTank.getFluidAmount() / (double) getReactor().fluidCoolantTank.getCapacity();
    }

    @LuaFunction
    public final Map<String, Object> getHeatedCoolant() {
        ChemicalStack<?> stack = getReactor().heatedCoolantTank.getStack();
        Map<String, Object> wrapped = new HashMap<>(2);
        wrapped.put("name", stack.getType().getRegistryName() == null ? null : stack.getType().getRegistryName().toString());
        wrapped.put("amount", stack.getAmount());
        return wrapped;
    }

    @LuaFunction
    public final long getHeatedCoolantCapacity() {
        return getReactor().heatedCoolantTank.getCapacity();
    }

    @LuaFunction
    public final long getHeatedCoolantNeeded() {
        return getReactor().heatedCoolantTank.getNeeded();
    }

    @LuaFunction
    public final double getHeatedCoolantFilledPercentage() {
        return getReactor().heatedCoolantTank.getStored() / (double) getHeatedCoolantCapacity();
    }

    @LuaFunction
    public final long getFuel() {
        return getReactor().fuelTank.getStored();
    }

    @LuaFunction
    public final long getFuelCapacity() {
        return getReactor().fuelTank.getCapacity();
    }

    @LuaFunction
    public final long getFuelNeeded() {
        return getReactor().fuelTank.getNeeded();
    }

    @LuaFunction
    public final double getFuelPercentage() {
        return getFuel() / (double) getFuelCapacity();
    }

    @LuaFunction
    public final long getWaste() {
        return getReactor().wasteTank.getStored();
    }

    @LuaFunction
    public final long getWasteCapacity() {
        return getReactor().wasteTank.getCapacity();
    }

    @LuaFunction
    public final long getWasteNeeded() {
        return getReactor().wasteTank.getNeeded();
    }

    @LuaFunction
    public final double getWasteFilledPercentage() {
        return getWaste() / (double) getWasteCapacity();
    }

    @LuaFunction
    public final boolean getStatus() {
        return getReactor().isActive();
    }

    @LuaFunction
    public final void scram() {
        getReactor().setActive(false);
    }

    @LuaFunction
    public final void activate() {
        getReactor().setActive(true);
    }

    @LuaFunction
    public final double getBurnRate() {
        return getReactor().rateLimit;
    }

    @LuaFunction
    public final void setBurnRate(double rate) throws LuaException {
        rate = (double) Math.round(rate * 100) / 100;
        int max = getMaxBurnRate();
        if (rate < 0 || rate > max)
            throw new LuaException("Burn Rate '" + rate + "' is out of range must be between 0 and " + max + ". (Inclusive)");

        getReactor().rateLimit = Math.max(Math.min(getMaxBurnRate(), rate), 0);
    }

    @LuaFunction
    public final double getActualBurnRate() {
        return getReactor().lastBurnRate;
    }

    @LuaFunction
    public final int getMaxBurnRate() {
        return getReactor().fuelAssemblies;
    }

    @LuaFunction
    public final double getDamagePercent() {
        return Math.round(getReactor().reactorDamage);
    }

    @LuaFunction
    public final double getHeatingRate() {
        return getReactor().lastBoilRate;
    }

    @LuaFunction
    public final double getEnvironmentalLoss() {
        return getReactor().lastEnvironmentLoss;
    }

    @LuaFunction
    public final double getTemperature() {
        return getReactor().getTotalTemperature();
    }

    @LuaFunction
    public final double getHeatCapacity() {
        return getReactor().heatCapacitor.getHeatCapacity();
    }

    @LuaFunction
    public final int getFuelAssemblies() {
        return getReactor().fuelAssemblies;
    }

    @LuaFunction
    public final int getFuelSurfaceArea() {
        return getReactor().surfaceArea;
    }

    @LuaFunction
    public final double getBoilEfficiency() {
        return getReactor().getBoilEfficiency();
    }

    private FissionReactorMultiblockData getReactor() {
        return getTileEntity().getMultiblock();
    }
}
