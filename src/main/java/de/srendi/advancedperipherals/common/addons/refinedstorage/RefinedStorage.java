package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.StringUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RefinedStorage {

    public static RefinedStorage instance;

    private final IRSAPI api;

    public RefinedStorage() {
        api = API.instance();
        initiate();
    }

    private static INetworkNode read(CompoundTag tag, NetworkNode node) {
        node.read(tag);
        return node;
    }

    public static Object listFluids(INetwork network) {
        List<Object> fluids = new ArrayList<>();
        getFluids(network).forEach(item -> fluids.add(getObjectFromFluid(item, network)));
        return fluids;
    }

    public static Object listItems(INetwork network) {
        List<Object> items = new ArrayList<>();
        getItems(network).forEach(item -> items.add(getObjectFromStack(item, network)));
        return items;
    }

    public static boolean isItemCraftable(INetwork network, ItemStack stack) {
        return network.getCraftingManager().getPattern(stack) != null;
    }

    public static boolean isFluidCraftable(INetwork network, FluidStack stack) {
        return network.getCraftingManager().getPattern(stack) != null;
    }

    public static int getMaxItemDiskStorage(INetwork network) {
        int total = 0;
        boolean creative = false;
        for (IStorage<ItemStack> store : network.getItemStorageCache().getStorages()) {
            if (store instanceof IStorageDisk<ItemStack> storageDisk) {
                int cap = storageDisk.getCapacity();
                if (cap > 0) total += cap;
                else creative = true;
            }
        }
        return creative ? -1 : total;
    }

    public static int getMaxFluidDiskStorage(INetwork network) {
        int total = 0;
        boolean creative = false;
        for (IStorage<FluidStack> store : network.getFluidStorageCache().getStorages()) {
            if (store instanceof IStorageDisk<FluidStack> storageDisk) {
                int cap = storageDisk.getCapacity();
                if (cap > 0) total += cap;
                else creative = true;
            }
        }
        return creative ? -1 : total;
    }

    public static int getMaxItemExternalStorage(INetwork network) {
        int total = 0;
        for (IStorage<ItemStack> store : network.getItemStorageCache().getStorages()) {
            if (store instanceof IExternalStorage<ItemStack> externalStorage) {
                total += externalStorage.getCapacity();
            }
        }
        return total;
    }

    public static int getMaxFluidExternalStorage(INetwork network) {
        int total = 0;
        for (IStorage<FluidStack> store : network.getFluidStorageCache().getStorages()) {
            if (store instanceof IExternalStorage<FluidStack> externalStorage) {
                total += externalStorage.getCapacity();
            }
        }
        return total;
    }

    public static Object getObjectFromPattern(ICraftingPattern pattern, INetwork network) {
        if (pattern == null) return null;
        Map<String, Object> map = new HashMap<>();
        List<ItemStack> outputsList = pattern.getOutputs();
        List<Object> outputs = new ArrayList<>();
        for (ItemStack itemStack : outputsList)
            outputs.add(getObjectFromStack(itemStack.copy(), network));

        map.put("outputs", outputs);

        List<NonNullList<ItemStack>> inputList = pattern.getInputs();
        List<Object> inputs = new ArrayList<>();
        for (List<ItemStack> singleInputList : inputList) {
            List<Object> inputs1 = new ArrayList<>();
            for (ItemStack stack : singleInputList)
                inputs1.add(getObjectFromStack(stack.copy(), network));
            inputs.add(inputs1);
        }

        List<Object> byproducts = new ArrayList<>();
        if (!pattern.isProcessing()) {
            List<ItemStack> byproductsList = pattern.getByproducts();
            for (ItemStack stack : byproductsList)
                byproducts.add(getObjectFromStack(stack.copy(), network));
        }

        map.put("inputs", inputs);
        map.put("outputs", outputs);
        map.put("byproducts", byproducts);
        map.put("processing", pattern.isProcessing());
        return map;
    }

    public static Map<String, Object> getObjectFromStack(@Nullable ItemStack itemStack, INetwork network) {
        if (itemStack == null)
            return Collections.emptyMap();

        Map<String, Object> map = new HashMap<>();
        CompoundTag nbt = itemStack.getTag();
        Supplier<Stream<TagKey<Item>>> tags = () -> itemStack.getItem().builtInRegistryHolder().tags();
        map.put("fingerprint", getFingerpint(itemStack));
        map.put("name", ItemUtil.getRegistryKey(itemStack.getItem()).toString());
        map.put("amount", itemStack.getCount());
        map.put("displayName", itemStack.getDisplayName().getString());
        map.put("isCraftable", isItemCraftable(network, itemStack));
        map.put("nbt", nbt == null ? null : NBTUtil.toLua(nbt));
        map.put("tags", tags.get().findAny().isEmpty() ? null : LuaConverter.tagsToList(tags));

        return map;
    }

    public static Map<String, Object> getObjectFromFluid(@Nullable FluidStack fluidStack, INetwork network) {
        if (fluidStack == null)
            return Collections.emptyMap();

        Map<String, Object> map = new HashMap<>();
        Supplier<Stream<TagKey<Fluid>>> tags = () -> fluidStack.getFluid().builtInRegistryHolder().tags();
        map.put("name", ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).toString());
        map.put("amount", fluidStack.getAmount());
        map.put("displayName", fluidStack.getDisplayName().getString());
        map.put("isCraftable", isFluidCraftable(network, fluidStack));
        map.put("tags", tags.get().findAny().isEmpty() ? null : LuaConverter.tagsToList(tags));

        return map;
    }

    public static Object getItem(INetwork network, ItemStack item) {
        for (ItemStack itemStack : getItems(network)) {
            if (itemStack.sameItem(item) && Objects.equals(itemStack.getTag(), item.getTag()))
                return getObjectFromStack(itemStack, network);
        }
        return null;
    }

    public static List<ItemStack> getCraftableItems(INetwork network) {
        IStorageCache<ItemStack> cache = network.getItemStorageCache();
        Collection<StackListEntry<ItemStack>> craftableEntries = cache.getCraftablesList().getStacks();
        List<ItemStack> result = new ArrayList<>(craftableEntries.size());

        for (StackListEntry<ItemStack> entry : craftableEntries) {
            result.add(entry.getStack());
        }

        return result;
    }

    public static List<FluidStack> getCraftableFluids(INetwork network) {
        IStorageCache<FluidStack> cache = network.getFluidStorageCache();
        Collection<StackListEntry<FluidStack>> craftableEntries = cache.getCraftablesList().getStacks();
        List<FluidStack> result = new ArrayList<>(craftableEntries.size());

        for (StackListEntry<FluidStack> entry : craftableEntries)
            result.add(entry.getStack());

        return result;
    }

    public static List<ItemStack> getItems(INetwork network) {
        IStorageCache<ItemStack> cache = network.getItemStorageCache();
        Collection<StackListEntry<ItemStack>> entries = cache.getList().getStacks();
        List<ItemStack> result = new ArrayList<>(entries.size());

        for (StackListEntry<ItemStack> entry : entries)
            result.add(entry.getStack().copy());

        return result;
    }

    public static List<FluidStack> getFluids(INetwork network) {
        IStorageCache<FluidStack> cache = network.getFluidStorageCache();
        Collection<StackListEntry<FluidStack>> entries = cache.getList().getStacks();
        List<FluidStack> result = new ArrayList<>(entries.size());

        for (StackListEntry<FluidStack> entry : entries)
            result.add(entry.getStack().copy());

        return result;
    }

    public static CompoundTag findMatchingTag(ItemStack stack, String nbtHash, List<ItemStack> items) {
        for (ItemStack rsStack : items) {
            if (rsStack.getCount() > 0 && rsStack.getItem().equals(stack.getItem())) {
                CompoundTag tag = rsStack.getTag();
                String hash = NBTUtil.getNBTHash(tag);
                if (nbtHash.equals(hash)) return tag.copy();
            }
        }
        return null;
    }

    public static ItemStack findMatchingFingerprint(String fingerprint, List<ItemStack> items) {
        for (ItemStack rsStack : items) {
            if (rsStack.getCount() > 0 && fingerprint.equals(getFingerpint(rsStack))) {
                return rsStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static String getFingerpint(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        String fingerprint = (nbt == null ? "null" : nbt.toString()) + ItemUtil.getRegistryKey(stack).toString() + stack.getDisplayName().getString();
        try {
            byte[] bytesOfHash = fingerprint.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            return StringUtil.toHexString(md.digest(bytesOfHash));
        } catch (NoSuchAlgorithmException ex) {
            AdvancedPeripherals.debug("Could not parse fingerprint.");
            ex.printStackTrace();
        }
        return "";
    }

    public void initiate() {
        api.getNetworkNodeRegistry().add(new ResourceLocation(AdvancedPeripherals.MOD_ID, "rs_bridge"), (tag, world, pos) -> read(tag, new RefinedStorageNode(world, pos)));
    }

    public IRSAPI getApi() {
        return api;
    }

}
