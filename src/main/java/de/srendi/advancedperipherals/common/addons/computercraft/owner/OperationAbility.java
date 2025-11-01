package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.lib.LibConfig;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralCheck;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralFunction;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static de.srendi.advancedperipherals.common.setup.DataComponents.ABILITY_COOLDOWN;

public class OperationAbility implements IOwnerAbility, IPeripheralPlugin {
    private final Map<String, IPeripheralOperation<?>> allowedOperations = new HashMap<>();
    private final IPeripheralOwner owner;

    private static final String COOLDOWNS_TAG = "cooldowns";

    public OperationAbility(IPeripheralOwner owner) {
        this.owner = owner;
    }

    protected void setCooldown(@NotNull IPeripheralOperation<?> operation, int cooldown) {
        if (cooldown > 0) {
            if (owner instanceof BlockEntityPeripheralOwner<?>) {
                CompoundTag dataStorage = owner.getNbtStorage();
                if (!dataStorage.contains(COOLDOWNS_TAG)) dataStorage.put(COOLDOWNS_TAG, new CompoundTag());
                dataStorage.getCompound(COOLDOWNS_TAG).putLong(operation.settingsName(), Timestamp.valueOf(LocalDateTime.now().plus(cooldown, ChronoUnit.MILLIS)).getTime());
            }

            PatchedDataComponentMap patch = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, owner.getDataStorage());
            if (!patch.has(ABILITY_COOLDOWN.get())) patch.set(ABILITY_COOLDOWN.get(), DataComponentPatch.EMPTY);

            PatchedDataComponentMap operationPatch = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, patch.get(ABILITY_COOLDOWN.get()));
            operationPatch.set(operation.dataComponentType(), (long) cooldown);

            patch.set(ABILITY_COOLDOWN.get(), operationPatch.asPatch());
            owner.putDataStorage(patch.asPatch());
        }
    }

    protected int getCooldown(@NotNull IPeripheralOperation<?> operation) {
        if (owner instanceof BlockEntityPeripheralOwner<?>) {
            CompoundTag dataStorage = owner.getNbtStorage();
            if (!dataStorage.contains(COOLDOWNS_TAG)) return 0;
            CompoundTag cooldowns = dataStorage.getCompound(COOLDOWNS_TAG);
            String operationName = operation.settingsName();
            if (!cooldowns.contains(operationName)) return 0;
            long currentTime = Timestamp.valueOf(LocalDateTime.now()).getTime();
            return (int) Math.max(0, cooldowns.getLong(operationName) - currentTime);
        }

        PatchedDataComponentMap patch = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, owner.getDataStorage());

        if (!patch.has(ABILITY_COOLDOWN.get())) {
            patch.set(ABILITY_COOLDOWN.get(), DataComponentPatch.EMPTY);
            owner.putDataStorage(patch.asPatch());
        }
        if (patch.get(ABILITY_COOLDOWN.get()).isEmpty()) return 0;

        DataComponentPatch cooldowns = patch.get(ABILITY_COOLDOWN.get());
        if (cooldowns.get(operation.dataComponentType()).isEmpty()) return 0;

        long currentTime = Timestamp.valueOf(LocalDateTime.now()).getTime();
        return (int) Math.max(0, cooldowns.get(operation.dataComponentType()).get() - currentTime);
    }

    public void registerOperation(@NotNull IPeripheralOperation<?> operation) {
        allowedOperations.put(operation.settingsName(), operation);
        if (LibConfig.initialCooldownEnabled) {
            int initialCooldown = operation.getInitialCooldown();
            if (initialCooldown >= LibConfig.initialCooldownSensitivity) setCooldown(operation, initialCooldown);
        }
    }

    public <T> @NotNull MethodResult performOperation(IPeripheralOperation<T> operation, T context, @Nullable IPeripheralCheck<T> check, IPeripheralFunction<T, MethodResult> method, @Nullable Consumer<T> successCallback, @Nullable BiConsumer<MethodResult, FailReason> failCallback) throws LuaException {
        if (isOnCooldown(operation)) {
            MethodResult result = MethodResult.of(null, String.format("%s is on cooldown", operation.settingsName()));
            if (failCallback != null) failCallback.accept(result, FailReason.COOLDOWN);
            return result;
        }
        if (check != null) {
            MethodResult checkResult = check.check(context);
            if (checkResult != null) {
                if (failCallback != null) failCallback.accept(checkResult, FailReason.CHECK_FAILED);
                return checkResult;
            }
        }
        int cost = operation.getCost(context);
        int cooldown = operation.getCooldown(context);
        FuelAbility<?> fuelAbility;
        if (cost != 0) {
            fuelAbility = owner.getAbility(PeripheralOwnerAbility.FUEL);
            if (fuelAbility == null) {
                MethodResult result = MethodResult.of(null, "This peripheral has no fuel at all");
                if (failCallback != null) failCallback.accept(result, FailReason.NOT_ENOUGH_FUEL);
                return result;
            }
            if (!fuelAbility.consumeFuel(cost, false)) {
                MethodResult result = MethodResult.of(null, "Not enough fuel for operation");
                if (failCallback != null) failCallback.accept(result, FailReason.NOT_ENOUGH_FUEL);
                return result;
            }
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
        if (op == null) return MethodResult.of(null, "Cannot find this operation");
        return MethodResult.of(getCurrentCooldown(op));
    }

    public enum FailReason {
        COOLDOWN, NOT_ENOUGH_FUEL, CHECK_FAILED
    }
}
