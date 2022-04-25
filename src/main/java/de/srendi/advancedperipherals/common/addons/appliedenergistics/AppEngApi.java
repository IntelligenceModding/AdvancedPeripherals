package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StringUtil;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppEngApi {

    public static Pair<Long, AEItemKey> findAEStackFromItemStack(MEStorage monitor, ItemStack item) {
        Pair<Long, AEItemKey> stack = null;
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEItemKey key) {
                if (key.matches(item)) {
                    stack = Pair.of(temp.getLongValue(), key);
                    break;
                }
            }
        }
        return stack;
    }

    public static List<Object> listStacks(MEStorage monitor, ICraftingService service, int flag) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEItemKey itemKey) {
                if (flag == 1) {
                    if (aeKey.getLongValue() < 0)
                        continue;
                } else if (flag == 2) {
                    if (!service.isCraftable(itemKey))
                        continue;
                }

                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service, flag));
            }
        }
        return items;
    }

    public static List<Object> listFluids(MEStorage monitor, ICraftingService service, int flag) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEFluidKey itemKey) {
                if (flag == 1) {
                    if (aeKey.getLongValue() < 0)
                        continue;
                } else if (flag == 2) {
                    if (!service.isCraftable(itemKey))
                        continue;
                }

                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service, flag));
            }
        }
        return items;
    }

    public static Map<String, Object> getObjectFromStack(Pair<Long, AEKey> stack, ICraftingService service, int flag) {
        if (stack.getRight() instanceof AEItemKey itemKey)
            return getObjectFromItemStack(Pair.of(stack.getLeft(), itemKey), service, flag);
        if (stack.getRight() instanceof AEFluidKey fluidKey)
            return getObjectFromFluidStack(Pair.of(stack.getLeft(), fluidKey), service, flag);

        AdvancedPeripherals.debug("Could not create table from unknown stack " + stack.getClass() + " - Report this to the owner", Level.ERROR);
        return null;
    }

    private static Map<String, Object> getObjectFromItemStack(Pair<Long, AEItemKey> stack, ICraftingService craftingService, int flag) {
        Map<String, Object> map = new HashMap<>();
        String displayName = stack.getRight().getDisplayName().getString();
        CompoundTag nbt = stack.getRight().toTag();
        long amount = stack.getLeft();
        map.put("fingerprint", getFingerpint(stack.getRight()));
        map.put("name", stack.getRight().getItem().getRegistryName().toString());
        map.put("amount", amount);
        map.put("displayName", displayName);
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("tags", LuaConverter.tagsToList(() -> stack.getRight().getItem().builtInRegistryHolder().tags()));
        map.put("isCraftable", craftingService.isCraftable(stack.getRight()));
        if (flag == 0) {
            return map;
        } else if (flag == 1) {
            if (amount > 0)
                return map;
        } else if (flag == 2) {
            if (craftingService.isCraftable(stack.getRight()))
                return map;
        }
        return null;
    }

    private static Map<String, Object> getObjectFromFluidStack(Pair<Long, AEFluidKey> stack, ICraftingService craftingService, int flag) {
        Map<String, Object> map = new HashMap<>();
        long amount = stack.getLeft();
        map.put("name", stack.getRight().getFluid().getRegistryName().toString());
        map.put("amount", amount);
        map.put("displayName", stack.getRight().getDisplayName());
        map.put("tags", LuaConverter.tagsToList(() -> stack.getRight().getFluid().builtInRegistryHolder().tags()));
        if (flag == 0) {
            return map;
        } else if (flag == 1) {
            if (amount > 0)
                return map;
        } else if (flag == 2) {
            if (craftingService.isCraftable(stack.getRight()))
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

    public static Map<String, Object> getObjectFromJob(ICraftingPlan job, ICraftingService craftingService) {
        Map<String, Object> result = new HashMap<>();
        result.put("item", getObjectFromStack(Pair.of(job.finalOutput().amount(), job.finalOutput().what()), craftingService, 0));
        result.put("bytes", job.bytes());
        return result;
    }

    public static CompoundTag findMatchingTag(ItemStack stack, String nbtHash, MEStorage monitor) {
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEItemKey itemKey) {
                if (aeKey.getLongValue() > 0 && itemKey.getItem() == stack.getItem()) {
                    CompoundTag tag = itemKey.toStack().getTag();
                    String hash = NBTUtil.getNBTHash(tag);
                    if (nbtHash.equals(hash))
                        return tag.copy();

                }
            }
        }
        return null;
    }

    public static ItemStack findMatchingFingerprint(String fingerprint, MEStorage monitor) {
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEItemKey itemKey) {
                if (aeKey.getLongValue() > 0) {
                    if (fingerprint.equals(getFingerpint(itemKey)))
                        return itemKey.toStack((int) aeKey.getLongValue());

                }
            }
        }
        return null;
    }

    public static String getFingerpint(AEItemKey itemStack) {
        ItemStack stack = itemStack.toStack();
        String fingerprint = stack.getOrCreateTag() + stack.getItem().getRegistryName().toString() + stack.getDisplayName().getString();
        try {
            byte[] bytesOfHash = fingerprint.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            return StringUtil.toHexString(md.digest(bytesOfHash));
        } catch (NoSuchAlgorithmException ex) {
            AdvancedPeripherals.debug("Could not parse fingerprint.", Level.ERROR);
            ex.printStackTrace();
        }
        return "";
    }

    public static MEStorage getMonitor(IGridNode node) {
        return node.getGrid().getService(IStorageService.class).getInventory();
    }


}