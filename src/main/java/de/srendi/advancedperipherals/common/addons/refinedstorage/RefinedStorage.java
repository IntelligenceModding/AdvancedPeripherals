package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class RefinedStorage {

    public static RefinedStorage instance;
    private final IRSAPI api;

    public RefinedStorage() {
        this.api = API.instance();
        initiate();
    }

    private static INetworkNode read(CompoundTag tag, NetworkNode node) {
        node.read(tag);
        return node;
    }

    public static Object listFluids(boolean craftable, INetwork network) {
        List<Object> items = new ArrayList<>();
        for (FluidStack stack : RefinedStorage.getFluids(network, craftable)) {
            Map<String, Object> map = new HashMap<>();
            Set<ResourceLocation> tags = stack.getFluid().getTags();
            map.put("name", stack.getFluid().getRegistryName().toString());
            if (craftable) {
                map.put("craftamount", stack.getAmount());
                map.put("amount", getFluids(network, false).stream()
                        .filter(fluidStack -> fluidStack.isFluidEqual(stack)).collect(Collectors.toList()).get(0).getAmount());
            } else {
                map.put("amount", stack.getAmount());
            }
            map.put("displayName", stack.getDisplayName().getString());
            map.put("tags", LuaConverter.tagsToList(tags));
            items.add(map);
        }
        return items;
    }

    public static Object listItems(boolean craftable, INetwork network) {
        List<Object> items = new ArrayList<>();
        for (ItemStack stack : RefinedStorage.getItems(network, craftable)) {
            Map<String, Object> map = new HashMap<>(getObjectFromStack(stack));
            if (craftable) {
                map.put("craftamount", stack.getCount());
                //The craftable item cache returns the items with the amount of the crafting recipe output.
                //So we need to loop through the normal item cache and get the amount
                map.put("amount", getItems(network, false).stream()
                        .filter(itemStack -> itemStack.getItem().equals(stack.getItem())).collect(Collectors.toList()).get(0).getCount());
                List<Object> ingredients = new ArrayList<>();
                for (List<ItemStack> craftingSlot : network.getCraftingManager().getPattern(stack).getInputs()) {
                    if (craftingSlot != null) {
                        List<Object> slotIngredient = new ArrayList<>();
                        for (ItemStack craftingStack : craftingSlot) {
                            if (craftingStack != null)
                                slotIngredient.add(getObjectFromStack(craftingStack));
                        }
                        ingredients.add(slotIngredient);
                    } else {
                        //Add null so the ingredients list has always a size of 9
                        ingredients.add(null);
                    }
                }
                map.put("ingredient", ingredients);
            } else {
                map.put("amount", stack.getCount());
            }
            items.add(map);
        }
        return items;
    }

    public static Object getObjectFromPattern(ICraftingPattern pattern) {
        if (pattern == null)
            return null;
        Map<String, Object> map = new HashMap<>();
        List<ItemStack> outputsList = pattern.getOutputs();
        List<Object> outputs = new ArrayList<>();
        for (ItemStack itemStack : outputsList)
            outputs.add(getObjectFromStack(itemStack.copy()));

        map.put("outputs", outputs);

        List<NonNullList<ItemStack>> inputList = pattern.getInputs();
        List<Object> inputs = new ArrayList<>();
        for (List<ItemStack> singleInputList : inputList) {
            List<Object> inputs1 = new ArrayList<>();
            for (ItemStack stack : singleInputList)
                inputs1.add(getObjectFromStack(stack.copy()));

            inputs.add(inputs1);
        }
        List<ItemStack> byproductsList = pattern.getByproducts();
        List<Object> byproducts = new ArrayList<>();
        for (ItemStack stack : byproductsList)
            byproducts.add(getObjectFromStack(stack.copy()));

        map.put("inputs", inputs);
        map.put("outputs", outputs);
        map.put("byproducts", byproducts);
        map.put("processing", pattern.isProcessing());
        return map;
    }

    public static Map<String, Object> getObjectFromStack(ItemStack itemStack) {
        Map<String, Object> map = new HashMap<>();
        CompoundTag nbt = itemStack.getOrCreateTag();
        Set<ResourceLocation> tags = itemStack.getItem().getTags();
        map.put("fingerprint", getFingerpint(itemStack));
        map.put("name", itemStack.getItem().getRegistryName().toString());
        map.put("amount", itemStack.getCount());
        map.put("displayName", itemStack.getDisplayName().getString());
        map.put("nbt", nbt.isEmpty() ? null : NBTUtil.toLua(nbt));
        map.put("tags", tags.isEmpty() ? null : LuaConverter.tagsToList(itemStack.getItem().getTags()));

        return map;
    }

    public static Object getItem(List<ItemStack> items, ItemStack item) {
        for (ItemStack itemStack : items) {
            if (itemStack.getItem().equals(item.getItem()) && Objects.equals(itemStack.getOrCreateTag(), item.getOrCreateTag()))
                return getObjectFromStack(itemStack);

        }
        return null;
    }

    public static List<ItemStack> getItems(INetwork network, boolean craftable) {
        IStorageCache<ItemStack> cache = network.getItemStorageCache();
        Collection<StackListEntry<ItemStack>> entries = craftable ? cache.getCraftablesList().getStacks() : cache.getList().getStacks();
        List<ItemStack> result = new ArrayList<>(entries.size());

        for (StackListEntry<ItemStack> entry : entries)
            result.add(entry.getStack().copy());

        return result;
    }

    public static List<FluidStack> getFluids(INetwork network, boolean craftable) {
        IStorageCache<FluidStack> cache = network.getFluidStorageCache();
        Collection<StackListEntry<FluidStack>> entries = craftable ? cache.getCraftablesList().getStacks() : cache.getList().getStacks();
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
                if (nbtHash.equals(hash))
                    return tag.copy();

            }
        }
        return null;
    }

    public static ItemStack findMatchingFingerprint(String fingerprint, List<ItemStack> items) {
        for (ItemStack rsStack : items) {
            if (rsStack.getCount() > 0) {
                if (fingerprint.equals(getFingerpint(rsStack)))
                    return rsStack;

            }
        }
        return ItemStack.EMPTY;
    }

    public static String getFingerpint(ItemStack stack) {
        String fingerprint = stack.getOrCreateTag() + stack.getItem().getRegistryName().toString() + stack.getDisplayName().getString();
        try {
            byte[] bytesOfHash = fingerprint.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            return new String(Hex.encodeHex(md.digest(bytesOfHash)));
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
