package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;

public class AutomataCorePlugin implements IPeripheralPlugin {

    protected final AutomataCorePeripheral automataCore;

    public AutomataCorePlugin(AutomataCorePeripheral automataCore) {
        this.automataCore = automataCore;
    }
}
