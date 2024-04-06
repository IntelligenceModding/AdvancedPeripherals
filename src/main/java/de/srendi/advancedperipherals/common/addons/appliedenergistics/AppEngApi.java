package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.*;
import appeng.api.storage.AEKeyFilter;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.items.storage.BasicStorageCell;
import appeng.parts.storagebus.StorageBusPart;
import com.the9grounds.aeadditions.item.storage.SuperStorageCell;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import io.github.projectet.ae2things.item.DISKDrive;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AppEngApi {

    public static Pair<Long, AEItemKey> findAEStackFromStack(MEStorage monitor, @Nullable ICraftingService crafting, ItemStack item) {
        return findAEStackFromFilter(monitor, crafting, ItemFilter.fromStack(item));
    }

    public static Pair<Long, AEItemKey> findAEStackFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, ItemFilter item) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEItemKey key && item.test(key.toStack()))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null)
            return Pair.of(0L, AEItemKey.of(ItemStack.EMPTY));

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof AEItemKey key && item.test(key.toStack()))
                return Pair.of(0L, key);
        }

        return Pair.of(0L, AEItemKey.of(ItemStack.EMPTY));
    }

    public static Pair<Long, AEFluidKey> findAEFluidFromStack(MEStorage monitor, @Nullable ICraftingService crafting, FluidStack item) {
        return findAEFluidFromFilter(monitor, crafting, FluidFilter.fromStack(item));
    }

    public static Pair<Long, AEFluidKey> findAEFluidFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, FluidFilter item) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEFluidKey key && item.test(key.toStack(1)))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null)
            return null;

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof AEFluidKey key && item.test(key.toStack(1)))
                return Pair.of(0L, key);
        }

        return null;
    }

    public static List<Object> listStacks(MEStorage monitor, ICraftingService service) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
            if (aeKey.getKey() instanceof AEItemKey itemKey) {
                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service));
            }
        }
        return items;
    }

    public static List<Object> listCraftableStacks(MEStorage monitor, ICraftingService service) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        Set<AEKey> craftables = service.getCraftables(AEKeyFilter.none());
        for (AEKey aeKey : craftables) {
            if (aeKey instanceof AEItemKey) {
                items.add(getObjectFromStack(Pair.of(keyCounter.get(aeKey), aeKey), service));
            }
        }
        return items;
    }

    public static List<Object> listFluids(MEStorage monitor, ICraftingService service) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEFluidKey itemKey) {
                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service));
            }
        }
        return items;
    }

    public static List<Object> listGases(MEStorage monitor, ICraftingService service, int flag) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (APAddons.appMekLoaded && aeKey.getKey() instanceof MekanismKey itemKey) {
                items.add(getObjectFromStack(Pair.of(aeKey.getLongValue(), itemKey), service));
            }
        }
        return items;
    }

    public static List<Object> listCraftableFluids(MEStorage monitor, ICraftingService service) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        Set<AEKey> craftables = service.getCraftables(AEKeyFilter.none());
        for (AEKey aeKey : craftables) {
            if (aeKey instanceof AEFluidKey) {
                items.add(getObjectFromStack(Pair.of(keyCounter.get(aeKey), aeKey), service));
            }
        }
        return items;
    }

    public static <T extends AEKey> Map<String, Object> getObjectFromStack(Pair<Long, T> stack, @Nullable ICraftingService service) {
        if (stack.getRight() == null)
            return Collections.emptyMap();
        if (stack.getRight() instanceof AEItemKey itemKey)
            return getObjectFromItemStack(Pair.of(stack.getLeft(), itemKey), service);
        if (stack.getRight() instanceof AEFluidKey fluidKey)
            return getObjectFromFluidStack(Pair.of(stack.getLeft(), fluidKey), service);
        if (APAddons.appMekLoaded && (stack.getRight() instanceof MekanismKey gasKey))
            return getObjectFromGasStack(Pair.of(stack.getLeft(), gasKey), service);

        AdvancedPeripherals.debug("Could not create table from unknown stack " + stack.getRight().getClass() + " - Report this to the maintainer of ap", Level.ERROR);
        return Collections.emptyMap();
    }

    private static Map<String, Object> getObjectFromItemStack(Pair<Long, AEItemKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> map = new HashMap<>();
        String displayName = stack.getRight().getDisplayName().getString();
        CompoundTag nbt = stack.getRight().toTag();
        long amount = stack.getLeft();
        map.put("fingerprint", ItemUtil.getFingerprint(stack.getRight().toStack()));
        map.put("name", ItemUtil.getRegistryKey(stack.getRight().getItem()).toString());
        map.put("amount", amount);
        map.put("displayName", displayName);
        map.put("nbt", NBTUtil.toLua(nbt));
        map.put("tags", LuaConverter.tagsToList(() -> stack.getRight().getItem().builtInRegistryHolder().tags()));
        map.put("isCraftable", craftingService != null && craftingService.isCraftable(stack.getRight()));

        return map;
    }

    private static Map<String, Object> getObjectFromFluidStack(Pair<Long, AEFluidKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> map = new HashMap<>();
        long amount = stack.getLeft();
        map.put("name", ForgeRegistries.FLUIDS.getKey(stack.getRight().getFluid()).toString());
        map.put("amount", amount);
        map.put("displayName", stack.getRight().getDisplayName().getString());
        map.put("tags", LuaConverter.tagsToList(() -> stack.getRight().getFluid().builtInRegistryHolder().tags()));
        map.put("isCraftable", craftingService != null && craftingService.isCraftable(stack.getRight()));

        return map;
    }

    private static Map<String, Object> getObjectFromGasStack(Pair<Long, MekanismKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> map = new HashMap<>();
        long amount = stack.getLeft();
        map.put("name", stack.getRight().getStack().getTypeRegistryName().toString());
        map.put("amount", amount);
        map.put("displayName", stack.getRight().getDisplayName().getString());
        map.put("tags", LuaConverter.tagsToList(() -> stack.getRight().getStack().getType().getTags()));

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
        map.put("craftingJob", cpu.getJobStatus() != null ? getObjectFromJob(cpu.getJobStatus()) : null);
        map.put("name", cpu.getName() != null ? cpu.getName().getString() : "Unnamed");
        map.put("selectionMode", cpu.getSelectionMode().toString());

        return map;
    }

    public static Map<String, Object> getObjectFromJob(CraftingJobStatus job) {
        Map<String, Object> map = new HashMap<>();
        map.put("storage", getObjectFromGenericStack(job.crafting()));
        map.put("elapsedTimeNanos", job.elapsedTimeNanos());
        map.put("totalItem", job.totalItems());
        map.put("progress", job.progress());

        return map;
    }

    public static Map<String, Object> getObjectFromGenericStack(GenericStack stack) {
        if (stack.what() == null)
            return Collections.emptyMap();
        if (stack.what() instanceof AEItemKey aeItemKey)
            return getObjectFromItemStack(Pair.of(stack.amount(), aeItemKey), null);
        if (stack.what() instanceof AEFluidKey aeFluidKey)
            return getObjectFromFluidStack(Pair.of(stack.amount(), aeFluidKey), null);
        return Collections.emptyMap();
    }

    public static MEStorage getMonitor(IGridNode node) {
        return node.getGrid().getService(IStorageService.class).getInventory();
    }

    public static boolean isItemCrafting(MEStorage monitor, ICraftingService grid, ItemFilter filter,
                                         @Nullable ICraftingCPU craftingCPU) {
        Pair<Long, AEItemKey> stack = AppEngApi.findAEStackFromFilter(monitor, grid, filter);

        // If the item stack does not exist, it cannot be crafted.
        if (stack == null)
            return false;

        // If the passed cpu is null, check all cpus
        if (craftingCPU == null) {
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

    public static boolean isFluidCrafting(MEStorage monitor, ICraftingService grid, FluidFilter filter,
                                          @Nullable ICraftingCPU craftingCPU) {
        Pair<Long, AEFluidKey> stack = AppEngApi.findAEFluidFromFilter(monitor, grid, filter);

        // If the fluid stack does not exist, it cannot be crafted.
        if (stack == null)
            return false;

        // If the passed cpu is null, check all cpus
        if (craftingCPU == null) {
            // Loop through all crafting cpus and check if the fluid is being crafted.
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
        long total = 0;

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if (entity == null)
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.items().getClass())) {
                        total += cell.getBytes(null);
                    }
                } else if (APAddons.aeThingsLoaded && stack.getItem() instanceof DISKDrive disk) {
                    if (disk.getKeyType().toString().equals("ae2:i")) {
                        total += disk.getBytes(null);
                    }
                } else if (APAddons.aeAdditionsLoaded && (stack.getItem() instanceof SuperStorageCell superStorageCell)) {
                    total += superStorageCell.getKiloBytes() * 1024;
                }
            }
        }

        iterator = node.getGrid().getMachineNodes(StorageBusPart.class).iterator();

        while (iterator.hasNext()) {
            StorageBusPart bus = (StorageBusPart) iterator.next().getService(IStorageProvider.class);
            net.minecraft.world.level.Level level = bus.getLevel();
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());
            BlockEntity connectedInventoryEntity = level.getBlockEntity(connectedInventoryPos);

            LazyOptional<IItemHandler> itemHandler = connectedInventoryEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (itemHandler.isPresent()) {
                IItemHandler handler = itemHandler.orElse(null);
                for (int i = 0; i < handler.getSlots(); i++) {
                    total += handler.getSlotLimit(i);
                }
            }
        }

        return total;
    }

    public static long getTotalFluidStorage(IGridNode node) {
        long total = 0;

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if (entity == null)
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.fluids().getClass())) {
                        total += cell.getBytes(null);
                    }
                } else if (APAddons.aeAdditionsLoaded && stack.getItem() instanceof SuperStorageCell superStorageCell) {
                    total += superStorageCell.getKiloBytes() * 1024;
                }
            }
        }

        iterator = node.getGrid().getMachineNodes(StorageBusPart.class).iterator();

        while (iterator.hasNext()) {
            StorageBusPart bus = (StorageBusPart) iterator.next().getService(IStorageProvider.class);
            net.minecraft.world.level.Level level = bus.getLevel();
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());
            BlockEntity connectedInventoryEntity = level.getBlockEntity(connectedInventoryPos);

            LazyOptional<IFluidHandler> fluidHandler = connectedInventoryEntity.getCapability(ForgeCapabilities.FLUID_HANDLER);
            if (fluidHandler.isPresent()) {
                IFluidHandler handler = fluidHandler.orElse(null);
                for (int i = 0; i < handler.getTanks(); i++) {
                    total += handler.getTankCapacity(i);
                }
            }
        }

        return total;
    }

    public static long getUsedItemStorage(IGridNode node) {
        long used = 0;

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if (entity == null)
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    int bytesPerType = cell.getBytesPerType(null);

                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.items().getClass())) {
                        if (stack.getTag() == null)
                            continue;
                        int numOfType = stack.getTag().getLongArray("amts").length;
                        long numItemsInCell = stack.getTag().getLong("ic");

                        used += ((int) Math.ceil(((double) numItemsInCell) / 8)) + ((long) bytesPerType * numOfType);
                    }
                } else if (APAddons.aeThingsLoaded && stack.getItem() instanceof DISKDrive disk) {
                    if (disk.getKeyType().toString().equals("ae2:i")) {
                        if (stack.getTag() == null)
                            continue;
                        long numBytesInCell = stack.getTag().getLong("ic");
                        used += numBytesInCell;
                    }
                } else if (APAddons.aeAdditionsLoaded && stack.getItem() instanceof SuperStorageCell) {
                    if (stack.getTag() == null)
                        continue;
                    long numItemsInCell = stack.getTag().getLong("ic");

                    used += numItemsInCell;
                }
            }
        }

        iterator = node.getGrid().getMachineNodes(StorageBusPart.class).iterator();

        while (iterator.hasNext()) {
            StorageBusPart bus = (StorageBusPart) iterator.next().getService(IStorageProvider.class);
            KeyCounter keyCounter = bus.getInternalHandler().getAvailableStacks();

            for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
                if (aeKey.getKey() instanceof AEItemKey) {
                    used += aeKey.getLongValue();
                }
            }
        }

        return used;
    }

    public static long getUsedFluidStorage(IGridNode node) {
        long used = 0;

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if (entity == null)
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    int bytesPerType = cell.getBytesPerType(null);

                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.fluids().getClass())) {
                        if (stack.getTag() == null)
                            continue;
                        int numOfType = stack.getTag().getLongArray("amts").length;
                        long numBucketsInCell = stack.getTag().getLong("ic") / 1000;

                        used += ((int) Math.ceil(((double) numBucketsInCell) / 8)) + ((long) bytesPerType * numOfType);
                    }
                } else if (APAddons.aeAdditionsLoaded && stack.getItem() instanceof SuperStorageCell superStorageCell) {
                    if (stack.getTag() == null)
                        continue;
                    long numItemsInCell = stack.getTag().getLong("ic");

                    used += numItemsInCell;
                }
            }
        }

        iterator = node.getGrid().getMachineNodes(StorageBusPart.class).iterator();

        while (iterator.hasNext()) {
            StorageBusPart bus = (StorageBusPart) iterator.next().getService(IStorageProvider.class);
            KeyCounter keyCounter = bus.getInternalHandler().getAvailableStacks();

            for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
                if (aeKey.getKey() instanceof AEFluidKey fluidKey) {
                    used += aeKey.getLongValue();
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
        List<Object> items = new ArrayList<>();

        Iterator<IGridNode> iterator = node.getGrid().getMachineNodes(DriveBlockEntity.class).iterator();

        if (!iterator.hasNext()) return items;
        while (iterator.hasNext()) {
            DriveBlockEntity entity = (DriveBlockEntity) iterator.next().getService(IStorageProvider.class);
            if (entity == null)
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof BasicStorageCell cell) {
                    items.add(getObjectFromCell(cell, stack));
                } else if (APAddons.aeThingsLoaded && stack.getItem() instanceof DISKDrive disk) {
                    items.add(getObjectFromDisk(disk, stack));
                } else if (APAddons.aeAdditionsLoaded && stack.getItem() instanceof SuperStorageCell superStorageCell) {
                    items.add(getObjectFromSuperCell(superStorageCell, stack));
                }
            }
        }

        return items;
    }

    private static Map<String, Object> getObjectFromCell(BasicStorageCell cell, ItemStack stack) {
        Map<String, Object> map = new HashMap<>();

        map.put("item", ItemUtil.getRegistryKey(stack.getItem()).toString());

        String cellType = "";

        if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.items().getClass())) {
            cellType = "item";
        } else if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.fluids().getClass())) {
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

        if (drive.getKeyType().toString().equals("ae2:i")) {
            cellType = "item";
        } else if (drive.getKeyType().toString().equals("ae2:f")) {
            cellType = "fluid";
        }

        map.put("cellType", cellType);
        map.put("totalBytes", drive.getBytes(null));

        return map;
    }

    private static Map<String, Object> getObjectFromSuperCell(SuperStorageCell cell, ItemStack stack) {
        Map<String, Object> map = new HashMap<>();

        map.put("item", stack.getItem().toString());

        String cellType = "all";

        map.put("cellType", cellType);
        map.put("totalBytes", cell.getBytes(stack));

        return map;
    }
}
