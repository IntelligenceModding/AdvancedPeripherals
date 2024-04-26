package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
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
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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

    public static ItemStack findStackFromStack(INetwork network, ItemStack item) {
        return findStackFromFilter(network, ItemFilter.fromStack(item));
    }

    public static ItemStack findPatternFromStack(ICraftingManager crafting, ItemStack item) {
        return findPatternFromFilter(crafting, ItemFilter.fromStack(item));
    }

    public static ItemStack findStackFromFilter(INetwork network, ItemFilter filter) {
        for (StackListEntry<ItemStack> temp : network.getItemStorageCache().getList().getStacks()) {
            ItemStack stack = temp.getStack();
            if (filter.test(stack)) {
                return stack.copy();
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack findPatternFromFilter(ICraftingManager crafting, ItemFilter filter) {
        for (ICraftingPattern pattern : crafting.getPatterns()) {
            for (ItemStack stack : pattern.getOutputs()) {
                if (filter.test(stack)) {
                    return stack.copy();
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public static FluidStack findFluidFromStack(INetwork network, FluidStack stack) {
        return findFluidFromFilter(network, FluidFilter.fromStack(stack));
    }

    public static FluidStack findFluidPatternFromStack(ICraftingManager crafting, FluidStack stack) {
        return findFluidPatternFromFilter(crafting, FluidFilter.fromStack(stack));
    }

    public static FluidStack findFluidFromFilter(INetwork network, FluidFilter filter) {
        for (StackListEntry<FluidStack> temp : network.getFluidStorageCache().getList().getStacks()) {
            FluidStack stack = temp.getStack();
            if (filter.test(stack)) {
                return stack.copy();
            }
        }

        return FluidStack.EMPTY;
    }

    public static FluidStack findFluidPatternFromFilter(ICraftingManager crafting, FluidFilter filter) {
        for (ICraftingPattern pattern : crafting.getPatterns()) {
            FluidStack stack = pattern.getFluidOutputs().stream().filter(filter::test).findFirst().orElse(FluidStack.EMPTY);
            if (stack != FluidStack.EMPTY) {
                return stack.copy();
            }
        }

        return FluidStack.EMPTY;
    }

    public static Object listItems(INetwork network) {
        Map<Object, ItemStack> craftables = new HashMap<>();
        getCraftableItemsStream(network).forEach(stack -> craftables.put(ItemUtil.asMapKey(stack), stack));
        return getItemsStream(network).map(item -> getObjectFromStack(item, craftables.get(ItemUtil.asMapKey(item)))).toList();
    }

    public static Object listFluids(INetwork network) {
        Map<Object, FluidStack> craftables = new HashMap<>();
        getCraftableFluidsStream(network).forEach(stack -> craftables.put(ItemUtil.fluidAsMapKey(stack), stack));
        return getFluidsStream(network).map(item -> getObjectFromFluid(item, craftables.get(ItemUtil.fluidAsMapKey(item)))).toList();
    }

    public static boolean isItemCraftable(INetwork network, ItemStack stack) {
        return network.getCraftingManager().getPattern(stack) != null;
    }

    public static boolean isFluidCraftable(INetwork network, FluidStack stack) {
        return network.getCraftingManager().getPattern(stack) != null;
    }

    public static long getMaxItemDiskStorage(INetwork network) {
        long total = 0;
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

    public static long getMaxFluidDiskStorage(INetwork network) {
        long total = 0;
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

    public static long getMaxItemExternalStorage(INetwork network) {
        long total = 0;
        for (IStorage<ItemStack> store : network.getItemStorageCache().getStorages()) {
            if (store instanceof IExternalStorage<ItemStack> externalStorage) {
                total += externalStorage.getCapacity();
            }
        }
        return total;
    }

    public static long getMaxFluidExternalStorage(INetwork network) {
        long total = 0;
        for (IStorage<FluidStack> store : network.getFluidStorageCache().getStorages()) {
            if (store instanceof IExternalStorage<FluidStack> externalStorage) {
                total += externalStorage.getCapacity();
            }
        }
        return total;
    }

    public static Object getObjectFromPattern(ICraftingPattern pattern, INetwork network) {
        if (pattern == null)
            return null;

        Map<String, Object> map = new HashMap<>();
        map.put("outputs", pattern.getOutputs().stream().map(RefinedStorage::getObjectFromStack).toList());
        map.put("fluidOutputs", pattern.getFluidOutputs().stream().map(RefinedStorage::getObjectFromFluid).toList());

        List<List<Map<String, Object>>> inputs = pattern.getInputs().stream().map(singleInputList ->
            singleInputList.stream().map(RefinedStorage::getObjectFromStack).toList()).toList();

        List<List<Map<String, Object>>> fluidInputs = pattern.getInputs().stream().map(singleInputList ->
            singleInputList.stream().map(RefinedStorage::getObjectFromStack).toList()).toList();

        List<Map<String, Object>> byproducts = new ArrayList<>();
        if (!pattern.isProcessing()) {
            byproducts = pattern.getByproducts().stream().map(RefinedStorage::getObjectFromStack).toList();
        }

        map.put("fluidInputs", fluidInputs);
        map.put("inputs", inputs);
        map.put("byproducts", byproducts);
        map.put("processing", pattern.isProcessing());
        Map<String, Object> container = new HashMap<>();
        map.put("name", pattern.getContainer().getName().getString());
        map.put("position", LuaConverter.posToObject(pattern.getContainer().getPosition()));

        map.put("container", container);
        map.put("isValid", pattern.isValid());
        map.put("errorMessage", pattern.getErrorMessage() == null ? "" : pattern.getErrorMessage().getString());
        return map;
    }

    @Nullable
    public static Map<String, Object> getObjectFromStack(@Nullable ItemStack item) {
        if (item == null) {
            return null;
        }
        Map<String, Object> map = LuaConverter.itemToObject(item.getItem());
        map.put("amount", item.getCount());
        map.put("fingerprint", ItemUtil.getFingerprint(item));
        map.put("displayName", item.getDisplayName().getString());
        CompoundTag nbt = item.getTag();
        map.put("nbt", nbt == null ? null : NBTUtil.toLua(nbt));
        return map;
    }

    @Nullable
    public static Map<String, Object> getObjectFromStack(@Nullable ItemStack stored, @Nullable ItemStack craftable) {
        Map<String, Object> map;
        if (stored == null || stored == ItemStack.EMPTY) {
            if (craftable == null || craftable == ItemStack.EMPTY) {
                return null;
            }
            map = LuaConverter.itemToObject(craftable.getItem());
            map.put("amount", 0);
            stored = craftable;
        } else {
            map = LuaConverter.itemToObject(stored.getItem());
            map.put("amount", stored.getCount());
        }

        map.put("fingerprint", ItemUtil.getFingerprint(stored));
        map.put("displayName", stored.getDisplayName().getString());
        CompoundTag nbt = stored.getTag();
        map.put("nbt", nbt == null ? null : NBTUtil.toLua(nbt));

        if (craftable == null) {
            map.put("isCraftable", false);
        } else {
            map.put("isCraftable", true);
            map.put("craftAmount", craftable.getCount());
        }

        return map;
    }

    @Nullable
    public static Map<String, Object> getObjectFromFluid(@Nullable FluidStack item) {
        if (item == null) {
            return null;
        }
        Map<String, Object> map = LuaConverter.fluidToObject(item.getFluid());
        map.put("amount", item.getAmount());
        map.put("displayName", item.getDisplayName().getString());
        CompoundTag nbt = item.getTag();
        map.put("nbt", nbt == null ? null : NBTUtil.toLua(nbt));
        return map;
    }

    @Nullable
    public static Map<String, Object> getObjectFromFluid(@Nullable FluidStack stored, @Nullable FluidStack craftable) {
        Map<String, Object> map;
        if (stored == null) {
            if (craftable == null) {
                return null;
            }
            map = LuaConverter.fluidToObject(craftable.getFluid());
            map.put("amount", 0);
            stored = craftable;
        } else {
            map = LuaConverter.fluidToObject(stored.getFluid());
            map.put("amount", stored.getAmount());
        }

        map.put("displayName", stored.getDisplayName().getString());

        if (craftable == null) {
            map.put("isCraftable", false);
        } else {
            map.put("isCraftable", true);
            map.put("craftAmount", craftable.getAmount());
        }

        return map;
    }

    public static Object getItem(INetwork network, ItemStack item) {
        ItemFilter filter = ItemFilter.fromStack(item);
        ItemStack stored = findStackFromFilter(network, filter);
        ItemStack craftable = findPatternFromFilter(network.getCraftingManager(), filter);
        return getObjectFromStack(stored, craftable);
    }

    public static Object getFluid(INetwork network, FluidStack fluid) {
        FluidFilter filter = FluidFilter.fromStack(fluid);
        FluidStack stored = findFluidFromFilter(network, filter);
        FluidStack craftable = findFluidPatternFromFilter(network.getCraftingManager(), filter);
        return getObjectFromFluid(stored, craftable);
    }

    public static Stream<ItemStack> getCraftableItemsStream(INetwork network) {
        IStorageCache<ItemStack> cache = network.getItemStorageCache();
        return cache.getCraftablesList().getStacks().stream().map(entry -> entry.getStack().copy());
    }

    public static List<ItemStack> getCraftableItems(INetwork network) {
        return getCraftableItemsStream(network).toList();
    }

    public static Stream<FluidStack> getCraftableFluidsStream(INetwork network) {
        IStorageCache<FluidStack> cache = network.getFluidStorageCache();
        return cache.getCraftablesList().getStacks().stream().map(entry -> entry.getStack().copy());
    }

    public static List<FluidStack> getCraftableFluids(INetwork network) {
        return getCraftableFluidsStream(network).toList();
    }

    public static Stream<ItemStack> getItemsStream(INetwork network) {
        IStorageCache<ItemStack> cache = network.getItemStorageCache();
        return cache.getList().getStacks().stream().map(entry -> entry.getStack().copy());
    }

    public static List<ItemStack> getItems(INetwork network) {
        return getItemsStream(network).toList();
    }

    public static Stream<FluidStack> getFluidsStream(INetwork network) {
        IStorageCache<FluidStack> cache = network.getFluidStorageCache();
        return cache.getList().getStacks().stream().map(entry -> entry.getStack().copy());
    }

    public static List<FluidStack> getFluids(INetwork network) {
        return getFluidsStream(network).toList();
    }

    public void initiate() {
        api.getNetworkNodeRegistry().add(new ResourceLocation(AdvancedPeripherals.MOD_ID, "rs_bridge"), (tag, world, pos) -> read(tag, new RefinedStorageNode(world, pos)));
    }

    public IRSAPI getApi() {
        return api;
    }

}
