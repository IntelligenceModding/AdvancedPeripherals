package de.srendi.advancedperipherals.common.smartglasses.modules;

import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;

/**
 * Used to define the available functions of the module.
 * Functions can be defined by creating final public methods annotated with the @{@link dan200.computercraft.api.lua.LuaFunction} annotation
 */
public interface IModuleFunctions extends IPeripheralPlugin {

    IModuleFunctions EMPTY = new IModuleFunctions() {};

}
