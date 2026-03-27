package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;

public class OverpoweredEndAutomataCorePeripheral extends EndAutomataCorePeripheral {

    public static final String TYPE = "overpowered_end_automata";

    public OverpoweredEndAutomataCorePeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(TYPE, turtle, side, AutomataCoreTier.OVERPOWERED_TIER2);
    }

    @Override
    public boolean canActiveOverpower() {
        return true;
    }
}
