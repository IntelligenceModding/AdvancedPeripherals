package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.util.List;

public interface IBasePeripheral extends IPeripheral {
    boolean isEnabled();
    List<IComputerAccess> getConnectedComputers();
}
