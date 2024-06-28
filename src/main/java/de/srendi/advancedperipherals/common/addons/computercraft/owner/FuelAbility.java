package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class FuelAbility<T extends IPeripheralOwner> implements IOwnerAbility, IPeripheralPlugin {
    protected static final String FUEL_CONSUMING_RATE_SETTING = "FUEL_CONSUMING_RATE";
    protected static final int DEFAULT_FUEL_CONSUMING_RATE = 1;

    protected @NotNull T owner;

    protected FuelAbility(@NotNull T owner) {
        this.owner = owner;
    }

    protected abstract boolean consumeFuel(int count);

    protected abstract int getMaxFuelConsumptionRate();

    /**
     * @return the fuel consumption rate
     */
    protected int getConsumptionRate() {
        CompoundTag settings = owner.getDataStorage();
        int rate = settings.getInt(FUEL_CONSUMING_RATE_SETTING);
        if (rate == 0) {
            setConsumptionRate(DEFAULT_FUEL_CONSUMING_RATE);
            return DEFAULT_FUEL_CONSUMING_RATE;
        }
        return rate;
    }

    /**
     * Sets the fuel consumption rate
     *
     * @param rate the new fuel consumption rate
     */
    protected void setConsumptionRate(int rate) {
        if (rate < DEFAULT_FUEL_CONSUMING_RATE) rate = DEFAULT_FUEL_CONSUMING_RATE;
        int maxFuelRate = getMaxFuelConsumptionRate();
        if (rate > maxFuelRate) rate = maxFuelRate;
        owner.getDataStorage().putInt(FUEL_CONSUMING_RATE_SETTING, rate);
    }

    public abstract boolean isFuelConsumptionDisable();

    public abstract int getFuelCount();

    public abstract int getFuelMaxCount();

    public abstract void addFuel(int count);

    public int getFuelConsumptionMultiply() {
        return (int) Math.pow(2, getConsumptionRate() - 1f);
    }

    public int reduceCooldownAccordingToConsumptionRate(int cooldown) {
        return cooldown / getConsumptionRate();
    }

    public boolean consumeFuel(int count, boolean simulate) {
        if (isFuelConsumptionDisable()) return true;
        int realCount = count * getFuelConsumptionMultiply();
        if (simulate) return getFuelLevel() >= realCount;
        return consumeFuel(realCount);
    }

    @LuaFunction(mainThread = true)
    public final int getFuelLevel() {
        return getFuelCount();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxFuelLevel() {
        return getFuelMaxCount();
    }

    @LuaFunction(mainThread = true)
    public final int getFuelConsumptionRate() {
        return getConsumptionRate();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult setFuelConsumptionRate(int rate) {
        if (rate < 1) return MethodResult.of(null, "Too small fuel consumption rate");
        if (rate > getMaxFuelConsumptionRate()) return MethodResult.of(null, "Too big fuel consumption rate");
        setConsumptionRate(rate);
        return MethodResult.of(true);
    }

    @Override
    public void collectConfiguration(Map<String, Object> dict) {
        dict.put("maxFuelConsumptionRate", getMaxFuelConsumptionRate());
    }
}
