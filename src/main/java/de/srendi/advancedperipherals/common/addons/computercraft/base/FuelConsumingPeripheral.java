package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;

import java.util.Map;
import java.util.Optional;

public abstract class FuelConsumingPeripheral extends OperationPeripheral {
    public FuelConsumingPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public FuelConsumingPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    public FuelConsumingPeripheral(String type, IPocketAccess pocket) {
        super(type, pocket);
    }

    protected abstract int getMaxFuelConsumptionRate();

    protected abstract int _getFuelConsumptionRate();

    protected abstract void _setFuelConsumptionRate(int rate);

    protected int fuelConsumptionMultiply() {
        return (int) Math.pow(2, _getFuelConsumptionRate() - 1);
    }

    public boolean fuelConsumptionDisabled() {
        return !ComputerCraft.turtlesNeedFuel;
    }

    public boolean consumeFuel(int count) {
        return consumeFuel(count, false);
    }

    public boolean consumeFuel(int count, boolean simulate) {
        if (fuelConsumptionDisabled())
            return true;
        count = count * fuelConsumptionMultiply();
        return owner.consumeFuel(count, simulate);
    }

    public Optional<MethodResult> consumeFuelOp(int count) {
        if (!consumeFuel(count))
            return Optional.of(MethodResult.of(null, String.format("Not enough fuel, %d needed", count)));
        return Optional.empty();
    }

    public <T> Optional<MethodResult> consumeFuelOp(IPeripheralOperation<T> operation, T context) {
        return consumeFuelOp(operation.getCost(context));
    }

    public void addFuel(int count) {
        if (fuelConsumptionDisabled())
            return;
        owner.addFuel(count);
    }

    @Override
    public <T> int getCooldown(IPeripheralOperation<T> operation, T context) {
        return operation.getCooldown(context) / _getFuelConsumptionRate();
    }

    @Override
    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> data = super.getPeripheralConfiguration();
        data.put("maxFuelConsumptionRate", getMaxFuelConsumptionRate());
        return data;
    }

    @LuaFunction(mainThread = true)
    public final int getFuelLevel() {
        return owner.getFuelCount();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxFuelLevel() {
        return owner.getFuelMaxCount();
    }

    @LuaFunction
    public final int getFuelConsumptionRate() {
        return _getFuelConsumptionRate();
    }

    @LuaFunction
    public final MethodResult setFuelConsumptionRate(int rate) {
        if (rate < 1)
            return MethodResult.of(null, "Too small fuel consumption rate");
        if (rate > getMaxFuelConsumptionRate())
            return MethodResult.of(null, "Too big fuel consumption rate");
        _setFuelConsumptionRate(rate);
        return MethodResult.of(true);
    }
}
