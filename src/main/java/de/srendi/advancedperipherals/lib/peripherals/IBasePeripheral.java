package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;

public interface IBasePeripheral<T extends IPeripheralOwner> extends IPeripheral {
    boolean isEnabled();

    Iterable<IComputerAccess> getConnectedComputers();

    default void queueEvent(String event, Object... args) {
        for (IComputerAccess computer : getConnectedComputers()) {
            computer.queueEvent(event, args);
        }
    }

    T getPeripheralOwner();
}
