package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
    private CompoundTag nbt = null;
    private int count = 64;
    private String fingerprint = "";
    public int fromSlot = -1;
    public int toSlot = -1;

    public static Pair<ItemFilter, String> parse(Map<?, ?> item) {
        ItemFilter itemArgument = empty();
        // If the map is empty, return a filter without any filters
        if (item.size() == 0)
            return Pair.of(itemArgument, null);
        if (item.containsKey("name")) {
            try {
                String name = TableHelper.getStringField(item, "name");
                if (name.startsWith("#")) {
                    itemArgument.tag = TagKey.create(Registries.ITEM, new ResourceLocation(name.substring(1)));
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
                try {
                    itemArgument.nbt = NBTUtil.fromText(TableHelper.getTableField(item, "nbt").toString());
                } catch (LuaException e) {
                    return Pair.of(null, "NO_VALID_NBT");
                }
            }
        }
        if (item.containsKey("fingerprint")) {
            try {
                itemArgument.fingerprint = TableHelper.getStringField(item, "fingerprint");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FINGERPRINT");
            }
        }
        if (item.containsKey("fromSlot")) {
            try {
                itemArgument.fromSlot = TableHelper.getIntField(item, "fromSlot");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FROMSLOT");
            }
        }
        if (item.containsKey("toSlot")) {
            try {
                itemArgument.toSlot = TableHelper.getIntField(item, "toSlot");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_TOSLOT");
            }
        }
        if (item.containsKey("count")) {
            try {
                itemArgument.count = TableHelper.getIntField(item, "count");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_COUNT");
            }
        }

        return Pair.of(itemArgument, null);
    }

    public static ItemFilter fromStack(ItemStack stack) {
        ItemFilter filter = empty();
        filter.item = stack.getItem();
        filter.nbt = stack.hasTag() ? stack.getTag() : null;
        return filter;
    }

    public static ItemFilter empty() {
        return new ItemFilter();
    }

    public boolean isEmpty() {
        return fingerprint.isEmpty() && item == Items.AIR && tag == null && nbt == null;
    }

    public ItemStack toItemStack() {
        var result = new ItemStack(item, count);
        result.setTag(nbt != null ? nbt.copy() : null);
        return result;
    }

    public boolean test(ItemStack stack) {
        if (!fingerprint.isEmpty()) {
            String testFingerprint = ItemUtil.getFingerprint(stack);
            return fingerprint.equals(testFingerprint);
        }

        // If the filter does not have nbt values, a tag or a fingerprint, just test if the items are the same
        if (item != Items.AIR) {
            if (tag == null && nbt == null && fingerprint.isEmpty())
                return stack.is(item);
        }
        if (tag != null && !stack.is(tag))
            return false;
        return nbt == null || stack.getOrCreateTag().equals(nbt);
    }

    public int getCount() {
        return count;
    }

    public Item getItem() {
        return item;
    }

    public int getFromSlot() {
        return fromSlot;
    }

    public int getToSlot() {
        return toSlot;
    }

    public Tag getNbt() {
        return nbt;
    }
}
