package de.srendi.advancedperipherals.common.addons.computercraft.base;

public interface IPeripheralOperation<T> extends IConfigHandler {
    int getCooldown(T context);
    int getCost(T context);
}
