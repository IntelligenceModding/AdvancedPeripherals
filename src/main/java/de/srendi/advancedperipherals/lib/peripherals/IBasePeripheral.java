package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;

import java.util.function.Consumer;

public interface IBasePeripheral<T extends IPeripheralOwner> extends IPeripheral {
    boolean isEnabled();

    void forEachConnectedComputers(Consumer<? super IComputerAccess> consumer);

    default void queueEvent(String event, Object... args) {
        forEachConnectedComputers((computer) -> computer.queueEvent(event, args));
    }

    T getPeripheralOwner();

    default void update() {}
}
