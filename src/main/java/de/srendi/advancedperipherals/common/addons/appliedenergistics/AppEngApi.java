package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.AEAddon;
import appeng.api.IAEAddon;
import appeng.api.IAppEngApi;
import appeng.api.networking.crafting.*;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@AEAddon
public class AppEngApi implements IAEAddon {

    public static final AppEngApi INSTANCE = new AppEngApi();

    private static IAppEngApi api;

    @Override
    public void onAPIAvailable(IAppEngApi iAppEngApi) {
        api = iAppEngApi;
    }

    public IAppEngApi getApi() {
        return api;
    }

    public static AppEngApi getInstance() {
        return INSTANCE;
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

    public HashMap<Integer, Object> iteratorToMapStack(Iterator<IAEItemStack> iterator, int flag) {
        HashMap<Integer, Object> map = new HashMap<>();
        int i = 1;
        while (iterator.hasNext()) {
            Object o = getObjectFromStack(iterator.next(), flag);
            if (o != null)
                map.put(i++, o);
        }
        return map;
    }

    public HashMap<Integer, Object> iteratorToMapStack(Iterator<IAEItemStack> iterator) {
        HashMap<Integer, Object> map = new HashMap<>();
        int i = 1;
        while (iterator.hasNext()) {
            Object o = getObjectFromStack(iterator.next());
            if (o != null)
                map.put(i++, o);
        }
        return map;
    }

    public HashMap<Integer, Object> iteratorToMapFluid(Iterator<IAEFluidStack> iterator, int flag) {
        HashMap<Integer, Object> map = new HashMap<>();
        int i = 1;
        while (iterator.hasNext()) {
            Object o = getObjectFromStack(iterator.next(), flag);
            if (o != null)
                map.put(i++, o);
        }
        return map;
    }

    public Object getObjectFromStack(IAEItemStack stack, int flag) {
        HashMap<String, Object> map = new HashMap<>();
        ResourceLocation itemResourceLocation = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String itemName = itemResourceLocation == null ? "null" : itemResourceLocation.toString();
        long amount = stack.getStackSize();
        String displayName = new ItemStack(stack.getItem()).getDisplayName().getString();
        CompoundNBT nbt = stack.createItemStack().getOrCreateTag();
        map.put("name", itemName);
        map.put("amount", amount);
        map.put("displayName", displayName);
        map.put("nbt", nbt.toString());
        if (flag == 0) {
            return map;
        } else if (flag == 1) {
            if (stack.getStackSize() > 0)
                return map;
        } else if (flag == 2) {
            if (stack.isCraftable())
                return map;
        } else if (flag >= 3) {
            throw new IllegalArgumentException("flag cannot be " + flag);
        }
        return null;
    }

    public Object getObjectFromStack(IAEItemStack stack) {
        HashMap<String, Object> map = new HashMap<>();
        ResourceLocation itemResourceLocation = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String itemName = itemResourceLocation == null ? "null" : itemResourceLocation.toString();
        long amount = stack.getStackSize();
        String displayName = new ItemStack(stack.getItem()).getDisplayName().getString();
        CompoundNBT nbt = stack.createItemStack().getOrCreateTag();
        map.put("name", itemName);
        map.put("amount", amount);
        map.put("displayName", displayName);
        map.put("nbt", nbt.toString());
        return map;
    }

    public Map<Object, Object> getMapFromStack(IAEItemStack stack) {
        Map<Object, Object> map = new HashMap<>();
        ResourceLocation itemResourceLocation = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String itemName = itemResourceLocation == null ? "null" : itemResourceLocation.toString();
        long amount = stack.getStackSize();
        String displayName = new ItemStack(stack.getItem()).getDisplayName().getString();
        CompoundNBT nbt = stack.createItemStack().getOrCreateTag();
        map.put("name", itemName);
        map.put("amount", amount);
        map.put("displayName", displayName);
        map.put("nbt", nbt.toString());
        return map;
    }

    public Object getObjectFromStack(IAEFluidStack stack, int flag) {
        HashMap<String, Object> map = new HashMap<>();
        ResourceLocation itemResourceLocation = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
        String itemName = itemResourceLocation == null ? "null" : itemResourceLocation.toString();
        long amount = stack.getStackSize();
        String displayName = stack.getFluidStack().getDisplayName().getString();
        map.put("name", itemName);
        map.put("amount", amount);
        map.put("displayName", displayName);
        if (flag == 0) {
            return map;
        } else if (flag == 1) {
            if (stack.getStackSize() > 0)
                return map;
        } else if (flag == 2) {
            if (stack.isCraftable())
                return map;
        } else if (flag >= 3) {
            throw new IllegalArgumentException("flag cannot be " + flag);
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

    public Object getObjectFromJob(ICraftingJob jobb, IMEMonitor<IAEItemStack> monitor) {
        final ICraftingJob job = jobb;

        final Map<Object, Object> result = new HashMap<>();
        final Map<Object, Object> stack = new HashMap<>();
        stack.put("item", getMapFromStack(job.getOutput()));
        result.put("stack", stack);
        result.put("bytes", job.getByteTotal());
        return result;
    }

}
