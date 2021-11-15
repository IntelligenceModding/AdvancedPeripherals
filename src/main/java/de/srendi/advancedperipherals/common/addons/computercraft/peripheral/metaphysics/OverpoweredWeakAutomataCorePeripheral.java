package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.configuration.GeneralConfig;

import java.util.Random;


public class OverpoweredWeakAutomataCorePeripheral extends WeakAutomataCorePeripheral {

    public static final String TYPE = "overpoweredWeakAutomata";

    public OverpoweredWeakAutomataCorePeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(TYPE, turtle, side, AutomataCoreTier.OVERPOWERED_TIER1);
        setAttribute(ATTR_STORING_TOOL_DURABILITY);
    }

    public void addRotationCycle(int count) {
        super.addRotationCycle(count);
        if (new Random().nextDouble() <= APConfig.METAPHYSICS_CONFIG.OVERPOWERED_AUTOMATA_BREAK_CHANCE.get())
            owner.destroyUpgrade();
    }
}
