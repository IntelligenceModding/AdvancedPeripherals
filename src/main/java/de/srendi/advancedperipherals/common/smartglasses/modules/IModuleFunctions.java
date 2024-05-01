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
package de.srendi.advancedperipherals.common.smartglasses.modules;

import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;

/**
 * Used to define the available functions of the module. Functions can be
 * defined by creating final public methods annotated with
 * the @{@link dan200.computercraft.api.lua.LuaFunction} annotation
 */
public interface IModuleFunctions extends IPeripheralPlugin {

    IModuleFunctions EMPTY = new IModuleFunctions() {
    };

}
