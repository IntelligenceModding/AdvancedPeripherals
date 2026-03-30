package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.OperationAbility.FailReason;
import de.srendi.advancedperipherals.lib.misc.IConfigHandler;

import java.util.Map;

public interface IPeripheralOperation<T> extends IConfigHandler {
    int getInitialCooldown();

    int getCooldown(T context);

    int getCost(T context);

    MethodResult getCostLua(IArguments args) throws LuaException;

    Map<String, Object> computerDescription();

    @FunctionalInterface
    interface Successor<T> {
        void accept(T context);
    }

    @FunctionalInterface
    interface Failer {
        void accept(MethodResult result, FailReason reason);
    }
}
