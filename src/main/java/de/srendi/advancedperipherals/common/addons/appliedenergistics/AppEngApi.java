package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.AEKeyFilter;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.items.storage.BasicStorageCell;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StringUtil;
import io.github.projectet.ae2things.item.DISKDrive;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AppEngApi {

    public static Pair<Long, AEItemKey> findAEStackFromItemStack(MEStorage monitor, ICraftingService crafting, ItemStack item) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEItemKey key && key.matches(item))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null)
            return null;

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof AEItemKey key && key.matches(item))
                return Pair.of(0L, key);
        }

        return null;
    }

    public static Pair<Long, AEItemKey> findAEStackFromItemStack(MEStorage monitor, ItemStack item) {
        return findAEStackFromItemStack(monitor, null, item);
    }

    public static List<Object> listStacks(MEStorage monitor, ICraftingService service, int flag) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
            if (aeKey.getKey() instanceof AEItemKey itemKey) {
                if (flag == 1 && aeKey.getLongValue() < 0) {
                    continue;
                } else if (flag == 2 && !service.isCraftable(itemKey)) {
                    service.getCraftables(AEKeyFilter.none()).forEach(aeKey1 -> {
                        Map<String, Object> itemObject = getObjectFromStack(Pair.of((long) 0, aeKey1), service);
                        if (keyCounter.get(aeKey1) == 0 && !items.contains(itemObject))
                            items.add(itemObject);
                    });
                    continue;
                }

                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service));
            }
        }
        return items;
    }

    public static List<Object> listFluids(MEStorage monitor, ICraftingService service, int flag) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEFluidKey itemKey) {
                if ((flag == 1 && aeKey.getLongValue() < 0) || (flag == 2 && !service.isCraftable(itemKey)))
                    continue;

                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service));
            }
        }
        return items;
    }

    public static Map<String, Object> getObjectFromStack(Pair<Long, AEKey> stack, ICraftingService service) {
        if (stack.getRight() instanceof AEItemKey itemKey)
            return getObjectFromItemStack(Pair.of(stack.getLeft(), itemKey), service);
        if (stack.getRight() instanceof AEFluidKey fluidKey)
            return getObjectFromFluidStack(Pair.of(stack.getLeft(), fluidKey), service);

        AdvancedPeripherals.debug("Could not create table from unknown stack " + stack.getClass() + " - Report this to the owner", Level.ERROR);
        return Collections.emptyMap();
    }

    private static Map<String, Object> getObjectFromItemStack(Pair<Long, AEItemKey> stack, ICraftingService craftingService) {
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

        return map;
    }

    private static Map<String, Object> getObjectFromFluidStack(Pair<Long, AEFluidKey> stack, ICraftingService craftingService) {
        Map<String, Object> map = new HashMap<>();
        long amount = stack.getLeft();
        map.put("name", stack.getRight().getFluid().getRegistryName().toString());
        map.put("amount", amount);
        map.put("displayName", stack.getRight().getDisplayName());
        map.put("tags", LuaConverter.tagsToList(() -> stack.getRight().getFluid().builtInRegistryHolder().tags()));
        map.put("isCraftable", craftingService.isCraftable(stack.getRight()));

        return map;
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
        result.put("item", getObjectFromStack(Pair.of(job.finalOutput().amount(), job.finalOutput().what()), craftingService));
        result.put("bytes", job.bytes());

        return result;
    }

    public static CompoundTag findMatchingTag(ItemStack stack, String nbtHash, MEStorage monitor) {
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEItemKey itemKey && aeKey.getLongValue() > 0 && itemKey.getItem() == stack.getItem()) {
                CompoundTag tag = itemKey.toStack().getTag();
                String hash = NBTUtil.getNBTHash(tag);
                if (nbtHash.equals(hash))
                    return tag.copy();

            }
        }
        return null;
    }

    public static CompoundTag findMatchingTag(FluidStack stack, String nbtHash, MEStorage monitor) {
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEFluidKey fluidKey && aeKey.getLongValue() > 0 && fluidKey.getFluid() == stack.getFluid()) {
                CompoundTag tag = fluidKey.toStack(1).getTag();
                String hash = NBTUtil.getNBTHash(tag);
                if (nbtHash.equals(hash))
                    return tag.copy();

            }
        }
        return null;
    }

    public static ItemStack findMatchingFingerprint(String fingerprint, MEStorage monitor) {
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (!(aeKey.getKey() instanceof AEItemKey itemKey))
                continue;
            if (aeKey.getLongValue() > 0 && fingerprint.equals(getFingerpint(itemKey))) {
                return itemKey.toStack((int) aeKey.getLongValue());
            }
        }
        return null;
    }

    public static FluidStack findMatchingFluidFingerprint(String fingerprint, MEStorage monitor) {
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (!(aeKey.getKey() instanceof AEFluidKey itemKey))
                continue;
            if (aeKey.getLongValue() > 0 && fingerprint.equals(getFingerpint(itemKey))) {
                return itemKey.toStack((int) aeKey.getLongValue());
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

    public static String getFingerpint(AEFluidKey fluidStack) {
        FluidStack stack = fluidStack.toStack(1);
        String fingerprint = stack.getOrCreateTag() + stack.getFluid().getRegistryName().toString() + stack.getDisplayName().getString();
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

    public static boolean isItemCrafting(MEStorage monitor, ICraftingService grid, ItemStack itemStack,
                                         @Nullable ICraftingCPU craftingCPU) {
        Pair<Long, AEItemKey> stack = AppEngApi.findAEStackFromItemStack(monitor, grid, itemStack);

        if (stack == null)
            // If the item stack does not exist, it cannot be crafting.
            return false;

        // If the passed cpu is null, check all cpus
        if(craftingCPU == null) {
            // Loop through all crafting cpus and check if the item is being crafted.
            for (ICraftingCPU cpu : grid.getCpus()) {
                if (cpu.isBusy()) {
                    CraftingJobStatus jobStatus = cpu.getJobStatus();

                    // avoid null pointer exception
                    if (jobStatus == null)
                        continue;

                    if (jobStatus.crafting().what().equals(stack.getRight()))
                        return true;
                }
            }
        } else {
            if (craftingCPU.isBusy()) {
                CraftingJobStatus jobStatus = craftingCPU.getJobStatus();

                // avoid null pointer exception
                if (jobStatus == null)
                    return false;

                return jobStatus.crafting().what().equals(stack.getRight());
            }
        }

        return false;
    }

    public static long getTotalItemStorage(IGridNode node) {
        boolean ae2ThingsEnabled = ModList.get().isLoaded("ae2things");

        long total = 0;

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        if (!iterator.hasNext()) return 0;
        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if(entity == null) continue;

            InternalInventory inventory = entity.getInternalInventory();

            for(int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if(stack.isEmpty()) continue;

                if(stack.getItem() instanceof BasicStorageCell cell) {
                    if(cell.getKeyType().toString().equals("ae2:i")) {
                        total += cell.getBytes(null);
                    }
                } else if(ae2ThingsEnabled && stack.getItem() instanceof DISKDrive disk) {
                    if(disk.getKeyType().toString().equals("ae2:i")) {
                        total += disk.getBytes(null);
                    }
                }
            }
        }

        return total;
    }

    public static long getTotalFluidStorage(IGridNode node) {
        long total = 0;

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        if (!iterator.hasNext()) return 0;
        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if(entity == null) continue;

            InternalInventory inventory = entity.getInternalInventory();

            for(int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if(stack.isEmpty()) continue;

                if(stack.getItem() instanceof BasicStorageCell cell) {
                    if(cell.getKeyType().toString().equals("ae2:f")) {
                        total += cell.getBytes(null);
                    }
                }
            }
        }

        return total;
    }

    public static long getUsedItemStorage(IGridNode node) {
        boolean ae2ThingsEnabled = ModList.get().isLoaded("ae2things");
        long used = 0;

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        if (!iterator.hasNext()) return 0;
        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if(entity == null) continue;

            InternalInventory inventory = entity.getInternalInventory();

            for(int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if(stack.isEmpty()) continue;

                if(stack.getItem() instanceof BasicStorageCell cell) {
                    int bytesPerType = cell.getBytesPerType(null);

                    if(cell.getKeyType().toString().equals("ae2:i")) {
                        if(stack.getTag() == null) continue;
                        int numOfType = stack.getTag().getLongArray("amts").length;
                        long numItemsInCell = stack.getTag().getLong("ic");

                        used += ((int) Math.ceil(((double) numItemsInCell) / 8)) + ((long) bytesPerType * numOfType);
                    }
                } else if(ae2ThingsEnabled && stack.getItem() instanceof DISKDrive disk) {
                    if(disk.getKeyType().toString().equals("ae2:i")) {
                        if(stack.getTag() == null) continue;
                        long numItemsInCell = stack.getTag().getLong("ic");
                        used += ((int) Math.ceil(((double) numItemsInCell) / 8));
                    }
                }
            }
        }

        return used;
    }

    public static long getUsedFluidStorage(IGridNode node) {
        long used = 0;

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        if (!iterator.hasNext()) return 0;
        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if(entity == null) continue;

            InternalInventory inventory = entity.getInternalInventory();

            for(int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if(stack.getItem() instanceof BasicStorageCell cell) {
                    int bytesPerType = cell.getBytesPerType(null);

                    if(cell.getKeyType().toString().equals("ae2:f")) {
                        if(stack.getTag() == null) continue;
                        int numOfType = stack.getTag().getLongArray("amts").length;
                        long numBucketsInCell = stack.getTag().getLong("ic") / 1000;

                        used += ((int) Math.ceil(((double) numBucketsInCell) / 8)) + ((long) bytesPerType * numOfType);
                    }
                }
            }
        }

        return used;
    }

    public static long getAvailableItemStorage(IGridNode node) {
        return getTotalItemStorage(node) - getUsedItemStorage(node);
    }

    public static long getAvailableFluidStorage(IGridNode node) {
        return getTotalFluidStorage(node) - getUsedFluidStorage(node);
    }

    public static List<Object> listCells(IGridNode node) {
        boolean ae2ThingsEnabled = ModList.get().isLoaded("ae2things");
        List<Object> items = new ArrayList<>();

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        if (!iterator.hasNext()) return items;
        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if(entity == null) continue;

            InternalInventory inventory = entity.getInternalInventory();

            for(int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if(stack.isEmpty()) continue;

                if(stack.getItem() instanceof BasicStorageCell cell) {
                    items.add(getObjectFromCell(cell, stack));
                } else if(ae2ThingsEnabled && stack.getItem() instanceof DISKDrive disk) {
                    items.add(getObjectFromDisk(disk, stack));
                }
            }
        }

        return items;
    }

    private static Map<String, Object> getObjectFromCell(BasicStorageCell cell, ItemStack stack) {
        Map<String, Object> map = new HashMap<>();

        map.put("item", stack.getItem().toString());

        String cellType = "";

        if(cell.getKeyType().toString().equals("ae2:i")) {
            cellType = "item";
        } else if(cell.getKeyType().toString().equals("ae2:f")) {
            cellType = "fluid";
        }

        map.put("cellType", cellType);
        map.put("bytesPerType", cell.getBytesPerType(null));
        map.put("totalBytes", cell.getBytes(null));

        return map;
    }

    private static Map<String, Object> getObjectFromDisk(DISKDrive drive, ItemStack stack) {
        Map<String, Object> map = new HashMap<>();

        map.put("item", stack.getItem().toString());

        String cellType = "";

        if(drive.getKeyType().toString().equals("ae2:i")) {
            cellType = "item";
        } else if(drive.getKeyType().toString().equals("ae2:f")) {
            cellType = "fluid";
        }

        map.put("cellType", cellType);
        map.put("totalBytes", drive.getBytes(null));

        return map;
    }
}
