package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IAutomataCoreTier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


public class OverpoweredEndAutomataCorePeripheral extends EndAutomataCorePeripheral {
    public static final String TYPE = "overpoweredEndAutomata";

    public OverpoweredEndAutomataCorePeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(TYPE, turtle, side);
    }

    @Override
    protected boolean restoreToolDurability() {
        return true;
    }

    @Override
    public IAutomataCoreTier getTier() {
        return AutomataCoreTier.OVERPOWERED_TIER2;
    }

    @NotNull
    @Override
    public @Nonnull
    MethodResult fuelErrorCallback(@Nonnull MethodResult fuelErrorResult) {
        owner.destroyUpgrade();
        return MethodResult.of(null, "Too much power! Soul is broken ...");
    }
}
