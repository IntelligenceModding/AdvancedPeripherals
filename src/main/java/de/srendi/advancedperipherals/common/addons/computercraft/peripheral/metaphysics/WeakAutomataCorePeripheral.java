/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

public class WeakAutomataCorePeripheral extends AutomataCorePeripheral {
    public static final String TYPE = "weak_automata";

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
