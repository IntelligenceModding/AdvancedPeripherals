package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IPeripheralTileEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.base.TileEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class OperationPeripheral extends BasePeripheral {

    private final Map<IPeripheralOperation<?>, Timestamp> targetOperationTimestamp = new HashMap<>();
    private final Map<String, IPeripheralOperation<?>> allowedOperations;

    public <T extends TileEntity & IPeripheralTileEntity> OperationPeripheral(String type, T tileEntity) {
        super(type, tileEntity);
        this.allowedOperations = buildAllowedOperations();
    }

    public OperationPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
        this.allowedOperations = buildAllowedOperations();
    }

    public OperationPeripheral(String type, IPocketAccess pocket) {
        super(type, pocket);
        this.allowedOperations = buildAllowedOperations();
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
            return Optional.of(MethodResult.of(null, String.format("%s is on cooldown", operation.settingsName())));
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

    public abstract List<IPeripheralOperation<?>> possibleOperations();

    private Map<String, IPeripheralOperation<?>> buildAllowedOperations() {
        Map<String, IPeripheralOperation<?>> allowedOperations = new HashMap<>();
        possibleOperations().forEach(op -> allowedOperations.put(op.settingsName(), op));
        return allowedOperations;
    }

    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> data = new HashMap<>();
        for (IPeripheralOperation<?> operation: allowedOperations.values()) {
            data.put(operation.settingsName(), operation.computerDescription());
        }
        return data;
    }

    @LuaFunction
    public final Map<String, Object> getConfiguration() {
        return getPeripheralConfiguration();
    }

    @LuaFunction
    public final MethodResult getOperationCooldown(String name) {
        IPeripheralOperation<?> op = allowedOperations.get(name);
        if (op == null)
            return MethodResult.of(null, "Cannot find this operation");
        return MethodResult.of(getCurrentCooldown(op));
    }
}
