package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.computer.core.ServerContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IPeripheralPlugin {
    default List<BoundMethod> getMethods() {
        return ServerContext.get(ServerLifecycleHooks.getCurrentServer())
            .peripheralMethods()
            .getSelfMethods(this)
            .entrySet()
            .stream()
            .map(entry -> new BoundMethod(this, entry.getKey(), entry.getValue()))
            .toList();
    }

    default IPeripheralOperation<?> @Nullable [] getOperations() {
        return null;
    }

    default boolean isSuitable(IPeripheral peripheral) {
        return true;
    }
}
