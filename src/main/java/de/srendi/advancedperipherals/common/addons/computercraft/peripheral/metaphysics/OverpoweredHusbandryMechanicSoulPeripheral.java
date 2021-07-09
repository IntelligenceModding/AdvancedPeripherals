package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class OverpoweredHusbandryMechanicSoulPeripheral extends HusbandryMechanicSoulPeripheral {
    public OverpoweredHusbandryMechanicSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    protected boolean restoreToolDurability() {
        return true;
    }

    @NotNull
    @Override
    protected @Nonnull MethodResult fuelErrorCallback(@Nonnull IComputerAccess access,@Nonnull MethodResult fuelErrorResult) {
        Pair<MethodResult, TurtleSide> sidePair = getTurtleSide(access);
        if (sidePair.leftPresent())
            return fuelErrorResult;
        turtle.setUpgrade(sidePair.getRight(), null);
        return MethodResult.of(null, "Too much power! Soul is broken ...");
    }
}
