package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class OverpoweredEndMechanicSoulPeripheral extends EndMechanicalSoulPeripheral {
    public OverpoweredEndMechanicSoulPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
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
