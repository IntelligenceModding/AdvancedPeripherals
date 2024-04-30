package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class ItemFilter extends GenericFilter {

    private Item item = Items.AIR;
    private TagKey<Item> tag = null;
    private CompoundTag nbt = null;
    private int count = 64;
    private String fingerprint = "";
    public int fromSlot = -1;
    public int toSlot = -1;

    private ItemFilter() {
    }

    public static Pair<ItemFilter, String> parse(Map<?, ?> item) {
        ItemFilter itemFilter = empty();
        // If the map is empty, return a filter without any filters
        if (item.isEmpty())
            return Pair.of(itemFilter, null);
        if (item.containsKey("name")) {
            try {
                String name = TableHelper.getStringField(item, "name");
                if (name.startsWith("#")) {
                    itemFilter.tag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(name.substring(1)));
                } else if ((itemFilter.item = ItemUtil.getRegistryEntry(name, ForgeRegistries.ITEMS)) == null) {
                    return Pair.of(null, "ITEM_NOT_FOUND");
                }
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_ITEM");
            }
        }
        if (item.containsKey("nbt")) {
            try {
                itemFilter.nbt = NBTUtil.fromText(TableHelper.getStringField(item, "nbt"));
            } catch (LuaException luaException) {
                try {
                    itemFilter.nbt = NBTUtil.fromText(TableHelper.getTableField(item, "nbt").toString());
                } catch (LuaException e) {
                    return Pair.of(null, "NO_VALID_NBT");
                }
            }
        }
        if (item.containsKey("fingerprint")) {
            try {
                itemFilter.fingerprint = TableHelper.getStringField(item, "fingerprint");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FINGERPRINT");
            }
        }
        if (item.containsKey("fromSlot")) {
            try {
                itemFilter.fromSlot = TableHelper.getIntField(item, "fromSlot") - 1;
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FROMSLOT");
            }
        }
        if (item.containsKey("toSlot")) {
            try {
                itemFilter.toSlot = TableHelper.getIntField(item, "toSlot") - 1;
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_TOSLOT");
            }
        }
        if (item.containsKey("count")) {
            try {
                itemFilter.count = TableHelper.getIntField(item, "count");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_COUNT");
            }
        }

        AdvancedPeripherals.debug("Parsed item filter: " + itemFilter);
        return Pair.of(itemFilter, null);
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

    @Override
    public boolean test(GenericStack genericStack) {
        if (genericStack.what() instanceof AEItemKey aeItemKey) {
            return test(aeItemKey.toStack());
        }
        return false;
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
        if (nbt != null && !stack.getOrCreateTag().equals(nbt) && (item == Items.AIR || stack.is(item)))
            return false;

        return true;
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

    @Override
    public String toString() {
        return "ItemFilter{" +
                "item=" + ItemUtil.getRegistryKey(item) +
                ", tag=" + tag +
                ", nbt=" + nbt +
                ", count=" + count +
                ", fingerprint='" + fingerprint + '\'' +
                ", fromSlot=" + fromSlot +
                ", toSlot=" + toSlot +
                '}';
    }
}
