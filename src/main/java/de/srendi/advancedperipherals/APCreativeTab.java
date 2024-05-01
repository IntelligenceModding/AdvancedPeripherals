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
package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class APCreativeTab extends CreativeModeTab {

    public APCreativeTab() {
        super("advancedperipheralstab");
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> items) {
        APRegistration.ITEMS.getEntries().stream().map(RegistryObject::get)
                .forEach(item -> items.add(new ItemStack(item)));
        items.addAll(pocketUpgrade(CCRegistration.ID.COLONY_POCKET));
        items.addAll(pocketUpgrade(CCRegistration.ID.CHATTY_POCKET));
        items.addAll(pocketUpgrade(CCRegistration.ID.PLAYER_POCKET));
        items.addAll(pocketUpgrade(CCRegistration.ID.ENVIRONMENT_POCKET));
        items.addAll(pocketUpgrade(CCRegistration.ID.GEOSCANNER_POCKET));

        items.addAll(turtleUpgrade(CCRegistration.ID.CHATTY_TURTLE));
        items.addAll(turtleUpgrade(CCRegistration.ID.CHUNKY_TURTLE));
        items.addAll(turtleUpgrade(CCRegistration.ID.COMPASS_TURTLE));
        items.addAll(turtleUpgrade(CCRegistration.ID.PLAYER_TURTLE));
        items.addAll(turtleUpgrade(CCRegistration.ID.ENVIRONMENT_TURTLE));
        items.addAll(turtleUpgrade(CCRegistration.ID.GEOSCANNER_TURTLE));

        items.addAll(turtleUpgrade(CCRegistration.ID.WEAK_AUTOMATA));
        items.addAll(turtleUpgrade(CCRegistration.ID.OP_WEAK_AUTOMATA));
        items.addAll(turtleUpgrade(CCRegistration.ID.HUSBANDRY_AUTOMATA));
        items.addAll(turtleUpgrade(CCRegistration.ID.OP_HUSBANDRY_AUTOMATA));
        items.addAll(turtleUpgrade(CCRegistration.ID.END_AUTOMATA));
        items.addAll(turtleUpgrade(CCRegistration.ID.OP_END_AUTOMATA));
    }

    private static Collection<ItemStack> pocketUpgrade(ResourceLocation pocketId) {
        return Set.of(ItemUtil.makePocket(ItemUtil.POCKET_NORMAL, pocketId.toString()),
                ItemUtil.makePocket(ItemUtil.POCKET_ADVANCED, pocketId.toString()));
    }

    private static Collection<ItemStack> turtleUpgrade(ResourceLocation pocketId) {
        return Set.of(ItemUtil.makeTurtle(ItemUtil.TURTLE_NORMAL, pocketId.toString()),
                ItemUtil.makeTurtle(ItemUtil.TURTLE_ADVANCED, pocketId.toString()));
    }

    @Override
    @NotNull public ItemStack makeIcon() {
        return new ItemStack(APBlocks.CHAT_BOX.get());
    }
}
