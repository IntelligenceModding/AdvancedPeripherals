package de.srendi.advancedperipherals.api.peripherals;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.api.peripherals.owner.IPeripheralOwner;

import java.util.List;

public interface IBasePeripheral<T extends IPeripheralOwner> extends IPeripheral {
    boolean isEnabled();
    List<IComputerAccess> getConnectedComputers();
    T getPeripheralOwner();
}
