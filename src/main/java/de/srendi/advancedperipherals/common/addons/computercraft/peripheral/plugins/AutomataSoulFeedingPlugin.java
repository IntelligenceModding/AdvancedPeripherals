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
package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.WeakAutomataCorePeripheral;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.metaphysics.IFeedableAutomataCore;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import net.minecraft.world.InteractionResult;

public class AutomataSoulFeedingPlugin extends AutomataCorePlugin {

    public AutomataSoulFeedingPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult feedSoul() {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        if (!(owner.getToolInMainHand().getItem() instanceof IFeedableAutomataCore))
            return MethodResult.of(null, "Well, you should feed correct mechanical soul!");

        InteractionResult result = owner.withPlayer(APFakePlayer::useOnEntity);
        automataCore.addRotationCycle(3);
        return MethodResult.of(true, result.toString());
    }

    @Override
    public boolean isSuitable(IPeripheral peripheral) {
        return peripheral.getClass() == WeakAutomataCorePeripheral.class;
    }
}
