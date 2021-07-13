package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;

public class DisabledPeripheral extends BasePeripheral {
    public final static DisabledPeripheral INSTANCE = new DisabledPeripheral("disabledPeripheral", null, TurtleSide.LEFT);

    public DisabledPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
