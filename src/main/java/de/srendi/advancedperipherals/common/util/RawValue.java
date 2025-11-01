package de.srendi.advancedperipherals.common.util;

import com.google.gson.JsonObject;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * Since Minecolonies/DO uses a library for data generation which we don't have implemented with which I really don't want to deal,
 * we need to generate our recipe without minecolonies in the runtime.
 * <p>
 * So we have an Ingredient value which can be parsed to json without the item being registered.
 */
public class RawValue implements Ingredient.Value {
    private final ResourceLocation item;

    public RawValue(ResourceLocation pItem) {
        this.item = pItem;
    }

    @NotNull
    public Collection<ItemStack> getItems() {
        return Collections.singleton(new ItemStack(ItemUtil.getRegistryEntry(this.item.toString(), BuiltInRegistries.ITEM)));
    }

    @NotNull
    public JsonObject serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", item.toString());
        return jsonobject;
    }
}

