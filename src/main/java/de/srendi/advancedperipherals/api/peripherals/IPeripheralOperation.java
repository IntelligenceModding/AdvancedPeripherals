package de.srendi.advancedperipherals.api.peripherals;

import de.srendi.advancedperipherals.api.misc.IConfigHandler;

import java.util.Map;

public interface IPeripheralOperation<T> extends IConfigHandler {
    int getInitialCooldown();
    int getCooldown(T context);
    int getCost(T context);
    Map<String, Object> computerDescription();
}
