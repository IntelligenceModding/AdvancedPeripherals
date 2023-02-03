package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
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
    private Tag nbt = null;
    private int count = -1;
    private String fingerprint = "";
    private int slot;

    public static Pair<ItemFilter, String> of(Map<?, ?> item) {
        ItemFilter itemArgument = new ItemFilter();
        // If the map is empty, return a filter without any filters
        if (item.size() == 0)
            return Pair.of(itemArgument, null);
        if (item.containsKey("name")) {
            try {
                String name = TableHelper.getStringField(item, "name");
                if (name.startsWith("#")) {
                    itemArgument.tag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(name.substring(1)));
                } else if ((itemArgument.item = ItemUtil.getRegistryEntry(name, ForgeRegistries.ITEMS)) == null) {
                    return Pair.of(null, "ITEM_NOT_FOUND");
                }
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_ITEM");
            }
        }
        if (item.containsKey("nbt")) {
            try {
                itemArgument.nbt = NBTUtil.fromText(TableHelper.getStringField(item, "nbt"));
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_NBT");
            }
        }
        if (item.containsKey("fingerprint")) {
            try {
                itemArgument.fingerprint = TableHelper.getStringField(item, "fingerprint");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FINGERPRINT");
            }
        }

        return Pair.of(itemArgument, null);
    }

    public static ItemFilter empty() {
        return new ItemFilter();
    }

    public boolean test(ItemStack stack) {
        // If the filter does not have nbt values, a tag or a fingerprint, just test if the items are the same
        if (!fingerprint.isEmpty()) {
            String testFingerprint = ItemUtil.getFingerprint(stack);
            return fingerprint.equals(testFingerprint);
        }
        if (item != Items.AIR) {
            if (tag == null && nbt == null && fingerprint.isEmpty())
                return stack.is(item);
        }
        if (tag != null && !stack.is(tag))
            return false;
        if (nbt != null && !stack.getOrCreateTag().equals(nbt))
            return false;

        return true;
    }

    public int getCount() {
        return count;
    }

    public Item getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }
}
