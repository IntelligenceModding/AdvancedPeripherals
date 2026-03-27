package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCoreTier;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataBlockHandPlugin;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataChargingPlugin;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataItemSuckPlugin;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataLookPlugin;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins.AutomataSoulFeedingPlugin;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.metaphysics.IAutomataCoreTier;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class WeakAutomataCorePeripheral extends AutomataCorePeripheral {
    public static final String TYPE = "weak_automata";
    private static final List<Function<AutomataCorePeripheral, IPeripheralPlugin>> PERIPHERAL_PLUGINS = new ArrayList<>();

    static {
        addIntegrationPlugin(AutomataItemSuckPlugin::new);
        addIntegrationPlugin(AutomataLookPlugin::new);
        addIntegrationPlugin(AutomataBlockHandPlugin::new);
        addIntegrationPlugin(AutomataSoulFeedingPlugin::new);
        addIntegrationPlugin(AutomataChargingPlugin::new);
    }

    public WeakAutomataCorePeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(TYPE, turtle, side, AutomataCoreTier.TIER1);
    }

    protected WeakAutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side, IAutomataCoreTier tier) {
        super(type, turtle, side, tier);
        for (Function<AutomataCorePeripheral, IPeripheralPlugin> plugin : PERIPHERAL_PLUGINS) {
            addPlugin(plugin.apply(this));
        }
    }

    public static void addIntegrationPlugin(Function<AutomataCorePeripheral, IPeripheralPlugin> plugin) {
        PERIPHERAL_PLUGINS.add(plugin);
    }

    @Override
    public boolean isEnabled() {
        return APConfig.METAPHYSICS_CONFIG.enableWeakAutomataCore.get();
    }

    @Override
    public double getBreakChance() {
        return APConfig.METAPHYSICS_CONFIG.overpoweredAutomataBreakChance.get();
    }
}
