package de.srendi.advancedperipherals.common.util;

import appeng.api.storage.MEStorage;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class ItemFilter {

    private Item item = Items.AIR;
    private TagKey<Item> tag = null;
    private CompoundTag nbt = null;
    private int count = -1;
    private String fingerprint = "";

    public static Pair<ItemFilter, String> of(Map<?, ?> item) {
        ItemFilter itemArgument = new ItemFilter();
        // If the map is empty, return a filter without any filters
        if (item.size() == 0)
            return Pair.of(itemArgument, null);
        if (item.containsKey("name")) {
            try {
                String name = TableHelper.getStringField(item, "name");
                if(name.startsWith("#")) {
                    itemArgument.tag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(name));
                } else if ((itemArgument.item = ItemUtil.getRegistryEntry(name, ForgeRegistries.ITEMS)) == null) {
                    return Pair.of(null, "ITEM_NOT_FOUND");
                }
            } catch (LuaException luaException) {
                return Pair.of(null, "ITEM_NO_STRING");
            }
        }

        return Pair.of(itemArgument, null);
    }

    public static ItemFilter empty() {
        return new ItemFilter();
    }

    public boolean test(ItemStack stack, MEStorage storageMonitor) {
        // If the filter does not have nbt values, a tag or a fingerprint, just test if the items are the same
        if(item != Items.AIR) {
            if (tag == null && nbt == null && fingerprint.isEmpty())
                return stack.is(item);
        } else {
            return false;
        }
        return false;
    }

    public int getCount() {
        return count;
    }

    public Item getItem() {
        return item;
    }
}
