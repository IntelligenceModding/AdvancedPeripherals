package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.AECapabilities;
import appeng.api.crafting.IPatternDetails;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.AEKeyFilter;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.crafting.execution.CraftingCpuLogic;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.items.storage.BasicStorageCell;
import appeng.me.cells.BasicCellHandler;
import appeng.me.cells.BasicCellInventory;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.parts.storagebus.StorageBusPart;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import io.github.projectet.ae2things.item.DISKDrive;
import io.github.projectet.ae2things.storage.DISKCellHandler;
import io.github.projectet.ae2things.storage.DISKCellInventory;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import me.ramidzkh.mekae2.item.ChemicalStorageCell;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.tile.TileEntityChemicalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AppEngApi {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, BlockEntityTypes.ME_BRIDGE.get(), (blockEntity, side) -> blockEntity);
    }

    public static Pair<Long, AEItemKey> findAEStackFromStack(MEStorage monitor, @Nullable ICraftingService crafting, ItemStack item) {
        return findAEStackFromFilter(monitor, crafting, ItemFilter.fromStack(item));
    }

    public static Pair<Long, AEItemKey> findAEStackFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, ItemFilter filter) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEItemKey key && filter.test(key.toStack()))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null) return Pair.of(0L, AEItemKey.of(ItemStack.EMPTY));

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof AEItemKey key && filter.test(key.toStack())) return Pair.of(0L, key);
        }

        return Pair.of(0L, AEItemKey.of(ItemStack.EMPTY));
    }

    public static Pair<Long, AEFluidKey> findAEFluidFromStack(MEStorage monitor, @Nullable ICraftingService crafting, FluidStack item) {
        return findAEFluidFromFilter(monitor, crafting, FluidFilter.fromStack(item));
    }

    public static Pair<Long, AEFluidKey> findAEFluidFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, FluidFilter filter) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEFluidKey key && filter.test(key.toStack(1)))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null) return Pair.of(0L, AEFluidKey.of(FluidStack.EMPTY));

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof AEFluidKey key && filter.test(key.toStack(1))) return Pair.of(0L, key);
        }

        return Pair.of(0L, AEFluidKey.of(FluidStack.EMPTY));
    }

    public static Pair<Long, MekanismKey> findAEChemicalFromStack(MEStorage monitor, @Nullable ICraftingService crafting, ChemicalStack stack) {
        return findAEChemicalFromFilter(monitor, crafting, ChemicalFilter.fromStack(stack));
    }

    public static Pair<Long, MekanismKey> findAEChemicalFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, ChemicalFilter filter) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof MekanismKey key && filter.test(key.getStack()))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null) return Pair.of(0L, MekanismKey.of(ChemicalStack.EMPTY));

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof MekanismKey key && filter.test(key.getStack())) return Pair.of(0L, key);
        }

        return Pair.of(0L, MekanismKey.of(ChemicalStack.EMPTY));
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
    public static Pair<IPatternDetails, String> findPatternFromFilters(IGrid grid, net.minecraft.world.level.Level level, @Nullable GenericFilter<?> inputFilter, @Nullable GenericFilter<?> outputFilter) {
        for (IPatternDetails pattern : getPatterns(grid, level)) {
            if (pattern.getInputs().length == 0)
                continue;
            if (pattern.getOutputs().isEmpty())
                continue;

            boolean inputMatch = false;
            boolean outputMatch = false;

            if (inputFilter != null) {
                outerLoop:
                for (IPatternDetails.IInput input : pattern.getInputs()) {
                    for (GenericStack possibleInput : input.getPossibleInputs()) {
                        if (inputFilter.testAE(possibleInput)) {
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
                    if (outputFilter.testAE(output)) {
                        outputMatch = true;
                        break;
                    }
                }
            } else {
                outputMatch = true;
            }

            if (inputMatch && outputMatch) return Pair.of(pattern, null);
        }

        return Pair.of(null, StatusConstants.NOT_FOUND.toString());
    }

    public static List<Object> listItems(MEStorage monitor, ICraftingService service, ItemFilter filter) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
            if (aeKey.getKey() instanceof AEItemKey itemKey && filter.test(itemKey.toStack())) {
                items.add(parseAeStack(Pair.of(aeKey.getLongValue(), itemKey), service));
            }
        }
        return items;
    }

    public static List<Object> listCraftableItems(MEStorage monitor, ICraftingService service, ItemFilter filter) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        Set<AEKey> craftables = service.getCraftables(AEKeyFilter.none());
        for (AEKey aeKey : craftables) {
            if (aeKey instanceof AEItemKey itemKey && filter.test(itemKey.toStack())) {
                items.add(parseAeStack(Pair.of(keyCounter.get(aeKey), aeKey), service));
            }
        }
        return items;
    }

    public static List<Object> listFluids(MEStorage monitor, ICraftingService service, FluidFilter filter) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (aeKey.getKey() instanceof AEFluidKey fluidKey && filter.test(fluidKey.toStack(1))) {
                items.add(parseAeStack(Pair.of(aeKey.getLongValue(), fluidKey), service));
            }
        }
        return items;
    }

    public static List<Object> listChemicals(MEStorage monitor, ICraftingService service, ChemicalFilter filter) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (APAddon.APP_MEKANISTICS.isLoaded() && aeKey.getKey() instanceof MekanismKey mekanismKey && filter.test(mekanismKey.getStack())) {
                items.add(parseAeStack(Pair.of(aeKey.getLongValue(), mekanismKey), service));
            }
        }
        return items;
    }

    public static List<Object> listCraftableFluids(MEStorage monitor, ICraftingService service, FluidFilter filter) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        Set<AEKey> craftables = service.getCraftables(AEKeyFilter.none());
        for (AEKey aeKey : craftables) {
            if (aeKey instanceof AEFluidKey fluidKey && filter.test(fluidKey.toStack(1))) {
                items.add(parseAeStack(Pair.of(keyCounter.get(aeKey), aeKey), service));
            }
        }
        return items;
    }

    public static List<Object> listCraftableChemicals(MEStorage monitor, ICraftingService service, ChemicalFilter filter) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        Set<AEKey> craftables = service.getCraftables(AEKeyFilter.none());
        for (AEKey aeKey : craftables) {
            if (aeKey instanceof MekanismKey mekanismKey && filter.test(mekanismKey.getStack())) {
                items.add(parseAeStack(Pair.of(keyCounter.get(aeKey), aeKey), service));
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
                    if (patternItem.getItem() instanceof EncodedPatternItem<?> item) {
                        IPatternDetails patternDetails = item.decode(patternItem, level);
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
        return getPatterns(grid, level).stream().map(AppEngApi::parsePattern).collect(Collectors.toList());
    }

    public static List<Object> listDrives(IGrid grid) {
        List<Object> drives = new ArrayList<>();

        for (IGridNode node : grid.getMachineNodes(DriveBlockEntity.class)) {
            DriveBlockEntity drive = (DriveBlockEntity) node.getService(IStorageProvider.class);

            // A normal drive has a cellCount of 10
            if (drive == null || drive.getCellCount() != 10)
                continue;

            drives.add(parseDrive(drive));
        }

        return drives;
    }

    private static Class<? extends PatternContainer> tryCastMachineToContainer(Class<?> machineClass) {
        if (PatternContainer.class.isAssignableFrom(machineClass))
            return machineClass.asSubclass(PatternContainer.class);

        return null;
    }

    public static <T extends AEKey> Map<String, Object> parseAeStack(Pair<Long, T> stack, @Nullable ICraftingService service) {
        if (stack == null || stack.getRight() == null) return Collections.emptyMap();
        if (stack.getRight() instanceof AEItemKey itemKey)
            return parseItemStack(Pair.of(stack.getLeft(), itemKey), service);
        if (stack.getRight() instanceof AEFluidKey fluidKey)
            return parseFluidStack(Pair.of(stack.getLeft(), fluidKey), service);
        if (APAddon.APP_MEKANISTICS.isLoaded() && (stack.getRight() instanceof MekanismKey gasKey))
            return parseChemStack(Pair.of(stack.getLeft(), gasKey));

        AdvancedPeripherals.debug("Could not create table from unknown stack " + stack.getRight().getClass() + " - Report this to the maintainer of ap", org.apache.logging.log4j.Level.WARN);
        return Collections.emptyMap();
    }

    public static Map<String, Object> parseGenericStack(GenericStack stack) {
        if (stack.what() == null) return Collections.emptyMap();
        if (stack.what() instanceof AEItemKey aeItemKey)
            return parseItemStack(Pair.of(stack.amount(), aeItemKey), null);
        if (stack.what() instanceof AEFluidKey aeFluidKey)
            return parseFluidStack(Pair.of(stack.amount(), aeFluidKey), null);

        AdvancedPeripherals.debug("Could not create table from unknown stack " + stack.getClass() + " - Report this to the maintainer of ap", org.apache.logging.log4j.Level.WARN);
        return Collections.emptyMap();
    }

    public static List<Object> parseKeyCounter(KeyCounter counter) {
        List<Object> parsedKeys = new ArrayList<>();
        for (AEKey key : counter.keySet()) {
            parsedKeys.add(parseGenericStack(new GenericStack(key, counter.get(key))));
        }

        return parsedKeys;
    }

    public static Map<Object, Object> parseDrive(DriveBlockEntity drive) {
        Map<Object, Object> map = new HashMap<>();

        map.put("powered", drive.isPowered());

        long totalBytes = 0;
        long usedBytes = 0;

        if (drive.getCellCount() != 10) return map;

        List<Object> driveCells = new ArrayList<>();
        for (ItemStack item : drive.getInternalInventory()) {
            if (item.getItem() instanceof BasicStorageCell cell) {
                BasicCellInventory cellInventory = BasicCellHandler.INSTANCE.getCellInventory(item, null);
                totalBytes += cellInventory.getTotalBytes();
                usedBytes += cellInventory.getUsedBytes();

                driveCells.add(parseCell(cell, item));
            }
        }

        map.put("usedBytes", usedBytes);
        map.put("totalBytes", totalBytes);
        map.put("cells", driveCells);
        map.put("priority", drive.getPriority());
        map.put("menuIcon", LuaConverter.itemToObject(drive.getMainMenuIcon().getItem()));
        map.put("position", LuaConverter.posToObject(drive.getBlockPos()));
        map.put("name", drive.hasCustomName() ? drive.getCustomName().getString() : drive.getDisplayName().getString());

        return map;
    }

    public static Map<Object, Object> parseCell(IBasicCellItem cell, ItemStack cellItem) {
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

    private static Map<String, Object> parseItemStack(Pair<Long, AEItemKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> map = LuaConverter.itemStackToObject(stack.getRight().toStack());
        map.put("isCraftable", craftingService != null && craftingService.isCraftable(stack.getRight()));
        map.put("count", stack.getLeft());
        return map;
    }

    private static Map<String, Object> parseFluidStack(Pair<Long, AEFluidKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> map = LuaConverter.fluidStackToObject(stack.getRight().toStack(1));
        map.put("count", stack.getLeft());
        map.put("isCraftable", craftingService != null && craftingService.isCraftable(stack.getRight()));
        return map;
    }

    private static Map<String, Object> parseChemStack(Pair<Long, MekanismKey> stack) {
        Map<String, Object> map = new HashMap<>();
        long amount = stack.getLeft();
        map.put("name", stack.getRight().getStack().getChemicalHolder().getRegisteredName());
        map.put("amount", amount);
        map.put("displayName", stack.getRight().getDisplayName().getString());
        map.put("tags", LuaConverter.tagsToList(() -> stack.getRight().getStack().getTags()));

        return map;
    }

    public static Map<String, Object> parsePattern(IPatternDetails pattern) {
        Map<String, Object> map = new HashMap<>();

        map.put("inputs", Arrays.stream(pattern.getInputs()).map(AppEngApi::parsePatternInput).collect(Collectors.toList()));
        map.put("outputs", pattern.getOutputs().stream().map(AppEngApi::parseGenericStack).collect(Collectors.toList()));
        map.put("primaryOutput", parseGenericStack(pattern.getPrimaryOutput()));
        return map;
    }

    public static Map<String, Object> parsePatternInput(IPatternDetails.IInput patternInput) {
        Map<String, Object> map = new HashMap<>();
        map.put("primaryInput", parseGenericStack(patternInput.getPossibleInputs()[0]));
        map.put("possibleInputs", Arrays.stream(Arrays.copyOfRange(patternInput.getPossibleInputs(), 1, patternInput.getPossibleInputs().length)).map(AppEngApi::parseGenericStack));
        map.put("multiplier", patternInput.getMultiplier());
        map.put("remaining", patternInput.getRemainingKey(patternInput.getPossibleInputs()[0].what()));
        return map;
    }

    public static Map<String, Object> parseCraftingCPU(ICraftingCPU cpu, boolean recursive) {
        Map<String, Object> map = new HashMap<>();
        long storage = cpu.getAvailableStorage();
        int coProcessors = cpu.getCoProcessors();
        boolean isBusy = cpu.isBusy();
        map.put("storage", storage);
        map.put("coProcessors", coProcessors);
        map.put("isBusy", isBusy);
        if (!recursive)
            map.put("craftingJob", cpu.getJobStatus() != null ? parseCraftingJob(cpu.getJobStatus(), null, null) : null);
        map.put("name", cpu.getName() != null ? cpu.getName().getString() : "Unnamed");
        map.put("selectionMode", cpu.getSelectionMode().toString());

        return map;
    }

    public static Object parseCraftingJob(CraftingJobStatus status, @Nullable AECraftJob craftJob, @Nullable ICraftingCPU cpu) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("bridge_id", craftJob == null ? -1 : craftJob.getId());
        properties.put("quantity", status.crafting().amount());
        properties.put("resource", parseGenericStack(status.crafting()));

        if (cpu != null) {
            CraftingCpuLogic craftingCpuLogic = ((CraftingCPUCluster) cpu).craftingLogic;
            long pending = craftingCpuLogic.getPendingOutputs(status.crafting().what());
            long active = craftingCpuLogic.getWaitingFor(status.crafting().what());
            long crafted = status.crafting().amount() - (pending + active);
            properties.put("completion", crafted / (double) status.crafting().amount());
            properties.put("crafted", crafted);

            properties.put("id", craftingCpuLogic.getLastLink().getCraftingID().toString());

            properties.put("cpu", parseCraftingCPU(cpu, true));
        }

        return properties;
    }

    public static MEStorage getMonitor(IGridNode node) {
        return node.getGrid().getService(IStorageService.class).getInventory();
    }

    public static boolean isCrafting(ICraftingService grid, GenericFilter<?> filter, @Nullable ICraftingCPU craftingCPU) {

        // If the passed cpu is null, check all cpus
        if (craftingCPU == null) {
            // Loop through all crafting cpus and check if the item is being crafted.
            for (ICraftingCPU cpu : grid.getCpus()) {
                if (cpu.isBusy()) {
                    CraftingJobStatus jobStatus = cpu.getJobStatus();

                    // avoid null pointer exception
                    if (jobStatus == null)
                        continue;

                    if (filter.testAE(jobStatus.crafting()))
                        return true;
                }
            }
        } else {
            if (craftingCPU.isBusy()) {
                CraftingJobStatus jobStatus = craftingCPU.getJobStatus();

                // avoid null pointer exception
                if (jobStatus == null) return false;

                return filter.testAE(jobStatus.crafting());
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
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());

            IItemHandler itemHandler = InventoryUtil.extractHandler(null, bus.getLevel(), connectedInventoryPos, bus.getSide());
            if (itemHandler != null) {
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stack = itemHandler.getStackInSlot(i);

                    // Use 64 as a rough estimate if no item is in that slot
                    if (stack.isEmpty()) total += 64;

                    // If the slot is not empty, use the max stack size of that stack
                    total += stack.getMaxStackSize();
                }
            }
        }

        return total;
    }

    public static long getTotalExternalFluidStorage(IGridNode node) {
        long total = 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(StorageBusPart.class)) {
            StorageBusPart bus = (StorageBusPart) iGridNode.getService(IStorageProvider.class);
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());

            IFluidHandler fluidHandler = FluidUtil.extractHandler(null, bus.getLevel(), connectedInventoryPos, bus.getSide());
            if (fluidHandler != null) {
                for (int i = 0; i < fluidHandler.getTanks(); i++) {
                    total += fluidHandler.getTankCapacity(i);
                }
            }
        }

        return total;
    }

    public static long getTotalExternalChemicalStorage(IGridNode node) {
        long total = 0;

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(StorageBusPart.class)) {
            StorageBusPart bus = (StorageBusPart) iGridNode.getService(IStorageProvider.class);
            net.minecraft.world.level.Level level = bus.getLevel();
            BlockPos connectedInventoryPos = bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide());
            BlockEntity connectedInventoryEntity = level.getBlockEntity(connectedInventoryPos);

            if (connectedInventoryEntity == null)
                continue;

            if (connectedInventoryEntity instanceof TileEntityChemicalTank tank) {
                total += tank.getChemicalTank().getCapacity();
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
                if (aeKey.getKey() instanceof AEItemKey) used += aeKey.getLongValue();
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
                if (aeKey.getKey() instanceof AEFluidKey) used += aeKey.getLongValue();
            }
        }

        return used;
    }

    public static long getUsedExternalChemicalStorage(IGridNode node) {
        long used = 0;

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return 0;

        for (IGridNode iGridNode : node.getGrid().getMachineNodes(StorageBusPart.class)) {
            StorageBusPart bus = (StorageBusPart) iGridNode.getService(IStorageProvider.class);
            KeyCounter keyCounter = bus.getInternalHandler().getAvailableStacks();

            for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
                if (aeKey.getKey() instanceof MekanismKey) used += aeKey.getLongValue();
            }
        }

        return used;
    }

    /// Internal Storage
    /// Total

    public static long getTotalItemStorage(IGridNode node) {
        long total = 0;

        // note: do not query DriveBlockEntity.class specifically here, because it will avoid subclasses, e.g. the ME Extended Drive from ExtendedAE
        for (IGridNode iGridNode : node.getGrid().getNodes()) {
            if (!(iGridNode.getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
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
                } else if (APAddon.AE2_THINGS.isLoaded() && stack.getItem() instanceof DISKDrive disk) {
                    if (disk.getKeyType().toString().equals("ae2:i")) {
                        total += disk.getBytes(null);
                    }
                }
            }
        }
        return total;
    }

    public static long getTotalFluidStorage(IGridNode node) {
        long total = 0;

        for (IGridNode iGridNode : node.getGrid().getNodes()) {
            if (!(iGridNode.getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
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
                }
            }
        }

        return total;
    }

    public static long getTotalChemicalStorage(IGridNode node) {
        long total = 0;

        if (!APAddon.APP_MEKANISTICS.isLoaded())
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

        for (IGridNode iGridNode : node.getGrid().getNodes()) {
            if (!(iGridNode.getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
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
                } else if (APAddon.AE2_THINGS.isLoaded() && stack.getItem() instanceof DISKDrive) {
                    DISKCellInventory diskCellInventory = DISKCellHandler.INSTANCE.getCellInventory(stack, null);
                    if (diskCellInventory != null) {
                        used += diskCellInventory.getNbtItemCount();
                    }
                }
            }
        }

        return used;
    }

    public static long getUsedFluidStorage(IGridNode node) {
        long used = 0;

        for (IGridNode iGridNode : node.getGrid().getNodes()) {
            if (!(iGridNode.getService(IStorageProvider.class) instanceof DriveBlockEntity entity))
                continue;

            InternalInventory inventory = entity.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    if (cell.getKeyType().getClass().isAssignableFrom(AEKeyType.fluids().getClass())) {
                        BasicCellInventory cellInventory = BasicCellHandler.INSTANCE.getCellInventory(stack, null);

                        used += cellInventory.getUsedBytes();
                    }
                }
            }
        }

        return used;
    }

    public static long getUsedChemicalStorage(IGridNode node) {
        long used = 0;

        if (!APAddon.APP_MEKANISTICS.isLoaded())
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

        Iterator<IGridNode> iterator = node.getGrid().getNodes().iterator();

        if (!iterator.hasNext())
            return items;

        while (iterator.hasNext()) {
            IStorageProvider entity = iterator.next().getService(IStorageProvider.class);
            if (!(entity instanceof DriveBlockEntity drive))
                continue;

            InternalInventory inventory = drive.getInternalInventory();

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack.isEmpty())
                    continue;

                if (stack.getItem() instanceof IBasicCellItem cell) {
                    items.add(parseCell(cell, stack));
                } else if (APAddon.AE2_THINGS.isLoaded() && stack.getItem() instanceof DISKDrive disk) {
                    items.add(getObjectFromDisk(disk, stack));
                }
            }
        }

        return items;
    }

    private static Map<String, Object> getObjectFromDisk(DISKDrive drive, ItemStack stack) {
        Map<String, Object> map = new HashMap<>();
        DISKCellInventory cellInventory = DISKCellHandler.INSTANCE.getCellInventory(stack, null);

        if (cellInventory == null) return null;

        map.put("item", LuaConverter.itemToObject(stack.getItem()));
        map.put("type", drive.getKeyType().toString());
        map.put("bytes", drive.getBytes(stack));
        map.put("bytesPerType", 0);
        map.put("usedBytes", cellInventory.getNbtItemCount());
        map.put("totalTypes", 0);
        map.put("fuzzyMode", drive.getFuzzyMode(stack).toString());

        return map;
    }

}
