package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.lib.LibConfig;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralCheck;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralFunction;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class OperationAbility implements IOwnerAbility, IPeripheralPlugin {
    private final Map<String, IPeripheralOperation<?>> allowedOperations = new HashMap<>();
    private final IPeripheralOwner owner;

    private static final String COOLDOWNS_TAG = "cooldowns";

    public OperationAbility(IPeripheralOwner owner) {
        this.owner = owner;
    }

    protected void setCooldown(@NotNull IPeripheralOperation<?> operation, int cooldown) {
        PatchedDataComponentMap patch = owner.getPatchedDataStorage();
        CompoundTag cooldowns = patch.get(APDataComponents.ABILITY_COOLDOWNS.get());
        if (cooldowns == null) {
            cooldowns = new CompoundTag();
            patch.set(APDataComponents.ABILITY_COOLDOWNS.get(), cooldowns);
        }

        long newTS = Timestamp.valueOf(LocalDateTime.now().plus(cooldown, ChronoUnit.MILLIS)).getTime();
        cooldowns.putLong(operation.settingsName(), newTS);
        owner.putDataStorage(patch.asPatch());
    }

    protected int getCooldown(@NotNull IPeripheralOperation<?> operation) {
        PatchedDataComponentMap patch = owner.getPatchedDataStorage();
        CompoundTag cooldowns = patch.get(APDataComponents.ABILITY_COOLDOWNS.get());
        String operationName = operation.settingsName();
        if (cooldowns == null || !cooldowns.contains(operationName)) {
            return 0;
        }

        long currentTime = Timestamp.valueOf(LocalDateTime.now()).getTime();
        return (int) Math.max(0, cooldowns.getLong(operationName) - currentTime);
    }

    public void registerOperation(@NotNull IPeripheralOperation<?> operation) {
        allowedOperations.put(operation.settingsName(), operation);
        if (!LibConfig.initialCooldownEnabled) {
            return;
        }
        PatchedDataComponentMap patch = owner.getPatchedDataStorage();
        CompoundTag cooldowns = patch.get(APDataComponents.ABILITY_COOLDOWNS.get());
        if (cooldowns != null && cooldowns.contains(operation.settingsName())) {
            return;
        }
        int initialCooldown = operation.getInitialCooldown();
        if (initialCooldown < LibConfig.initialCooldownSensitivity) {
            return;
        }
        long newTS = Timestamp.valueOf(LocalDateTime.now().plus(initialCooldown, ChronoUnit.MILLIS)).getTime();
        if (cooldowns == null) {
            cooldowns = new CompoundTag();
            patch.set(APDataComponents.ABILITY_COOLDOWNS.get(), cooldowns);
        }
        cooldowns.putLong(operation.settingsName(), newTS);
        owner.putDataStorage(patch.asPatch());
    }

    public <T> @NotNull MethodResult performOperation(
        IPeripheralOperation<T> operation,
        T context,
        @Nullable IPeripheralCheck<T> check,
        IPeripheralFunction<T, MethodResult> method,
        @Nullable IPeripheralOperation.Successor<T> successCallback,
        @Nullable IPeripheralOperation.Failer failCallback
    ) throws LuaException {
        if (isOnCooldown(operation)) {
            MethodResult result = MethodResult.of(null, String.format("%s is on cooldown", operation.settingsName()));
            if (failCallback != null) {
                failCallback.accept(result, FailReason.COOLDOWN);
            }
            return result;
        }
        if (check != null) {
            MethodResult checkResult = check.check(context);
            if (checkResult != null) {
                if (failCallback != null) {
                    failCallback.accept(checkResult, FailReason.CHECK_FAILED);
                }
                return checkResult;
            }
        }
        int cost = operation.getCost(context);
        int cooldown = operation.getCooldown(context);
        FuelAbility<?> fuelAbility;
        if (cost != 0) {
            fuelAbility = owner.getAbility(PeripheralOwnerAbility.FUEL);
            String errorMsg = null;
            if (fuelAbility == null) {
                errorMsg = "This peripheral has no fuel at all";
            } else if (!fuelAbility.consumeFuel(cost, false)) {
                errorMsg = "Not enough fuel for operation";
            }
            if (errorMsg != null) {
                MethodResult result = MethodResult.of(null, errorMsg);
                if (failCallback != null) {
                    failCallback.accept(result, FailReason.NOT_ENOUGH_FUEL);
                }
                return result;
            }
            cooldown = fuelAbility.reduceCooldownAccordingToConsumptionRate(cooldown);
        }

        MethodResult result = method.apply(context);
        if (successCallback != null) {
            successCallback.accept(context);
        }

        setCooldown(operation, cooldown);
        return result;
    }

    public int getCurrentCooldown(IPeripheralOperation<?> operation) {
        return getCooldown(operation);
    }

    public boolean isOnCooldown(IPeripheralOperation<?> operation) {
        return getCurrentCooldown(operation) > 0;
    }

    @Override
    public void collectConfiguration(Map<String, Object> dict) {
        for (IPeripheralOperation<?> operation : allowedOperations.values()) {
            dict.put(operation.settingsName(), operation.computerDescription());
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getOperationCooldown(String name) {
        IPeripheralOperation<?> op = allowedOperations.get(name);
        if (op == null) {
            return MethodResult.of(null, "Cannot find this operation");
        }
        return MethodResult.of(getCurrentCooldown(op));
    }

    public enum FailReason {
        COOLDOWN, NOT_ENOUGH_FUEL, CHECK_FAILED
    }
}
