package de.srendi.advancedperipherals.common.addons.create;

import com.mojang.serialization.DataResult;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.filter.AttributeFilterItem;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.ListFilterItem;
import com.simibubi.create.content.logistics.filter.PackageFilterItem;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.foundation.item.ItemHelper;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.ObjectLuaTable;
import de.srendi.advancedperipherals.common.util.DataComponentUtil;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.LuaOps;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CreateFilter {
    private CreateFilter() {}

    public static final Map<String, ?> filterToLua(FilterItemStack stack, RegistryAccess registryAccess) {
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
                "mode", attributeFilter.whitelistMode.getSerializedName(),
                "attributes",
                attributeFilter.attributeTests.stream()
                    .map((pair) -> {
                        Map<Object, Object> data = new HashMap<>((Map<?, ?>) ItemAttribute.CODEC
                            .encodeStart(RegistryOps.create(LuaOps.INSTANCE, registryAccess), pair.getFirst())
                            .getOrThrow());
                        data.put("inverted", pair.getSecond());
                        return data;
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
                "item", LuaConverter.itemStackToLua(stack.item())
            );
        }
        return Map.of(
            "type", "unknown",
            "item", LuaConverter.itemStackToLua(stack.item())
        );
    }

    public static final Pair<ItemStack, String> updateFilter(@Nullable ItemStack stack, LuaTable<?, ?> data, RegistryAccess registryAccess) throws LuaException {
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
                stack.set(AllDataComponents.FILTER_ITEMS_BLACKLIST, data.optBoolean("blacklist").orElse(false));
                stack.set(AllDataComponents.FILTER_ITEMS_RESPECT_NBT, !data.optBoolean("ignoreNBT").orElse(false));

                LuaTable<?, ?> filters = EmptyLuaTable.orEmpty(data.optTable("filters"));
                ItemStackHandler handler = listFilterItem.getFilterItemHandler(stack);
                for (int slot = 0; slot < handler.getSlots(); slot++) {
                    Pair<ItemStack, String> res = updateFilter(null, filters.optTable(slot + 1).map(ObjectLuaTable::new).orElse(null), registryAccess);
                    if (res.rightPresent()) {
                        return res;
                    }
                    handler.setStackInSlot(slot, res.left());
                }
                stack.set(AllDataComponents.FILTER_ITEMS, ItemHelper.containerContentsFromHandler(handler));
                return Pair.onlyLeft(stack);
            }
            case "attribute" -> {
                if (stack == null) {
                    stack = new ItemStack(AllItems.ATTRIBUTE_FILTER.get());
                }
                if (!(stack.getItem() instanceof AttributeFilterItem)) {
                    return Pair.onlyRight("existing filter item is not attribute filter");
                }

                String modeOpt = data.optString("mode").orElse(null);
                if (modeOpt != null) {
                    AttributeFilterWhitelistMode mode = null;
                    for (AttributeFilterWhitelistMode m : AttributeFilterWhitelistMode.values()) {
                        if (m.getSerializedName().equals(modeOpt)) {
                            mode = m;
                            break;
                        }
                    }
                    if (mode == null) {
                        return Pair.onlyRight("unexpected value for attributes filter mode: " + modeOpt);
                    }
                    stack.set(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE, mode);
                } else {
                    stack.remove(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE);
                }

                LuaTable<?, ?> attributes = EmptyLuaTable.orEmpty(data.optTable("attributes"));
                List<ItemAttribute.ItemAttributeEntry> attrs = new ArrayList<>(attributes.size());
                for (int i = 1; i <= attributes.size(); i++) {
                    LuaTable<?, ?> attribute = new ObjectLuaTable(attributes.getTable(i));
                    boolean inverted = attribute.optBoolean("inverted").orElse(false);
                    DataResult<ItemAttribute> attr = ItemAttribute.CODEC.parse(RegistryOps.create(LuaOps.INSTANCE, registryAccess), attribute);
                    DataResult.Error<ItemAttribute> error = attr.error().orElse(null);
                    if (error != null) {
                        return Pair.onlyRight("cannot parse attributes: " + error.message());
                    }
                    attrs.add(new ItemAttribute.ItemAttributeEntry(attr.result().orElseThrow(), inverted));
                }
                stack.set(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, attrs);
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
                Item item = BuiltInRegistries.ITEM.getOptional(itemId).orElse(null);
                if (item == null) {
                    return Pair.onlyRight("item does not exists: " + itemId);
                }
                if (item instanceof FilterItem) {
                    return Pair.onlyRight("item cannot be filter item");
                }
                ItemStack newStack = new ItemStack(item);
                Map<?, ?> components = data.optTable("components").orElse(null);
                if (components != null) {
                    newStack.applyComponents(DataComponentUtil.luaToPatch(components, registryAccess));
                }
                return Pair.onlyLeft(newStack);
            }
        }
        throw new LuaException("Unsupported filter type. Expect 'list', 'attribute', 'package', or 'item'. Got " + filterType);
    }
}
