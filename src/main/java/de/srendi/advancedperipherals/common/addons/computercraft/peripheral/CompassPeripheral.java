package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TurtlePeripheralOwner;

public class CompassPeripheral extends BasePeripheral<TurtlePeripheralOwner> {

    public static final String TYPE = "compass";

    public CompassPeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(TYPE, new TurtlePeripheralOwner(turtle, side));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.ENABLE_COMPASS_TURTLE.get();
    }

    @LuaFunction(mainThread = true)
    public String getFacing() {
        return owner.getFacing().toString();
    }
}
