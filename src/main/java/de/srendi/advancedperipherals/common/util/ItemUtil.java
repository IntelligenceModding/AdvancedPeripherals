package de.srendi.advancedperipherals.common.util;

import appeng.api.storage.MEStorage;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import dan200.computercraft.shared.Registry;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemUtil {

    @ObjectHolder("computercraft:turtle_normal")
    public static final Item TURTLE_NORMAL = Registry.ModItems.TURTLE_NORMAL.get();
    @ObjectHolder("computercraft:turtle_advanced")
    public static final Item TURTLE_ADVANCED = Registry.ModItems.TURTLE_ADVANCED.get();

    @ObjectHolder("computercraft:pocket_computer_normal")
    public static final Item POCKET_NORMAL = Registry.ModItems.POCKET_COMPUTER_NORMAL.get();
    @ObjectHolder("computercraft:pocket_computer_advanced")
    public static final Item POCKET_ADVANCED = Registry.ModItems.POCKET_COMPUTER_ADVANCED.get();

    public static <T extends ForgeRegistryEntry<T>> T getRegistryEntry(String name, IForgeRegistry<T> forgeRegistry) {
        ResourceLocation location;
        try {
            location = new ResourceLocation(name);
        } catch (ResourceLocationException ex) {
            location = null;
        }

        T value;
        if (location != null && forgeRegistry.containsKey(location) && (value = forgeRegistry.getValue(location)) != null) {
            return value;
        } else {
            return null;
        }
    }

    public static ItemStack makeTurtle(Item turtle, String upgrade) {
        ItemStack stack = new ItemStack(turtle);
        stack.getOrCreateTag().putString("RightUpgrade", upgrade);
        return stack;
    }

    public static ItemStack makePocket(Item turtle, String upgrade) {
        ItemStack stack = new ItemStack(turtle);
        stack.getOrCreateTag().putString("Upgrade", upgrade);
        return stack;
    }

    //AE2
    public static ItemStack getItemStack(Map<?, ?> table, MEStorage monitor) throws LuaException {
        if (table == null || table.isEmpty()) return ItemStack.EMPTY;

        if (table.containsKey("fingerprint")) {
            ItemStack fingerprint = AppEngApi.findMatchingFingerprint(TableHelper.getStringField(table, "fingerprint"), monitor);
            if (table.containsKey("count")) fingerprint.setCount(TableHelper.getIntField(table, "count"));
            return fingerprint;
        }

        if (!table.containsKey("name")) return ItemStack.EMPTY;

        String name = TableHelper.getStringField(table, "name");

        Item item = getRegistryEntry(name, ForgeRegistries.ITEMS);

        ItemStack stack = new ItemStack(item, 1);

        if (table.containsKey("count")) stack.setCount(TableHelper.getIntField(table, "count"));

        if (table.containsKey("nbt") || table.containsKey("json") || table.containsKey("tag"))
            stack.setTag(getTag(stack, table, monitor));

        return stack;
    }

    private static CompoundTag getTag(ItemStack stack, Map<?, ?> table, MEStorage monitor) throws LuaException {
        CompoundTag nbt = NBTUtil.fromText(TableHelper.optStringField(table, "json", null));
        if (nbt == null) {
            nbt = NBTUtil.fromBinary(TableHelper.optStringField(table, "tag", null));
            if (nbt == null) {
                nbt = parseNBTHash(stack, table, monitor);
            }
        }
        return nbt;
    }

    private static CompoundTag parseNBTHash(ItemStack stack, Map<?, ?> table, MEStorage monitor) throws LuaException {
        String nbt = TableHelper.optStringField(table, "nbt", null);
        if (nbt == null || nbt.isEmpty()) return null;
        CompoundTag tag = AppEngApi.findMatchingTag(stack, nbt, monitor);
        if (tag != null) return tag;

        tag = new CompoundTag();
        tag.put("_apPlaceholder_", IntTag.valueOf(1));
        return tag;
    }

    //RS
    public static ItemStack getItemStackRS(Map<?, ?> table, List<ItemStack> items) throws LuaException {
        if (table == null || table.isEmpty()) return ItemStack.EMPTY;

        if (table.containsKey("fingerprint")) {
            ItemStack fingerprint = RefinedStorage.findMatchingFingerprint(TableHelper.getStringField(table, "fingerprint"), items);
            if (table.containsKey("count")) fingerprint.setCount(TableHelper.getIntField(table, "count"));
            return fingerprint;
        }
        if (!table.containsKey("name")) return ItemStack.EMPTY;

        String name = TableHelper.getStringField(table, "name");

        Item item = getRegistryEntry(name, ForgeRegistries.ITEMS);

        ItemStack stack = new ItemStack(item, 1);

        if (table.containsKey("count")) stack.setCount(TableHelper.getIntField(table, "count"));

        if (table.containsKey("nbt") || table.containsKey("json") || table.containsKey("tag"))
            stack.setTag(getTagRS(stack, table, items));

        return stack;
    }

    private static CompoundTag getTagRS(ItemStack stack, Map<?, ?> table, List<ItemStack> items) throws LuaException {
        CompoundTag nbt = NBTUtil.fromText(TableHelper.optStringField(table, "json", null));
        if (nbt == null) {
            nbt = NBTUtil.fromBinary(TableHelper.optStringField(table, "tag", null));
            if (nbt == null) {
                nbt = parseNBTHashRS(stack, table, items);
            }
        }
        return nbt;
    }

    private static CompoundTag parseNBTHashRS(ItemStack stack, Map<?, ?> table, List<ItemStack> items) throws LuaException {
        String nbt = TableHelper.optStringField(table, "nbt", null);
        if (nbt == null || nbt.isEmpty()) return null;
        CompoundTag tag = RefinedStorage.findMatchingTag(stack, nbt, items);
        if (tag != null) return tag;

        tag = new CompoundTag();
        return tag;
    }

    //Gathers all items in handler and returns them
    public static List<ItemStack> getItemsFromItemHandler(IItemHandler handler) {
        List<ItemStack> items = new ArrayList<>(handler.getSlots());
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            items.add(handler.getStackInSlot(slot).copy());
        }

        return items;
    }
}
