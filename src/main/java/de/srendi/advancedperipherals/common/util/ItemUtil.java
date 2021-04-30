package de.srendi.advancedperipherals.common.util;

import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

import java.sql.Ref;
import java.util.List;
import java.util.Map;

public class ItemUtil {

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

    //AE2
    public static ItemStack getItemStack(Map<?, ?> table, IMEMonitor<IAEItemStack> monitor) throws LuaException {
        if(table.isEmpty())
            return ItemStack.EMPTY;

        if(table.containsKey("fingerprint"))
            return AppEngApi.getInstance().findMatchingFingerprint(TableHelper.getStringField(table, "fingerprint"), monitor);

        if (!table.containsKey("name"))
            return ItemStack.EMPTY;

        String name = TableHelper.getStringField(table, "name");

        Item item = getRegistryEntry(name, ForgeRegistries.ITEMS);

        ItemStack stack = new ItemStack(item, 1);

        if (table.containsKey("count"))
            stack.setCount(TableHelper.getIntField(table, "count"));

        stack.setTag(getTag(stack, table, monitor));

        return stack;
    }

    private static CompoundNBT getTag(ItemStack stack, Map<?, ?> table, IMEMonitor<IAEItemStack> monitor) throws LuaException {
        CompoundNBT nbt = NBTUtil.fromText(TableHelper.optStringField(table, "json", null));
        if (nbt == null) {
            nbt = NBTUtil.fromBinary(TableHelper.optStringField(table, "tag", null));
            if (nbt == null) {
                nbt = parseNBTHash(stack, table, monitor);
            }
        }
        return nbt;
    }

    private static CompoundNBT parseNBTHash(ItemStack stack, Map<?, ?> table, IMEMonitor<IAEItemStack> monitor) throws LuaException {
        String nbt = TableHelper.optStringField(table, "nbt", null);
        if (nbt == null || nbt.isEmpty())
            return null;
        CompoundNBT tag = AppEngApi.getInstance().findMatchingTag(stack, nbt, monitor);
        if (tag != null)
            return tag;

        tag = new CompoundNBT();
        tag.put("_apPlaceholder_", IntNBT.valueOf(1));
        return tag;
    }

    //RS
    public static ItemStack getItemStackRS(Map<?, ?> table, List<ItemStack> items) throws LuaException {
        if(table.isEmpty())
            return ItemStack.EMPTY;

        if(table.containsKey("fingerprint"))
            return RefinedStorage.findMatchingFingerprint(TableHelper.getStringField(table, "fingerprint"), items);

        if (table == null || !table.containsKey("name"))
            return ItemStack.EMPTY;

        String name = TableHelper.getStringField(table, "name");

        Item item = getRegistryEntry(name, ForgeRegistries.ITEMS);

        ItemStack stack = new ItemStack(item, 1);

        if (table.containsKey("count"))
            stack.setCount(TableHelper.getIntField(table, "count"));

        stack.setTag(getTagRS(stack, table, items));

        return stack;
    }

    private static CompoundNBT getTagRS(ItemStack stack, Map<?, ?> table, List<ItemStack> items) throws LuaException {
        CompoundNBT nbt = NBTUtil.fromText(TableHelper.optStringField(table, "json", null));
        if (nbt == null) {
            nbt = NBTUtil.fromBinary(TableHelper.optStringField(table, "tag", null));
            if (nbt == null) {
                nbt = parseNBTHashRS(stack, table, items);
            }
        }
        return nbt;
    }

    private static CompoundNBT parseNBTHashRS(ItemStack stack, Map<?, ?> table, List<ItemStack> items) throws LuaException {
        String nbt = TableHelper.optStringField(table, "nbt", null);
        if (nbt == null || nbt.isEmpty())
            return null;
        CompoundNBT tag = RefinedStorage.findMatchingTag(stack, nbt, items);
        if (tag != null)
            return tag;

        tag = new CompoundNBT();
        tag.put("_apPlaceholder_", IntNBT.valueOf(1));
        return tag;
    }

}
