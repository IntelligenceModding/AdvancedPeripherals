package de.srendi.advancedperipherals.api.peripherals;

import dan200.computercraft.api.lua.LuaException;

@FunctionalInterface
public interface IPeripheralFunction<T, R> {
    R apply(T var1) throws LuaException;
}
