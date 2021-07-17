package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class OperationPeripheral extends BasePeripheral {

    private final Map<IPeripheralOperation<?>, Timestamp> targetOperationTimestamp = new HashMap<>();

    public OperationPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public OperationPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    public OperationPeripheral(String type, IPocketAccess pocket) {
        super(type, pocket);
    }

    public <T> int getCooldown(IPeripheralOperation<T> operation, T context) {
        return operation.getCooldown(context);
    }

    public <T> void trackOperation(IPeripheralOperation<T> operation, T context) {
        trackOperation(operation, context, 1);
    }

    public <T> void trackOperation(IPeripheralOperation<T> operation, T context, int count) {
        targetOperationTimestamp.put(operation, Timestamp.valueOf(LocalDateTime.now().plus((long) count * getCooldown(operation, context), ChronoUnit.MILLIS)));
    }

    public Optional<MethodResult> cooldownCheck(IPeripheralOperation<?> operation) {
        if (isOnCooldown(operation))
            return Optional.of(MethodResult.of(null, String.format("%s is on cooldown", operation)));
        return Optional.empty();
    }

    public boolean isOnCooldown(IPeripheralOperation<?> operation) {
        Timestamp targetTimestamp = targetOperationTimestamp.get(operation);
        if (targetTimestamp == null) {
            return false;
        }
        return Timestamp.valueOf(LocalDateTime.now()).before(targetTimestamp);
    }

    public int getCurrentCooldown(IPeripheralOperation<?> operation) {
        Timestamp targetTimestamp = targetOperationTimestamp.get(operation);
        if (targetTimestamp == null) {
            return 0;
        }
        return (int) Math.max(0, targetTimestamp.getTime() - Timestamp.valueOf(LocalDateTime.now()).getTime());
    }

    public Map<String, Object> getPeripheralConfiguration() {
        return new HashMap<>();
    }

    @LuaFunction
    public final Map<String, Object> getConfiguration() {
        return getPeripheralConfiguration();
    }
}
