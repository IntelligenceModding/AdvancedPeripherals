package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataWarpingPlugin;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.metaphysics.IAutomataCoreTier;


public class EndAutomataCorePeripheral extends WeakAutomataCorePeripheral {

    public static final String TYPE = "end_automata";

    public EndAutomataCorePeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(TYPE, turtle, side, AutomataCoreTier.TIER2);
    }

    protected EndAutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side, IAutomataCoreTier tier) {
        super(type, turtle, side, tier);
        addPlugin(new AutomataWarpingPlugin(this));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.METAPHYSICS_CONFIG.enableEndAutomataCore.get();
    }
}
