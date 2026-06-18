package de.srendi.advancedperipherals.common.smartglasses.modules;

import dan200.computercraft.shared.computer.core.ServerContext;
import de.srendi.advancedperipherals.lib.peripherals.BoundMethod;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

/**
 * Used to define the available functions of the module.
 * Functions can be defined by creating final public methods annotated with the @{@link dan200.computercraft.api.lua.LuaFunction} annotation
 */
public interface IModuleFunctions {
    default List<BoundMethod> getMethods() {
        return ServerContext.get(ServerLifecycleHooks.getCurrentServer())
            .peripheralMethods()
            .getSelfMethods(this)
            .entrySet()
            .stream()
            .map(entry -> new BoundMethod(this, entry.getKey(), entry.getValue()))
            .toList();
    }
}
