package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.crafting.IPatternDetails;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
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
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.helpers.iface.PatternContainer;
import appeng.items.storage.BasicStorageCell;
import appeng.me.cells.BasicCellHandler;
import appeng.me.cells.BasicCellInventory;
import appeng.parts.storagebus.StorageBusPart;
import com.the9grounds.aeadditions.item.storage.StorageCell;
import com.the9grounds.aeadditions.item.storage.SuperStorageCell;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import io.github.projectet.ae2things.item.DISKDrive;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.item.ChemicalStorageCell;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.common.tile.TileEntityChemicalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Finds a pattern from filters.
     *
     * @param grid         The grid to search patterns from.
     * @param level        The level of the grid.
     * @param inputFilter  The input filter to apply, can be null to ignore input filter.
     * @param outputFilter The output filter to apply, can be null to ignore output filter.
     * @return A Pair object containing the matched pattern and an error message if no pattern is found.
     * The pattern can be null if no pattern is found.
     * The error message is "NO_PATTERN_FOUND" if no pattern is found.
     */
    public static Pair<IPatternDetails, String> findPatternFromFilters(IGrid grid, Level level, @Nullable GenericFilter inputFilter, @Nullable GenericFilter outputFilter) {
        for (IPatternDetails pattern : getPatterns(grid, level)) {
            if (pattern.getInputs().length == 0)
                continue;
            if (pattern.getOutputs().length == 0)
                continue;

            boolean inputMatch = false;
            boolean outputMatch = false;

            if (inputFilter != null) {
                outerLoop:
                for (IPatternDetails.IInput input : pattern.getInputs()) {
                    for (GenericStack possibleInput : input.getPossibleInputs()) {
                        if (inputFilter.test(possibleInput)) {
                            inputMatch = true;
                            break outerLoop;
                        }
                    }
                }
            } else {
                inputMatch = true;
            }

            if (outputFilter != null) {
                for (GenericStack output : pattern.getOutputs()) {
                    if (outputFilter.test(output)) {
                        outputMatch = true;
                        break;
                    }
                }
            } else {
                outputMatch = true;
            }

            if (inputMatch && outputMatch)
                return Pair.of(pattern, null);
        }

        return Pair.of(null, "NO_PATTERN_FOUND");
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

    public static List<Object> listGases(MEStorage monitor, ICraftingService service) {
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

    public static List<IPatternDetails> getPatterns(IGrid grid, Level level) {
        List<IPatternDetails> patterns = new ArrayList<>();
        for (var machineClass : grid.getMachineClasses()) {
            var containerClass = tryCastMachineToContainer(machineClass);
            if (containerClass == null)
                continue;

            for (var container : grid.getActiveMachines(containerClass)) {
                for (ItemStack patternItem : container.getTerminalPatternInventory()) {
                    if (patternItem.getItem() instanceof EncodedPatternItem item) {
                        IPatternDetails patternDetails = item.decode(patternItem, level, false);
                        if (patternDetails == null)
                            continue;

                        patterns.add(patternDetails);
                    }
                }
            }
        }
        return patterns;
    }

    public static List<Object> listPatterns(IGrid grid, Level level) {
        return getPatterns(grid, level).stream().map(AppEngApi::getObjectFromPattern).collect(Collectors.toList());
    }

    public static List<Object> listDrives(IGrid grid) {
        List<Object> drives = new ArrayList<>();

        for (IGridNode node : grid.getMachineNodes(DriveBlockEntity.class)) {
            DriveBlockEntity drive = (DriveBlockEntity) node.getService(IStorageProvider.class);

            // A normal drive has a cellCount of 10
            if (drive == null || drive.getCellCount() != 10)
                continue;

            drives.add(getObjectFromDrive(drive));
        }

        return drives;
    }

    private static Class<? extends PatternContainer> tryCastMachineToContainer(Class<?> machineClass) {
        if (PatternContainer.class.isAssignableFrom(machineClass))
            return machineClass.asSubclass(PatternContainer.class);

        return null;
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

        AdvancedPeripherals.debug("Could not create table from unknown stack " + stack.getRight().getClass() + " - Report this to the maintainer of ap", org.apache.logging.log4j.Level.ERROR);
        return Collections.emptyMap();
    }

    public static Map<Object, Object> getObjectFromDrive(DriveBlockEntity drive) {
        Map<Object, Object> map = new HashMap<>();

        map.put("powered", drive.isPowered());

        long totalBytes = 0;
        long usedBytes = 0;

        if (drive.getCellCount() != 10)
            return map;

        List<Object> driveCells = new ArrayList<>();
        for (ItemStack item : drive.getInternalInventory()) {
            if (item.getItem() instanceof BasicStorageCell cell) {
                BasicCellInventory cellInventory = BasicCellHandler.INSTANCE.getCellInventory(item, null);
                totalBytes += cellInventory.getTotalBytes();
                usedBytes += cellInventory.getUsedBytes();

                driveCells.add(getObjectFromCell(cell, item));
            }
        }

        map.put("usedBytes", usedBytes);
        map.put("totalBytes", totalBytes);
        map.put("cells", driveCells);
        map.put("priority", drive.getPriority());
        map.put("menuIcon", LuaConverter.itemToObject(drive.getMainMenuIcon().getItem()));
        map.put("position", LuaConverter.posToObject(drive.getBlockPos()));
        map.put("name", drive.getCustomInventoryName().getString());

        return map;
    }

    public static Map<Object, Object> getObjectFromCell(BasicStorageCell cell, ItemStack cellItem) {
        Map<Object, Object> map = new HashMap<>();
        BasicCellInventory cellInventory = BasicCellHandler.INSTANCE.getCellInventory(cellItem, null);

        map.put("item", LuaConverter.itemToObject(cellItem.getItem()));
        map.put("type", cell.getKeyType().toString());
        map.put("bytes", cell.getBytes(cellItem));
        map.put("bytesPerType", cell.getBytesPerType(cellItem));
        map.put("usedBytes", cellInventory.getUsedBytes());
        map.put("totalTypes", cell.getTotalTypes(cellItem));
        map.put("fuzzyMode", cell.getFuzzyMode(cellItem).toString());

        return map;
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

    public static Map<String, Object> getObjectFromPattern(IPatternDetails pattern) {
        Map<String, Object> map = new HashMap<>();

        map.put("inputs", Arrays.stream(pattern.getInputs()).map(AppEngApi::getObjectFromPatternInput).collect(Collectors.toList()));
        map.put("outputs", Arrays.stream(pattern.getOutputs()).map(AppEngApi::getObjectFromGenericStack).collect(Collectors.toList()));
        map.put("primaryOutput", getObjectFromGenericStack(pattern.getPrimaryOutput()));
        return map;
    }

    public static Map<String, Object> getObjectFromPatternInput(IPatternDetails.IInput patternInput) {
        Map<String, Object> map = new HashMap<>();
        map.put("primaryInput", getObjectFromGenericStack(patternInput.getPossibleInputs()[0]));
        map.put("possibleInputs",
                Arrays.stream(Arrays.copyOfRange(patternInput.getPossibleInputs(), 1, patternInput.getPossibleInputs().length))
                        .map(AppEngApi::getObjectFromGenericStack));
        map.put("multiplier", patternInput.getMultiplier());
        map.put("remaining", patternInput.getRemainingKey(patternInput.getPossibleInputs()[0].what()));
        return map;
    }

    public static Map<String, Object> getObjectFromCPU(ICraftingCPU cpu, boolean recursive) {
        Map<String, Object> map = new HashMap<>();
        long storage = cpu.getAvailableStorage();
        int coProcessors = cpu.getCoProcessors();
        boolean isBusy = cpu.isBusy();
        map.put("storage", storage);
        map.put("coProcessors", coProcessors);
        map.put("isBusy", isBusy);
        if (!recursive)
            map.put("craftingJob", cpu.getJobStatus() != null ? getObjectFromJob(cpu.getJobStatus(), null) : null);
        map.put("name", cpu.getName() != null ? cpu.getName().getString() : "Unnamed");
        map.put("selectionMode", cpu.getSelectionMode().toString());

        return map;
    }

    public static Map<String, Object> getObjectFromJob(CraftingJobStatus job, @Nullable ICraftingCPU cpu) {
        Map<String, Object> map = new HashMap<>();
        map.put("storage", getObjectFromGenericStack(job.crafting()));
        map.put("elapsedTimeNanos", job.elapsedTimeNanos());
        map.put("totalItem", job.totalItems());
        map.put("progress", job.progress());

        if (cpu != null)
            map.put("cpu", getObjectFromCPU(cpu, true));

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

    /// External Storage
    /// Total

    public static long getTotalExternalItemStorage(IGridNode node) {
        long total = 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(StorageBusPart.class)) {
            StorageBusPart bus = (StorageBusPart) iGridNode.getService(IStorageProvider.class);
            Level level = bus.getLevel();
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());
            BlockEntity connectedInventoryEntity = level.getBlockEntity(connectedInventoryPos);

            if (connectedInventoryEntity == null)
                continue;

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

    public static long getTotalExternalFluidStorage(IGridNode node) {
        long total = 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(StorageBusPart.class)) {
            StorageBusPart bus = (StorageBusPart) iGridNode.getService(IStorageProvider.class);
            Level level = bus.getLevel();
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());
            BlockEntity connectedInventoryEntity = level.getBlockEntity(connectedInventoryPos);

            if (connectedInventoryEntity == null)
                continue;

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

    public static long getTotalExternalChemicalStorage(IGridNode node) {
        long total = 0;

        if (!APAddons.appMekLoaded)
            return 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(StorageBusPart.class)) {
            StorageBusPart bus = (StorageBusPart) iGridNode.getService(IStorageProvider.class);
            Level level = bus.getLevel();
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());
            BlockEntity connectedInventoryEntity = level.getBlockEntity(connectedInventoryPos);

            if (connectedInventoryEntity == null)
                continue;

            if (connectedInventoryEntity instanceof TileEntityChemicalTank tank) {
                MergedChemicalTank.Current current = tank.getChemicalTank().getCurrent() == MergedChemicalTank.Current.EMPTY ? MergedChemicalTank.Current.GAS : tank.getChemicalTank().getCurrent();
                total += tank.getChemicalTank().getTankFromCurrent(current).getCapacity();
            }
        }

        return total;
    }

    /// Used

    public static long getUsedExternalItemStorage(IGridNode node) {
        long used = 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(StorageBusPart.class)) {
            StorageBusPart bus = (StorageBusPart) iGridNode.getService(IStorageProvider.class);
            KeyCounter keyCounter = bus.getInternalHandler().getAvailableStacks();

            for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
                if (aeKey.getKey() instanceof AEItemKey)
                    used += aeKey.getLongValue();
            }
        }

        return used;
    }

    public static long getUsedExternalFluidStorage(IGridNode node) {
        long used = 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(StorageBusPart.class)) {
            StorageBusPart bus = (StorageBusPart) iGridNode.getService(IStorageProvider.class);
            KeyCounter keyCounter = bus.getInternalHandler().getAvailableStacks();

            for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
                if (aeKey.getKey() instanceof AEFluidKey)
                    used += aeKey.getLongValue();
            }
        }

        return used;
    }

    public static long getUsedExternalChemicalStorage(IGridNode node) {
        long used = 0;

        if (!APAddons.appMekLoaded)
            return 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(StorageBusPart.class)) {
            StorageBusPart bus = (StorageBusPart) iGridNode.getService(IStorageProvider.class);
            KeyCounter keyCounter = bus.getInternalHandler().getAvailableStacks();

            for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
                if (aeKey.getKey() instanceof MekanismKey)
                    used += aeKey.getLongValue();
            }
        }

        return used;
    }

    /// Internal Storage
    /// Total

    public static long getTotalItemStorage(IGridNode node) {
        long total = 0;

        // note: do not query DriveBlockEntity.class specifically here, because it will avoid subclasses, e.g. the ME Extended Drive from ExtendedAE
        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        while (iterator.hasNext()) {
            if (!(iterator.next().getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
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
                    total += superStorageCell.getKiloBytes() * 1024L;
                } else if (APAddons.aeAdditionsLoaded && (stack.getItem() instanceof StorageCell storageCell)) {
                    if (storageCell.getKeyType() != AEKeyType.items())
                        continue;
                    total += storageCell.getKiloBytes() * 1024;
                }
            }
        }
        return total;
    }

    public static long getTotalFluidStorage(IGridNode node) {
        long total = 0;

        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        while (iterator.hasNext()) {
            if (!(iterator.next().getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
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
                    total += superStorageCell.getKiloBytes() * 1024L;
                } else if (APAddons.aeAdditionsLoaded && (stack.getItem() instanceof StorageCell storageCell)) {
                    if (storageCell.getKeyType() != AEKeyType.fluids())
                        continue;
                    total += storageCell.getKiloBytes() * 1024;
                }
            }
        }

        return total;
    }

    public static long getTotalChemicalStorage(IGridNode node) {
        long total = 0;

        if (!APAddons.appMekLoaded)
            return 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(DriveBlockEntity.class)) {
            DriveBlockEntity entity = (DriveBlockEntity) iGridNode.getService(IStorageProvider.class);
            if (entity == null)
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof ChemicalStorageCell cell) {
                    if (cell.getKeyType() instanceof MekanismKeyType) {
                        total += cell.getBytes(null);
                    }
                }
            }
        }

        return total;
    }

    /// Used

    public static long getUsedItemStorage(IGridNode node) {
        long used = 0;

        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        while (iterator.hasNext()) {
            if (!(iterator.next().getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.items().getClass())) {
                        BasicCellInventory cellInventory = BasicCellHandler.INSTANCE.getCellInventory(stack, null);

                        used += cellInventory.getUsedBytes();
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
                } else if (APAddons.aeAdditionsLoaded && stack.getItem() instanceof StorageCell storageCell) {
                    if (storageCell.getKeyType() != AEKeyType.items())
                        continue;
                    if (stack.getTag() == null)
                        continue;
                    long numItemsInCell = stack.getTag().getLong("ic");

                    used += numItemsInCell;
                }
            }
        }

        return used;
    }

    public static long getUsedFluidStorage(IGridNode node) {
        long used = 0;

        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        while (iterator.hasNext()) {
            if (!(iterator.next().getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.fluids().getClass())) {
                        BasicCellInventory cellInventory = BasicCellHandler.INSTANCE.getCellInventory(stack, null);

                        used += cellInventory.getUsedBytes();
                    }
                } else if (APAddons.aeAdditionsLoaded && stack.getItem() instanceof SuperStorageCell) {
                    if (stack.getTag() == null)
                        continue;
                    long numItemsInCell = stack.getTag().getLong("ic");

                    used += numItemsInCell;
                } else if (APAddons.aeAdditionsLoaded && stack.getItem() instanceof StorageCell storageCell) {
                    if (storageCell.getKeyType() != AEKeyType.fluids())
                        continue;
                    if (stack.getTag() == null)
                        continue;
                    long numItemsInCell = stack.getTag().getLong("ic");

                    used += numItemsInCell;
                }
            }
        }

        return used;
    }

    public static long getUsedChemicalStorage(IGridNode node) {
        long used = 0;

        if (!APAddons.appMekLoaded)
            return 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(DriveBlockEntity.class)) {
            DriveBlockEntity entity = (DriveBlockEntity) iGridNode.getService(IStorageProvider.class);
            if (entity == null)
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.getItem() instanceof ChemicalStorageCell) {
                    BasicCellInventory cellInventory = BasicCellHandler.INSTANCE.getCellInventory(stack, null);

                    used = cellInventory.getUsedBytes() / MekanismKeyType.TYPE.getAmountPerByte();
                }
            }
        }

        return used;
    }

    /// Available Storage

    /**
     * Calculates the available item storage on a given grid node.
     * It subtracts the used item storage from the total item storage.
     *
     * @param node The grid node to calculate the available item storage for.
     * @return The available item storage in bytes.
     */
    public static long getAvailableItemStorage(IGridNode node) {
        return getTotalItemStorage(node) - getUsedItemStorage(node);
    }

    /**
     * Calculates the available fluid storage in a given grid node.
     *
     * @param node The grid node to calculate the available fluid storage for.
     * @return The available fluid storage in bytes.
     */
    public static long getAvailableFluidStorage(IGridNode node) {
        return getTotalFluidStorage(node) - getUsedFluidStorage(node);
    }

    public static long getAvailableChemicalStorage(IGridNode node) {
        return getTotalChemicalStorage(node) - getUsedChemicalStorage(node);
    }

    /**
     * Calculates the available external item storage of a given grid node.
     *
     * @param node The grid node for which to calculate the available external item storage.
     * @return The available external item storage.
     */
    public static long getAvailableExternalItemStorage(IGridNode node) {
        return getTotalExternalItemStorage(node) - getUsedExternalItemStorage(node);
    }

    /**
     * Calculates the available external fluid storage on a given grid node by subtracting the used external fluid storage
     * from the total external fluid storage.
     *
     * @param node The grid node on which to calculate the available external fluid storage.
     * @return The available external fluid storage on the grid node.
     */
    public static long getAvailableExternalFluidStorage(IGridNode node) {
        return getTotalExternalFluidStorage(node) - getUsedExternalFluidStorage(node);
    }

    public static long getAvailableExternalChemicalStorage(IGridNode node) {
        return getTotalExternalChemicalStorage(node) - getUsedExternalChemicalStorage(node);
    }

    public static ICraftingCPU getCraftingCPU(IGridNode node, String cpuName) {
        if (cpuName.isEmpty()) return null;
        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        if (grid == null) return null;

        Iterator<ICraftingCPU> iterator = grid.getCpus().iterator();
        if (!iterator.hasNext()) return null;

        while (iterator.hasNext()) {
            ICraftingCPU cpu = iterator.next();

            if (cpu.getName() != null && cpu.getName().getString().equals(cpuName)) {
                return cpu;
            }
        }

        return null;
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

                if (stack.getItem() instanceof IBasicCellItem cell) {
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

    private static Map<String, Object> getObjectFromCell(IBasicCellItem cell, ItemStack stack) {
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
