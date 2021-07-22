package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import de.srendi.advancedperipherals.common.addons.computercraft.base.IConfigHandler;

import java.util.Map;

public interface IPeripheralOperation<T> extends IConfigHandler {
    int getCooldown(T context);
    int getCost(T context);
    Map<String, Object> computerDescription();
}
