package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.lua.MethodResult;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IPeripheralCheck<T> {

    @Nullable MethodResult check(T context);

    default IPeripheralCheck<T> thenCheck(IPeripheralCheck<T> other) {
        return (context) -> {
            MethodResult result = this.check(context);
            if (result == null) {
                result = other.check(context);
            }
            return result;
        };
    }
}
