package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.lua.MethodResult;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IPeripheralCheck<T> {

    @Nullable MethodResult check(T context);

    default IPeripheralCheck<T> checkAlso(IPeripheralCheck<T> check) {
        return new ChainedPeripheralCheck<>(this, check);
    }

    class ChainedPeripheralCheck<T1> implements IPeripheralCheck<T1> {
        private final IPeripheralCheck<T1> first;
        private final IPeripheralCheck<T1> second;

        private ChainedPeripheralCheck(IPeripheralCheck<T1> first, IPeripheralCheck<T1> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public @Nullable MethodResult check(T1 context) {
            MethodResult firstCheck = first.check(context);
            if (firstCheck != null) return firstCheck;
            return second.check(context);
        }
    }
}
