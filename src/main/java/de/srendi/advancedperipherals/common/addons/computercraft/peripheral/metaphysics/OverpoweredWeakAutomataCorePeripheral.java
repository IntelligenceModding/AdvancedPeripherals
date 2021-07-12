package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class OverpoweredWeakAutomataCorePeripheral extends WeakAutomataCorePeripheral {
    public OverpoweredWeakAutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    @Override
    protected boolean restoreToolDurability() {
        return true;
    }

    @NotNull
    @Override
    protected @Nonnull MethodResult fuelErrorCallback(@Nonnull MethodResult fuelErrorResult) {
        owner.destroyUpgrade();
        return MethodResult.of(null, "Too much power! Soul is broken ...");
    }
}
