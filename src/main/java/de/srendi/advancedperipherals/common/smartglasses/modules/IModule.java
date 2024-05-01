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

import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface IModule {

    ResourceLocation getName();

    /**
     * Used to define the available functions of the module. This method only gets
     * called once when indexing the modules
     * <p>
     * Return null if the module does not have any functions
     *
     * @return an object containing lua functions
     *         {@link dan200.computercraft.api.lua.LuaFunction}
     */
    @Nullable IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess);

    /**
     * Classic tick function.
     * <p>
     * Implementations should check if the entity is not null since the glasses can
     * still tick without belonging to an entity
     * 
     * @param smartGlassesAccess
     *            Contains access to the entity, the computer, the level or the
     *            upgrades
     */
    default void tick(SmartGlassesAccess smartGlassesAccess) {
    }

    default void onUnequipped(SmartGlassesAccess smartGlassesAccess) {
    }

    /**
     * ErrorConstants class contains constants for error messages. This is used for
     * easier error handling for users.
     */
    class ErrorConstants {
        public static final String ALREADY_EXISTS = "ID_ALREADY_EXISTS";
    }

}
