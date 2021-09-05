package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.asm.PeripheralMethod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public interface IPeripheralPlugin {
    default List<BoundMethod> getMethods() {
        return PeripheralMethod.GENERATOR.getMethods(this.getClass()).stream().map(named -> new BoundMethod(this, named)).collect(Collectors.toList());
    }

    default @Nullable IPeripheralOperation<?>[] getOperations() {
        return null;
    }

    default boolean isSuitable(IPeripheral peripheral) {
        return true;
    }
}
