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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IModuleItem {

    IModule createModule(SmartGlassesAccess access);

    /**
     * This method is called every tick the item is in the inventory of the smart
     * glasses Runs on the client and server side
     *
     * @param access
     *            The access to the smart glasses - Null on the client side
     * @param module
     *            The module - Null on the client side
     */
    default void inventoryTick(ItemStack itemStack, Level level, Entity entity, int inventorySlot,
            boolean isCurrentItem, @Nullable SmartGlassesAccess access, @Nullable IModule module) {

    }

}
