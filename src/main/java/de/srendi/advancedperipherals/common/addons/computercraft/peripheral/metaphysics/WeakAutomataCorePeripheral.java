package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.*;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.metaphysics.IAutomataCoreTier;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;

public class WeakAutomataCorePeripheral extends AutomataCorePeripheral {
    public static final String TYPE = "weakAutomata";

    public WeakAutomataCorePeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(TYPE, turtle, side, AutomataCoreTier.TIER1);
    }

    protected WeakAutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side, IAutomataCoreTier tier) {
        super(type, turtle, side, tier);
        addPlugin(new AutomataItemSuckPlugin(this));
        addPlugin(new AutomataLookPlugin(this));
        addPlugin(new AutomataBlockHandPlugin(this));
        addPlugin(new AutomataSoulFeedingPlugin(this));
        addPlugin(new AutomataChargingPlugin(this));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.METAPHYSICS_CONFIG.enableWeakAutomataCore.get();
    }
}
