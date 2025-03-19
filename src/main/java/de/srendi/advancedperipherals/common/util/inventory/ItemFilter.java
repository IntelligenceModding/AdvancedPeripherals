package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.DataComponentUtil;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

public class ItemFilter extends GenericFilter<ItemStack> {

    private Item item = Items.AIR;
    private TagKey<Item> tag = null;
    private Tag componentsAsNbt = null;
    private PatchedDataComponentMap components;
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
                    itemFilter.tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(name.substring(1)));
                } else if ((itemFilter.item = ItemUtil.getRegistryEntry(name, BuiltInRegistries.ITEM)) == null) {
                    return Pair.of(null, "ITEM_NOT_FOUND");
                }
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_ITEM");
            }
        }
        if (item.containsKey("components")) {
            try {
                itemFilter.componentsAsNbt = NBTUtil.fromText(TableHelper.getStringField(item, "components"));
            } catch (LuaException luaException) {
                try {
                    itemFilter.componentsAsNbt = NBTUtil.fromText(TableHelper.getTableField(item, "components").toString());
                } catch (LuaException e) {
                    return Pair.of(null, "NO_VALID_COMPONENTS");
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
                itemFilter.fromSlot = TableHelper.getIntField(item, "fromSlot");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FROMSLOT");
            }
        }
        if (item.containsKey("toSlot")) {
            try {
                itemFilter.toSlot = TableHelper.getIntField(item, "toSlot");
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
        filter.componentsAsNbt = DataComponentUtil.toNbt(stack.getComponentsPatch());
        filter.components = (PatchedDataComponentMap) stack.getComponents();
        return filter;
    }

    public static ItemFilter empty() {
        return new ItemFilter();
    }

    public boolean isEmpty() {
        return fingerprint.isEmpty() && item == Items.AIR && tag == null && componentsAsNbt == null;
    }

    @Override
    public boolean testAE(GenericStack genericStack) {
        if (genericStack.what() instanceof AEItemKey aeItemKey) {
            return test(aeItemKey.toStack());
        }
        return false;
    }

    @Override
    public boolean testRS(ResourceAmount resourceAmount) {
        if (resourceAmount.resource() instanceof ItemResource itemResource) {
            return test(itemResource.toItemStack(1));
        }
        return false;
    }

    public ItemStack toItemStack() {
        var result = new ItemStack(item, count);
        if (components != null) {
            result.applyComponents(components);
        }
        return result;
    }

    public boolean test(ItemStack stack) {
        if (isEmpty())
            return true;

        if (!fingerprint.isEmpty()) {
            String testFingerprint = ItemUtil.getFingerprint(stack);
            return fingerprint.equals(testFingerprint);
        }

        if (item != Items.AIR && !stack.is(item)) {
            return false;
        }
        if (tag != null && !stack.is(tag)) {
            return false;
        }
        if (componentsAsNbt != null && !DataComponentUtil.toNbt(stack.getComponentsPatch()).equals(componentsAsNbt)) {
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

    public Tag getComponentsAsNbt() {
        return componentsAsNbt;
    }

    @Override
    public String toString() {
        return "ItemFilter{" +
                "item=" + ItemUtil.getRegistryKey(item) +
                ", tag=" + tag +
                ", components=" + componentsAsNbt +
                ", count=" + count +
                ", fingerprint='" + fingerprint + '\'' +
                ", fromSlot=" + fromSlot +
                ", toSlot=" + toSlot +
                '}';
    }
}
