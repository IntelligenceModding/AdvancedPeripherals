package de.srendi.advancedperipherals.api.peripherals.owner;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.api.peripherals.*;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OperationAbility implements IOwnerAbility, IPeripheralPlugin {
    private final static String COOLDOWNS_TAG = "cooldowns";

    private final Map<String, IPeripheralOperation<?>> allowedOperations = new HashMap<>();
    private final IPeripheralOwner owner;

    public OperationAbility(IPeripheralOwner owner) {
        this.owner = owner;
    }

    protected void setCooldown(@NotNull IPeripheralOperation<?> operation, int cooldown) {
        if (cooldown > 0) {
            CompoundNBT dataStorage = owner.getDataStorage();
            if (!dataStorage.contains(COOLDOWNS_TAG))
                dataStorage.put(COOLDOWNS_TAG, new CompoundNBT());
            dataStorage.getCompound(COOLDOWNS_TAG).putInt(operation.settingsName(), Timestamp.valueOf(LocalDateTime.now().plus(cooldown, ChronoUnit.MILLIS)).getNanos());
        }
    }

    protected int getCooldown(@NotNull IPeripheralOperation<?> operation) {
        CompoundNBT dataStorage = owner.getDataStorage();
        if (!dataStorage.contains(COOLDOWNS_TAG))
            return 0;
        CompoundNBT cooldowns = dataStorage.getCompound(COOLDOWNS_TAG);
        String operationName = operation.settingsName();
        if (!cooldowns.contains(operationName))
            return 0;
        int currentNanos = Timestamp.valueOf(LocalDateTime.now()).getNanos();
        return Math.max(0, cooldowns.getInt(operationName) - currentNanos);
    }

    public void registerOperation(@NotNull IPeripheralOperation<?> operation) {
        allowedOperations.put(operation.settingsName(), operation);
        setCooldown(operation, operation.getInitialCooldown());
    }

    public <T> @NotNull MethodResult performOperation(IPeripheralOperation<T> operation, T context, @Nullable IPeripheralCheck<T> check, IPeripheralFunction<T, MethodResult> method, @Nullable Consumer<T> successCallback) throws LuaException {
        if (isOnCooldown(operation))
            return MethodResult.of(null, String.format("%s is on cooldown", operation.settingsName()));
        if (check != null) {
            MethodResult checkResult = check.check(context);
            if (checkResult != null)
                return checkResult;
        }
        int cost = operation.getCost(context);
        int cooldown = operation.getCooldown(context);
        FuelAbility<?> fuelAbility;
        if (cost != 0) {
            fuelAbility = owner.getAbility(PeripheralOwnerAbility.FUEL);
            if (fuelAbility == null)
                return MethodResult.of(null, "This peripheral has no fuel at all");
            if (!fuelAbility.consumeFuel(cost, false))
                return MethodResult.of(null, "Not enough fuel for operation");
            cooldown = fuelAbility.reduceCooldownAccordingToConsumptionRate(cooldown);
        }
        MethodResult result = method.apply(context);
        if (successCallback != null)
            successCallback.accept(context);
        setCooldown(operation, cooldown);
        return result;
    }

    public int getCurrentCooldown(IPeripheralOperation<?> operation) {
        return getCooldown(operation);
    }

    public boolean isOnCooldown(IPeripheralOperation<?> operation) {
        return getCurrentCooldown(operation) <= 0;
    }

    @Override
    public void collectConfiguration(Map<String, Object> dict) {
        for (IPeripheralOperation<?> operation: allowedOperations.values()) {
            dict.put(operation.settingsName(), operation.computerDescription());
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getOperationCooldown(String name) {
        IPeripheralOperation<?> op = allowedOperations.get(name);
        if (op == null)
            return MethodResult.of(null, "Cannot find this operation");
        return MethodResult.of(getCurrentCooldown(op));
    }
}
