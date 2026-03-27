package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;

public class OverpoweredWeakAutomataCorePeripheral extends WeakAutomataCorePeripheral {

    public static final String TYPE = "overpowered_weak_automata";

    public OverpoweredWeakAutomataCorePeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(TYPE, turtle, side, AutomataCoreTier.OVERPOWERED_TIER1);
    }

    @Override
    public boolean canActiveOverpower() {
        return true;
    }
}
