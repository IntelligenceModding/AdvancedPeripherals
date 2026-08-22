package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.AttributeFilterItem;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.ListFilterItem;
import com.simibubi.create.content.logistics.filter.PackageFilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack.AttributeFilterItemStack;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.ObjectLuaTable;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public final class CreateFilter {
    private CreateFilter() {}

    public static Map<String, ?> filterToLua(FilterItemStack stack, RegistryAccess registryAccess) {
        if (stack.isEmpty()) {
            return null;
        }
        if (stack instanceof FilterItemStack.ListFilterItemStack listFilter) {
            return Map.of(
                "type", "list",
                "blacklist", listFilter.isBlacklist,
                "ignoreNBT", !listFilter.shouldRespectNBT,
                "filters",
                listFilter.containedItems.stream().map((s) -> filterToLua(s, registryAccess)).toList()
            );
        }
        if (stack instanceof FilterItemStack.AttributeFilterItemStack attributeFilter) {
            return Map.of(
                "type", "attribute",
                "mode", attributeFilter.whitelistMode.name(),
                "attributes",
                attributeFilter.attributeTests.stream()
                    .map((pair) -> {
                        CompoundTag data = ItemAttribute.saveStatic(pair.getFirst());
                        data.putBoolean("inverted", pair.getSecond());
                        return NBTUtil.toLua(data);
                    })
                    .toList()
            );
        }
        if (stack instanceof FilterItemStack.PackageFilterItemStack packageFilter) {
            return Map.of(
                "type", "package",
                "address", packageFilter.filterString
            );
        }
        if (stack.getClass() == FilterItemStack.class) {
            return Map.of(
                "type", "item",
                "item", LuaConverter.itemStackToLuaNoCount(stack.item())
            );
        }
        return Map.of(
            "type", "unknown",
            "item", LuaConverter.itemStackToLuaNoCount(stack.item())
        );
    }

    public static Pair<ItemStack, String> updateFilter(@Nullable ItemStack stack, LuaTable<?, ?> data, RegistryAccess registryAccess) throws LuaException {
        if (data == null) {
            return Pair.onlyLeft(ItemStack.EMPTY);
        }
        String filterType = data.getString("type");
        switch (filterType) {
            case "list" -> {
                if (stack == null) {
                    stack = new ItemStack(AllItems.FILTER.get());
                }
                if (!(stack.getItem() instanceof ListFilterItem listFilterItem)) {
                    return Pair.onlyRight("existing filter item is not list filter");
                }
                CompoundTag tag = stack.getOrCreateTag();

                tag.putBoolean("Blacklist", data.optBoolean("blacklist").orElse(false));
                tag.putBoolean("RespectNBT", !data.optBoolean("ignoreNBT").orElse(false));

                LuaTable<?, ?> filters = EmptyLuaTable.orEmpty(data.optTable("filters"));
                ItemStackHandler handler = listFilterItem.getFilterItemHandler(stack);
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    Pair<ItemStack, String> res = updateFilter(null, filters.optTable(slot + 1).map(ObjectLuaTable::new).orElse(null), registryAccess);
                    if (res.rightPresent()) {
                        return res;
                    }
                    handler.setStackInSlot(slot, res.left());
                }
                tag.put("Items", handler.serializeNBT());
                return Pair.onlyLeft(stack);
            }
            case "attribute" -> {
                if (stack == null) {
                    stack = new ItemStack(AllItems.ATTRIBUTE_FILTER.get());
                }
                if (!(stack.getItem() instanceof AttributeFilterItem)) {
                    return Pair.onlyRight("existing filter item is not attribute filter");
                }
                CompoundTag tag = stack.getOrCreateTag();

                String modeOpt = data.optString("mode").orElse(null);
                if (modeOpt != null) {
                    modeOpt = modeOpt.toUpperCase(Locale.ROOT);
                    AttributeFilterItemStack.WhitelistMode mode = null;
                    for (AttributeFilterItemStack.WhitelistMode m : AttributeFilterItemStack.WhitelistMode.values()) {
                        if (m.name().equals(modeOpt)) {
                            mode = m;
                            break;
                        }
                    }
                    if (mode == null) {
                        return Pair.onlyRight("unexpected value for attributes filter mode: " + modeOpt);
                    }
                    tag.putInt("WhitelistMode", mode.ordinal());
                } else {
                    tag.remove("WhitelistMode");
                }

                LuaTable<?, ?> attributes = EmptyLuaTable.orEmpty(data.optTable("attributes"));
                ListTag attrs = new ListTag();
                for (int i = 1; i <= attributes.size(); i++) {
                    CompoundTag attribute = de.srendi.advancedperipherals.common.util.NBTUtil.mapToNBT(attributes.getTable(i));
                    boolean inverted = attribute.getBoolean("inverted");
                    ItemAttribute attr = ItemAttribute.loadStatic(attribute);
                    if (attr == null) {
                        return Pair.onlyRight("cannot parse attributes");
                    }

                    CompoundTag attr2 = ItemAttribute.saveStatic(attr);
                    attr2.putBoolean("Inverted", inverted);
                    attrs.add(attr2);
                }
                tag.put("MatchedAttributes", attrs);
                return Pair.onlyLeft(stack);
            }
            case "package" -> {
                if (stack == null) {
                    stack = new ItemStack(AllItems.PACKAGE_FILTER.get());
                }
                if (!(stack.getItem() instanceof PackageFilterItem)) {
                    return Pair.onlyRight("existing filter item is not package filter");
                }
                PackageItem.addAddress(stack, data.getString("address"));
                return Pair.onlyLeft(stack);
            }
            case "item" -> {
                if (stack != null && stack.getItem() instanceof FilterItem) {
                    return Pair.onlyRight("existing filter item cannot be a filter");
                }
                LuaTable<?, ?> itemData = new ObjectLuaTable(data.getTable("item"));
                ResourceLocation itemId = ResourceLocation.tryParse(itemData.getString("name"));
                if (itemId == null) {
                    return Pair.onlyRight("invalid item ID: " + itemData.getString("name"));
                }
                Item item = ForgeRegistries.ITEMS.getValue(itemId);
                if (item == null) {
                    return Pair.onlyRight("item does not exists: " + itemId);
                }
                if (item instanceof FilterItem) {
                    return Pair.onlyRight("item cannot be filter item");
                }
                ItemStack newStack = new ItemStack(item);
                Map<?, ?> components = data.optTable("components").orElse(null);
                if (components != null) {
                    newStack.setTag(de.srendi.advancedperipherals.common.util.NBTUtil.mapToNBT(components));
                }
                return Pair.onlyLeft(newStack);
            }
        }
        throw new LuaException("Unsupported filter type. Expect 'list', 'attribute', 'package', or 'item'. Got " + filterType);
    }
}
