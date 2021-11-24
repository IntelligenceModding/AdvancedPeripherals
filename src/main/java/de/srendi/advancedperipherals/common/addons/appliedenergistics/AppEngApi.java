package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.storage.IStorageService;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.StorageChannels;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackList;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.Level;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AppEngApi {

    public static IAEItemStack findAEStackFromItemStack(IMEMonitor<IAEItemStack> monitor, ItemStack item) {
        IAEItemStack stack = null;
        for (IAEItemStack temp : monitor.getStorageList()) {
            if (temp.isSameType(item)) {
                stack = temp;
                break;
            }
        }
        return stack;
    }

    public static List<Object> iteratorToMapStack(Iterator<IAEItemStack> iterator, int flag) {
        List<Object> items = new ArrayList<>();
        while (iterator.hasNext()) {
            IAEItemStack stack = iterator.next();
            if (flag == 1) {
                if (stack.getStackSize() < 0)
                    continue;
            } else if (flag == 2) {
                if (!stack.isCraftable())
                    continue;
            }

            items.add(getObjectFromStack(stack, flag));
        }
        return items;
    }

    public static List<Object> iteratorToMapFluid(Iterator<IAEFluidStack> iterator, int flag) {
        List<Object> items = new ArrayList<>();
        while (iterator.hasNext()) {
            IAEFluidStack stack = iterator.next();
            if (flag == 1) {
                if (stack.getStackSize() < 0)
                    continue;
            } else if (flag == 2) {
                if (!stack.isCraftable())
                    continue;
            }

            items.add(getObjectFromStack(stack, flag));
        }
        return items;
    }

    public static Map<String, Object> getObjectFromStack(IAEStack stack, int flag) {
        if(stack instanceof IAEItemStack itemStack)
            return getObjectFromStack(itemStack, flag);
        if(stack instanceof IAEFluidStack fluidStack)
            return getObjectFromStack(fluidStack, flag);

        AdvancedPeripherals.debug("Could not create table from unknown stack " + stack.getClass() + " - Report this to the owner", Level.ERROR);
        return null;
    }

    public static Map<String, Object> getObjectFromStack(IAEItemStack stack, int flag) {
        Map<String, Object> map = new HashMap<>();
        String displayName = stack.createItemStack().getDisplayName().getString();
        CompoundTag nbt = stack.createItemStack().getOrCreateTag();
        map.put("fingerprint", getFingerpint(stack));
        map.put("name", stack.getItem().getRegistryName().toString());
        map.put("amount", stack.getStackSize());
        map.put("count", stack.getStackSize());
        map.put("displayName", displayName);
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("tags", LuaConverter.tagsToList(stack.getItem().getTags()));
        map.put("isCraftable", stack.isCraftable());
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

    public static Map<String, Object> getObjectFromStack(IAEFluidStack stack, int flag) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", stack.getFluidStack().getFluid().getRegistryName().toString());
        map.put("amount", stack.getFluidStack().getAmount());
        map.put("count", stack.getFluidStack().getAmount());
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

    public static Map<String, Object> getObjectFromCPU(ICraftingCPU cpu) {
        Map<String, Object> map = new HashMap<>();
        long storage = cpu.getAvailableStorage();
        int coProcessors = cpu.getCoProcessors();
        boolean isBusy = cpu.isBusy();
        map.put("storage", storage);
        map.put("coProcessors", coProcessors);
        map.put("isBusy", isBusy);
        return map;
    }

    public static Map<String, Object> getObjectFromJob(ICraftingPlan job) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> stack = new HashMap<>();
        stack.put("item", getObjectFromStack(job.finalOutput(), 0));
        result.put("stack", stack);
        result.put("bytes", job.bytes());
        return result;
    }

    public static CompoundTag findMatchingTag(ItemStack stack, String nbtHash, IMEMonitor<IAEItemStack> monitor) {
        IAEStackList<IAEItemStack> itemStacks = monitor.getStorageList();
        for (IAEItemStack aeStack : itemStacks) {
            if (aeStack.getStackSize() > 0 && aeStack.getItem().equals(stack.getItem())) {
                CompoundTag tag = aeStack.createItemStack().getTag();
                String hash = NBTUtil.getNBTHash(tag);
                if (nbtHash.equals(hash))
                    return tag.copy();

            }
        }
        return null;
    }

    public static ItemStack findMatchingFingerprint(String fingerprint, IMEMonitor<IAEItemStack> monitor) {
        IAEStackList<IAEItemStack> itemStacks = monitor.getStorageList();
        for (IAEItemStack aeStack : itemStacks) {
            if (aeStack.getStackSize() > 0) {
                if (fingerprint.equals(getFingerpint(aeStack)))
                    return aeStack.createItemStack();

            }
        }
        return null;
    }

    public static String getFingerpint(IAEItemStack itemStack) {
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

    public static IMEMonitor<IAEItemStack> getMonitor(IGridNode node) {
        return node.getGrid().getService(IStorageService.class).getInventory(StorageChannels.items());
    }

    public static IMEMonitor<IAEFluidStack> getMonitorF(IGridNode node) {
        return node.getGrid().getService(IStorageService.class).getInventory(StorageChannels.fluids());
    }
}