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
package de.srendi.advancedperipherals.common.util;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * Since Minecolonies/DO uses a library for data generation which we don't have
 * implemented with which I really don't want to deal, we need to generate our
 * recipe without minecolonies in the runtime.
 * <p>
 * So we have an Ingredient value which can be parsed to json without the item
 * being registered.
 */
public class RawValue implements Ingredient.Value {
    private final ResourceLocation item;

    public RawValue(ResourceLocation pItem) {
        this.item = pItem;
    }

    @NotNull public Collection<ItemStack> getItems() {
        return Collections.singleton(new ItemStack(ForgeRegistries.ITEMS.getValue(this.item)));
    }

    @NotNull public JsonObject serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", item.toString());
        return jsonobject;
    }
}
