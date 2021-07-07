package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class OperationPeripheral extends FuelConsumingPeripheral{

    private final Map<String, Timestamp> targetOperationTimestamp = new HashMap<>();

    public OperationPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public OperationPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    public OperationPeripheral(String type, Entity entity) {
        super(type, entity);
    }

    public OperationPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    protected int fuelConsumptionMultiply(@Nonnull IComputerAccess access) {
        return (int) Math.pow(2, getFuelConsumptionRate(access) - 1);
    }

    @Override
    public boolean consumeFuel(@Nonnull IComputerAccess access, int count, boolean simulate) {
        return super.consumeFuel(access, count * fuelConsumptionMultiply(access), simulate);
    }

    protected abstract int getRawCooldown(String name);

    protected abstract int getMaxFuelConsumptionRate();

    protected abstract int _getFuelConsumptionRate(@Nonnull IComputerAccess access);

    protected abstract void _setFuelConsumptionRate(@Nonnull IComputerAccess access, int rate);

    public int getCooldown(@Nonnull IComputerAccess access, String name) {
        return getRawCooldown(name) / _getFuelConsumptionRate(access);
    }

    public void trackOperation(@Nonnull IComputerAccess access, String name) {
        targetOperationTimestamp.put(name, Timestamp.valueOf(LocalDateTime.now().plus(getCooldown(access, name), ChronoUnit.MILLIS)));
    }

    public Optional<MethodResult> cooldownCheck(String name){
        if (isOnCooldown(name))
            return Optional.of(MethodResult.of(null, String.format("%s is on cooldown", name)));
        return Optional.empty();
    }

    public boolean isOnCooldown(String name) {
        Timestamp targetTimestamp = targetOperationTimestamp.get(name);
        if (targetTimestamp == null) {
            return false;
        }
        return Timestamp.valueOf(LocalDateTime.now()).before(targetTimestamp);
    }

    public int getCurrentCooldown(String name) {
        Timestamp targetTimestamp = targetOperationTimestamp.get(name);
        if (targetTimestamp == null) {
            return 0;
        }
        return (int) Math.max(0, targetTimestamp.getTime() - Timestamp.valueOf(LocalDateTime.now()).getTime());
    }

    @LuaFunction
    public final int getFuelConsumptionRate(@Nonnull IComputerAccess access) {
        return _getFuelConsumptionRate(access);
    }

    @LuaFunction
    public Map<String, Object> getConfiguration() {
        Map<String, Object> data = new HashMap<>();
        data.put("maxFuelConsumptionRate", getMaxFuelConsumptionRate());
        return data;
    }

    @LuaFunction
    public final MethodResult setFuelConsumptionRate(@Nonnull IComputerAccess access, @Nonnull IArguments arguments) throws LuaException {
        int rate = arguments.getInt(0);
        int maxConsumptionRate = getMaxFuelConsumptionRate();
        if (rate > maxConsumptionRate)
            return MethodResult.of(null, String.format("Rate is bigger than max allowed %d", maxConsumptionRate));
        if (rate < 1)
            return MethodResult.of(null, "Consumption rate cannot be lower than 1");
        _setFuelConsumptionRate(access, rate);
        return MethodResult.of(true);
    }
}
