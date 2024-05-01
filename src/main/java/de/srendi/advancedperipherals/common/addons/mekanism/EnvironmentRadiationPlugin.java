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
package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;

public class EnvironmentRadiationPlugin implements IPeripheralPlugin {
    private final IPeripheralOwner owner;

    public EnvironmentRadiationPlugin(IPeripheralOwner owner) {
        this.owner = owner;
    }

    @LuaFunction(mainThread = true)
    public final Object getRadiation() {
        return Mekanism.getRadiation(owner.getLevel(), owner.getPos());
    }

    @LuaFunction(mainThread = true)
    public final double getRadiationRaw() {
        return Mekanism.getRadiationRaw(owner.getLevel(), owner.getPos());
    }

}
