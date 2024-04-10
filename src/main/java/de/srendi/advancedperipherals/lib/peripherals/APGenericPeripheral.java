package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import de.srendi.advancedperipherals.AdvancedPeripherals;

public interface APGenericPeripheral extends GenericPeripheral {
    String getPeripheralType();

    @Override
    default String id() {
        return AdvancedPeripherals.MOD_ID + ":" + getPeripheralType();
    }

    @Override
    default PeripheralType getType() {
        return PeripheralType.ofType(getPeripheralType());
    }
}
