package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.AEAddon;
import appeng.api.IAEAddon;
import appeng.api.IAppEngApi;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@AEAddon
public class AppEngApi implements IAEAddon {

    public static final AppEngApi INSTANCE = new AppEngApi();

    private static IAppEngApi api;

    public static AppEngApi getInstance() {
        return INSTANCE;
    }

    @Override
    public void onAPIAvailable(IAppEngApi iAppEngApi) {
        api = iAppEngApi;
    }

    public IAppEngApi getApi() {
        return api;
    }

    public IAEItemStack findAEStackFromItemStack(IMEMonitor<IAEItemStack> monitor, ItemStack item) {
        IAEItemStack stack = null;
        for (IAEItemStack temp : monitor.getStorageList()) {
            if (temp.isSameType(item)) {
                stack = temp;
                break;
            }
        }
        return stack;
    }

    public List<Object> iteratorToMapStack(Iterator<IAEItemStack> iterator, int flag) {
        List<Object> items = new ArrayList<>();
        while (iterator.hasNext()) {
            Object item = getObjectFromStack(iterator.next(), flag);
            if (item != null)
                items.add(item);
        }
        return items;
    }

    public List<Object> iteratorToMapFluid(Iterator<IAEFluidStack> iterator, int flag) {
        List<Object> items = new ArrayList<>();
        while (iterator.hasNext()) {
            Object item = getObjectFromStack(iterator.next(), flag);
            if (item != null)
                items.add(item);
        }
        return items;
    }

    public Object getObjectFromStack(IAEItemStack stack, int flag) {
        Map<Object, Object> map = getMapFromStack(stack);
        if (flag == 0) {
            return map;
        } else if (flag == 1) {
            if (stack.getStackSize() > 0)
                return map;
        } else if (flag == 2) {
            if (stack.isCraftable())
                return map;
        }
        return null;
    }

    public Map<Object, Object> getMapFromStack(IAEItemStack stack) {
        Map<Object, Object> map = new HashMap<>();
        String displayName = stack.createItemStack().getDisplayName().getString();
        CompoundNBT nbt = stack.createItemStack().getOrCreateTag();
        map.put("fingerprint", getFingerpint(stack));
        map.put("name", stack.getItem().getRegistryName().toString());
        map.put("amount", stack.getStackSize());
        map.put("displayName", displayName);
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("tags", LuaConverter.tagsToList(stack.getItem().getTags()));
        map.put("isCraftable", stack.isCraftable());
        return map;
    }

    public Object getObjectFromStack(IAEFluidStack stack, int flag) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", stack.getFluidStack().getFluid().getRegistryName().toString());
        map.put("amount", stack.getFluidStack().getAmount());
        map.put("displayName", stack.getFluidStack().getDisplayName());
        map.put("tags", LuaConverter.tagsToList(stack.getFluid().getTags()));
        if (flag == 0) {
            return map;
        } else if (flag == 1) {
            if (stack.getStackSize() > 0)
                return map;
        } else if (flag == 2) {
            if (stack.isCraftable())
                return map;
        }
        return null;
    }

    public Object getObjectFromCPU(ICraftingCPU cpu) {
        Map<String, Object> map = new HashMap<>();
        long storage = cpu.getAvailableStorage();
        int coProcessors = cpu.getCoProcessors();
        boolean isBusy = cpu.isBusy();
        map.put("storage", storage);
        map.put("coProcessors", coProcessors);
        map.put("isBusy", isBusy);
        return map;
    }

    public Object getObjectFromJob(ICraftingJob job) {
        final Map<Object, Object> result = new HashMap<>();
        final Map<Object, Object> stack = new HashMap<>();
        stack.put("item", getMapFromStack(job.getOutput()));
        result.put("stack", stack);
        result.put("bytes", job.getByteTotal());
        return result;
    }

    public CompoundNBT findMatchingTag(ItemStack stack, String nbtHash, IMEMonitor<IAEItemStack> monitor) {
        IItemList<IAEItemStack> itemStacks = monitor.getStorageList();
        for (IAEItemStack aeStack : itemStacks) {
            if (aeStack.getStackSize() > 0 && aeStack.getItem().equals(stack.getItem())) {
                CompoundNBT tag = aeStack.createItemStack().getTag();
                String hash = NBTUtil.getNBTHash(tag);
                if (nbtHash.equals(hash))
                    return tag.copy();

            }
        }
        return null;
    }

    public ItemStack findMatchingFingerprint(String fingerprint, IMEMonitor<IAEItemStack> monitor) {
        IItemList<IAEItemStack> itemStacks = monitor.getStorageList();
        for (IAEItemStack aeStack : itemStacks) {
            if (aeStack.getStackSize() > 0) {
                if (fingerprint.equals(getFingerpint(aeStack)))
                    return aeStack.createItemStack();

            }
        }
        return null;
    }

    public String getFingerpint(IAEItemStack itemStack) {
        ItemStack stack = itemStack.createItemStack();
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
}