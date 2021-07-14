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

    private final Map<String, Timestamp> targetOperationTimestamp = new HashMap<>();

    public OperationPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public OperationPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    public OperationPeripheral(String type, IPocketAccess pocket) {
        super(type, pocket);
    }

    protected abstract int getRawCooldown(String name);

    public int getCooldown(String name) {
        return getRawCooldown(name);
    }

    public void trackOperation(String name) {
        trackOperation(name, 1);
    }

    public void trackOperation(String name, int count) {
        targetOperationTimestamp.put(name, Timestamp.valueOf(LocalDateTime.now().plus((long) count * getCooldown(name), ChronoUnit.MILLIS)));
    }

    public Optional<MethodResult> cooldownCheck(String name) {
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

    public Map<String, Object> getPeripheralConfiguration() {
        return new HashMap<>();
    }

    @LuaFunction
    public final Map<String, Object> getConfiguration() {
        return getPeripheralConfiguration();
    }
}
