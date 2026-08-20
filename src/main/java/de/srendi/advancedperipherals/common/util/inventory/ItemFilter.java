package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.LuaValues;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.util.FingerprintUtil;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.RegistryUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

public class ItemFilter extends GenericFilter<ItemStack> {

    public static final ItemFilter EMPTY = new ItemFilter();

    private Item item = Items.AIR;
    private TagKey<Item> tag = null;
    private CompoundTag components = null;
    private int count = Integer.MAX_VALUE;
    private String nbtHash = null;
    public int fromSlot = -1;
    public int toSlot = -1;

    private ItemFilter() {
    }

    public static Pair<ItemFilter, String> parse(LuaTable<?, ?> item) throws LuaException {
        // If the map is empty, return a filter without any filters
        if (item.isEmpty()) {
            return Pair.of(EMPTY, null);
        }

        ItemFilter itemFilter = createEmpty();

        if (item.containsKey("name")) {
            String name = item.getString("name");
            if (name.startsWith("#")) {
                itemFilter.tag = TagKey.create(Registries.ITEM, new ResourceLocation(name.substring(1)));
            } else if ((itemFilter.item = RegistryUtil.getRegistryEntry(name, BuiltInRegistries.ITEM)) == null) {
                return Pair.of(null, "ITEM_NOT_FOUND");
            }
        }
        if (item.containsKey("components")) {
            Object components = item.get("components");
            if (components instanceof String snbt) {
                itemFilter.components = NBTUtil.fromSNBT(snbt);
            } else if (components instanceof Map<?, ?> map) {
                itemFilter.components = NBTUtil.mapToNBT(map);
            } else {
                throw LuaValues.badField("components", "string or table", LuaValues.getType(components));
            }
        }
        if (item.containsKey("nbtHash")) {
            itemFilter.nbtHash = item.getString("nbtHash");
        }
        if (item.containsKey("fromSlot")) {
            itemFilter.fromSlot = item.getInt("fromSlot") - 1;
        }
        if (item.containsKey("toSlot")) {
            itemFilter.toSlot = item.getInt("toSlot") - 1;
        }
        if (item.containsKey("count")) {
            itemFilter.count = item.getInt("count");
        }

        AdvancedPeripherals.debug("Parsed item filter: {}", itemFilter);
        return Pair.of(itemFilter, null);
    }

    public static ItemFilter fromStack(ItemStack stack) {
        return fromStackWithCount(stack, stack.getCount());
    }

    public static ItemFilter fromStackWithCount(ItemStack stack, int count) {
        ItemFilter filter = createEmpty();
        filter.item = stack.getItem();
        filter.count = count;
        filter.components = stack.hasTag() ? stack.getTag().copy() : new CompoundTag();
        return filter;
    }

    public static ItemFilter createEmpty() {
        return new ItemFilter();
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY || (nbtHash == null && item == Items.AIR && tag == null && components == null);
    }

    @Override
    public boolean testAE(GenericStack genericStack) {
        if (!APAddon.AE2.isLoaded())
            return false;

        if (genericStack.what() instanceof AEItemKey aeItemKey) {
            return test(aeItemKey.toStack());
        }
        return false;
    }

    @Override
    public boolean testRS(ResourceAmount resourceAmount) {
        if (!APAddon.REFINEDSTORAGE.isLoaded())
            return false;

        if (resourceAmount.resource() instanceof ItemResource itemResource) {
            return test(itemResource.toItemStack(1));
        }
        return false;
    }

    @Override
    public ItemFilter copy() {
        ItemFilter newFilter = new ItemFilter();
        newFilter.item = this.item;
        newFilter.tag = this.tag;
        newFilter.components = this.components;
        newFilter.count = this.count;
        newFilter.nbtHash = this.nbtHash;
        newFilter.fromSlot = this.fromSlot;
        newFilter.toSlot = this.toSlot;
        return newFilter;
    }

    public ItemFilter copyWithCount(int count) {
        ItemFilter newFilter = this.copy();
        newFilter.count = count;
        return newFilter;
    }

    public ItemStack toItemStack() {
        var result = new ItemStack(item, count);
        if (components != null && !components.isEmpty()) {
            result.setTag(components.copy());
        }
        return result;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (isEmpty()) {
            return true;
        }
        if (item != Items.AIR && !stack.is(item)) {
            return false;
        }
        if (tag != null && !stack.is(tag)) {
            return false;
        }
        if (components != null && !components.equals(stack.hasTag() ? stack.getTag() : new CompoundTag())) {
            return false;
        }
        if (nbtHash != null && !nbtHash.equals(FingerprintUtil.hashOrEmpty(stack.getTag()))) {
            return false;
        }
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

    public CompoundTag getComponents() {
        return components;
    }

    @Override
    public String toString() {
        return "ItemFilter{" +
                "item=" + ItemUtil.getRegistryKey(item) +
                ", tag=" + tag +
                ", components=" + components +
                ", count=" + count +
                ", nbtHash='" + nbtHash + '\'' +
                ", fromSlot=" + fromSlot +
                ", toSlot=" + toSlot +
                '}';
    }
}
