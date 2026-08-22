package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.IChestOrDrive;
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
import appeng.api.storage.ISubMenuHost;
import appeng.api.storage.MEStorage;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.blockentity.storage.ChestBlockEntity;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.crafting.execution.CraftingCpuLogic;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.helpers.IPriorityHost;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.me.cells.BasicCellHandler;
import appeng.me.cells.BasicCellInventory;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.parts.storagebus.StorageBusPart;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskCellItem;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskCellStorage;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskHandler;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskKeys;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import io.github.projectet.ae2things.storage.DISKCellHandler;
import io.github.projectet.ae2things.storage.DISKCellInventory;
import io.github.projectet.ae2things.storage.IDISKCellItem;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AEApi {
    @NotNull
    public static Pair<Long, AEItemKey> findAEStackFromStack(MEStorage monitor, @Nullable ICraftingService crafting, ItemStack item) {
        return findAEStackFromFilter(monitor, crafting, ItemFilter.fromStack(item));
    }

    @NotNull
    public static Pair<Long, AEItemKey> findAEStackFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, ItemFilter filter) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEItemKey key && filter.test(key.toStack()))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null)
            return Pair.of(0L, null);

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof AEItemKey key && filter.test(key.toStack()))
                return Pair.of(0L, key);
        }

        return Pair.of(0L, null);
    }

    @NotNull
    public static List<Pair<Long, AEItemKey>> findAEStacksFromFilter(MEStorage monitor, ItemFilter filter) {
        List<Pair<Long, AEItemKey>> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEItemKey key && filter.test(key.toStack())) {
                items.add(Pair.of(temp.getLongValue(), key));
            }
        }
        return items;
    }

    @NotNull
    public static Pair<Long, AEFluidKey> findAEFluidFromStack(MEStorage monitor, @Nullable ICraftingService crafting, FluidStack item) {
        return findAEFluidFromFilter(monitor, crafting, FluidFilter.fromStack(item));
    }

    @NotNull
    public static Pair<Long, AEFluidKey> findAEFluidFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, FluidFilter filter) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEFluidKey key && filter.test(key.toStack(1)))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null)
            return Pair.of(0L, null);

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof AEFluidKey key && filter.test(key.toStack(1)))
                return Pair.of(0L, key);
        }

        return Pair.of(0L, null);
    }

    @NotNull
    public static List<Pair<Long, AEFluidKey>> findAEFluidsFromFilter(MEStorage monitor, FluidFilter filter) {
        List<Pair<Long, AEFluidKey>> fluids = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof AEFluidKey key && filter.test(key.toStack(1))) {
                fluids.add(Pair.of(temp.getLongValue(), key));
            }
        }
        return fluids;
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
     * The error message is "NOT_FOUND" if no pattern is found. See {@link StatusConstants#NOT_FOUND}
     */
    @NotNull
    public static Pair<Pair<EncodedPatternItem, IPatternDetails>, String> findPatternFromFilters(IGrid grid, Level level, @Nullable GenericFilter<?> inputFilter, @Nullable GenericFilter<?> outputFilter) {
        for (Pair<EncodedPatternItem, IPatternDetails> pattern : getPatterns(grid, level)) {
            IPatternDetails patternDetails = pattern.right();
            if (patternDetails.getInputs().length == 0)
                continue;
            if (patternDetails.getOutputs().length == 0)
                continue;

            boolean inputMatch = false;
            boolean outputMatch = false;

            if (inputFilter != null) {
                outerLoop:
                for (IPatternDetails.IInput input : patternDetails.getInputs()) {
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
                for (GenericStack output : patternDetails.getOutputs()) {
                    if (outputFilter.testAE(output)) {
                        outputMatch = true;
                        break;
                    }
                }
            } else {
                outputMatch = true;
            }

            if (inputMatch && outputMatch)
                return Pair.of(Pair.of(pattern.left(), patternDetails), null);
        }

        return Pair.of(null, StatusConstants.NOT_FOUND.toString());
    }

    public static List<Object> listItems(MEStorage monitor, ICraftingService service, ItemFilter filter) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        for (Object2LongMap.Entry<AEKey> aeKey : keyCounter) {
            if (aeKey.getKey() instanceof AEItemKey itemKey && filter.test(itemKey.getReadOnlyStack())) {
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

    public static List<Pair<EncodedPatternItem, IPatternDetails>> getPatterns(IGrid grid, Level level) {
        List<Pair<EncodedPatternItem, IPatternDetails>> patterns = new ArrayList<>();

        for (var machineClass : grid.getMachineClasses()) {
            var containerClass = tryCastMachineToContainer(machineClass);
            if (containerClass == null)
                continue;

            for (var container : grid.getActiveMachines(containerClass)) {
                for (ItemStack patternItem : container.getTerminalPatternInventory()) {
                    if (patternItem.getItem() instanceof EncodedPatternItem item) {
                        IPatternDetails patternDetails = item.decode(patternItem, level, true);
                        if (patternDetails == null)
                            continue;

                        patterns.add(Pair.of(item, patternDetails));
                    }
                }
            }
        }
        return patterns;
    }

    public static List<Object> listPatterns(IGrid grid, Level level) {
        return getPatterns(grid, level).stream().map(AEApi::parsePattern).collect(Collectors.toList());
    }

    public static List<Object> listDrives(IGrid grid) {
        List<Object> drives = new ArrayList<>();
        streamCellDrive(grid).forEach((node) -> {
            IStorageProvider storage = node.getService(IStorageProvider.class);
            if (storage instanceof IChestOrDrive drive) {
                drives.add(parseDrive(drive));
            }
        });
        return drives;
    }

    private static Stream<@NotNull IGridNode> streamCellDrive(IGrid grid) {
        return StreamSupport.stream(grid.getMachineClasses().spliterator(), false)
            .filter(IChestOrDrive.class::isAssignableFrom)
            .flatMap((clazz) -> StreamSupport.stream(grid.getMachineNodes(clazz).spliterator(), false));
    }

    private static Stream<@NotNull Pair<Item, ItemStack>> streamCell(IGrid grid) {
        return streamCellDrive(grid)
            .map((node) -> node.getService(IStorageProvider.class) instanceof IChestOrDrive drive ? drive : null)
            .filter(Objects::nonNull)
            .flatMap((drive) -> {
                IntFunction<ItemStack> cellInv = getDriveCellStorageOrEmpty(drive);
                return IntStream.range(0, drive.getCellCount())
                    .mapToObj((slot) -> {
                        Item cell = drive.getCellItem(slot);
                        return cell == null ? null : Pair.of(cell, cellInv.apply(slot));
                    })
                    .filter(Objects::nonNull);
            });
    }

    private static Stream<@NotNull ICellWrapper> streamWrappedCell(IGrid grid) {
        return streamCell(grid)
            .map((pair) -> ICellWrapper.of(pair.left(), pair.right()))
            .filter(Objects::nonNull);
    }

    private static Stream<@NotNull StorageBusPart> streamExternalStorage(IGrid grid) {
        return StreamSupport.stream(grid.getMachineNodes(StorageBusPart.class).spliterator(), false)
            .map((node) -> Objects.requireNonNull((StorageBusPart) node.getService(IStorageProvider.class)));
    }

    private static Stream<@NotNull IItemHandler> streamExternalItemStorage(IGrid grid) {
        return streamExternalStorage(grid)
            .map((bus) -> ItemUtil.extractHandler(null, bus.getLevel(), bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide()), bus.getSide().getOpposite()))
            .filter(Objects::nonNull);
    }

    private static Stream<@NotNull IFluidHandler> streamExternalFluidStorage(IGrid grid) {
        return streamExternalStorage(grid)
            .map((bus) -> FluidUtil.extractHandler(null, bus.getLevel(), bus.getHost().getBlockEntity().getBlockPos().relative(bus.getSide()), bus.getSide().getOpposite()))
            .filter(Objects::nonNull);
    }

    private static Class<? extends PatternContainer> tryCastMachineToContainer(Class<?> machineClass) {
        if (PatternContainer.class.isAssignableFrom(machineClass))
            return machineClass.asSubclass(PatternContainer.class);

        return null;
    }

    public static <T extends AEKey> Map<String, Object> parseAeStack(Pair<Long, T> stack, @Nullable ICraftingService service) {
        if (stack.right() == null)
            return null;
        if (stack.right() instanceof AEItemKey itemKey)
            return parseItemStack(Pair.of(stack.left(), itemKey), service);
        if (stack.right() instanceof AEFluidKey fluidKey)
            return parseFluidStack(Pair.of(stack.left(), fluidKey), service);

        AdvancedPeripherals.debug(org.apache.logging.log4j.Level.WARN, "Could not create table from unknown stack {} - Report this to the maintainer of ap", stack.right().getClass());
        return null;
    }

    public static Map<String, Object> parseGenericStack(GenericStack stack) {
        if (stack.what() == null)
            return null;
        if (stack.what() instanceof AEItemKey aeItemKey)
            return parseItemStack(Pair.of(stack.amount(), aeItemKey), null);
        if (stack.what() instanceof AEFluidKey aeFluidKey)
            return parseFluidStack(Pair.of(stack.amount(), aeFluidKey), null);

        AdvancedPeripherals.debug(org.apache.logging.log4j.Level.WARN, "Could not create table from unknown stack {} - Report this to the maintainer of ap", stack.getClass());
        return null;
    }

    public static List<Object> parseKeyCounter(KeyCounter counter) {
        List<Object> parsedKeys = new ArrayList<>();
        for (AEKey key : counter.keySet()) {
            parsedKeys.add(parseGenericStack(new GenericStack(key, counter.get(key))));
        }

        return parsedKeys;
    }

    public static Map<Object, Object> parseDrive(IChestOrDrive drive) {
        long totalBytes = 0;
        long usedBytes = 0;

        Map<Integer, Object> driveCells = new HashMap<>();

        int cellCount = drive.getCellCount();
        IntFunction<ItemStack> cellInv = getDriveCellStorageOrEmpty(drive);
        for (int slot = 0; slot < cellCount; slot++) {
            Item cell = drive.getCellItem(slot);
            if (cell == null) {
                continue;
            }
            ItemStack cellStack = cellInv.apply(slot);
            driveCells.put(slot + 1, cellToLua(cell, cellStack));
        }

        Map<Object, Object> properties = new HashMap<>();

        properties.put("usedBytes", usedBytes);
        properties.put("totalBytes", totalBytes);
        properties.put("cells", driveCells);
        if (drive instanceof Nameable nameable) {
            properties.put("name", nameable.hasCustomName() ? nameable.getCustomName().getString() : nameable.getDisplayName().getString());
        }
        if (drive instanceof BlockEntity be) {
            properties.put("type", ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(be.getType()));
            properties.put("position", LuaConverter.posToLua(be.getBlockPos()));
        }
        if (drive instanceof IPriorityHost priHost) {
            properties.put("priority", priHost.getPriority());
        }
        if (drive instanceof ISubMenuHost menuHost) {
            properties.put("menuIcon", LuaConverter.itemToLua(menuHost.getMainMenuIcon().getItem()));
        }
        return properties;
    }

    @Nullable
    private static IntFunction<ItemStack> getDriveCellStorage(IChestOrDrive drive) {
        if (drive instanceof DriveBlockEntity be) {
            InternalInventory inv = be.getInternalInventory();
            return inv::getStackInSlot;
        }
        if (drive instanceof ChestBlockEntity be) {
            return (slot) -> slot == 0 ? be.getCell() : ItemStack.EMPTY;
        }
        return null;
    }

    @NotNull
    private static IntFunction<ItemStack> getDriveCellStorageOrEmpty(IChestOrDrive drive) {
        IntFunction<ItemStack> inv = getDriveCellStorage(drive);
        return inv != null ? inv : (slot) -> ItemStack.EMPTY;
    }

    public static Map<String, Object> cellToLua(Item cell, ItemStack stack) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("item", LuaConverter.itemToLua(cell));
        if (cell instanceof ICellWorkbenchItem workbenchCell) {
            properties.put("fuzzyMode", workbenchCell.getFuzzyMode(stack).toString());
        }
        ICellWrapper wrapper = ICellWrapper.of(cell, stack);
        if (wrapper != null) {
            properties.put("type", wrapper.keyType().getId().toString());
            properties.put("bytesPerType", wrapper.bytesPerType());
            properties.put("maxBytes", wrapper.maxBytes());
            properties.put("maxTypes", wrapper.maxTypes());
            properties.put("usedBytes", wrapper.usedBytes());
        }
        return properties;
    }

    private static Map<String, Object> parseItemStack(Pair<Long, AEItemKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> properties = LuaConverter.itemStackToLua(stack.right().getReadOnlyStack(), stack.left());
        properties.put("isCraftable", craftingService != null && craftingService.isCraftable(stack.right()));
        return properties;
    }

    private static Map<String, Object> parseFluidStack(Pair<Long, AEFluidKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> properties = LuaConverter.fluidStackToLua(stack.right().toStack(1), stack.left());
        properties.put("isCraftable", craftingService != null && craftingService.isCraftable(stack.right()));
        return properties;
    }

    public static Map<String, Object> parsePattern(Pair<EncodedPatternItem, IPatternDetails> pattern) {
        Map<String, Object> properties = new HashMap<>();
        IPatternDetails patternDetails = pattern.right();
        String patternType = getPatternType(pattern.left());

        properties.put("inputs", Arrays.stream(patternDetails.getInputs()).map(AEApi::parsePatternInput).collect(Collectors.toList()));
        properties.put("outputs", Arrays.stream(patternDetails.getOutputs()).map(AEApi::parseGenericStack).collect(Collectors.toList()));
        properties.put("primaryOutput", parseGenericStack(patternDetails.getPrimaryOutput()));
        properties.put("patternType", patternType);
        return properties;
    }

    private static String getPatternType(EncodedPatternItem patternItem) {
        if (patternItem.equals(AEItems.CRAFTING_PATTERN.asItem())) return "crafting";
        if (patternItem.equals(AEItems.PROCESSING_PATTERN.asItem())) return "processing";
        if (patternItem.equals(AEItems.SMITHING_TABLE_PATTERN.asItem())) return "smithing";
        if (patternItem.equals(AEItems.STONECUTTING_PATTERN.asItem())) return "stonecutting";
        return "unknown";
    }

    public static Map<String, Object> parsePatternInput(IPatternDetails.IInput patternInput) {
        Map<String, Object> properties = new HashMap<>();
        GenericStack primaryInput = patternInput.getPossibleInputs()[0];
        properties.put("primaryInput", parseGenericStack(primaryInput));
        properties.put("possibleInputs", Arrays.stream(Arrays.copyOfRange(patternInput.getPossibleInputs(), 1, patternInput.getPossibleInputs().length)).map(AEApi::parseGenericStack));
        properties.put("multiplier", patternInput.getMultiplier());

        AEKey remainingKey = patternInput.getRemainingKey(patternInput.getPossibleInputs()[0].what());
        Map<String, Object> remainingKeyProperties = remainingKey == null ? null : parseGenericStack(new GenericStack(remainingKey, 1));
        properties.put("remaining", remainingKeyProperties);

        return properties;
    }

    public static Map<String, Object> parseCraftingCPU(ICraftingCPU cpu, boolean recursive) {
        Map<String, Object> properties = new HashMap<>();
        long storage = cpu.getAvailableStorage();
        int coProcessors = cpu.getCoProcessors();
        boolean isBusy = cpu.isBusy();
        properties.put("storage", storage);
        properties.put("coProcessors", coProcessors);
        properties.put("isBusy", isBusy);
        if (!recursive)
            properties.put("craftingJob", cpu.getJobStatus() != null ? parseCraftingJob(cpu.getJobStatus(), null, null) : null);
        properties.put("name", cpu.getName() != null ? cpu.getName().getString() : "Unnamed");
        properties.put("selectionMode", cpu.getSelectionMode().toString());

        return properties;
    }

    public static Object parseCraftingJob(CraftingJobStatus status, @Nullable AECraftJob craftJob, @Nullable ICraftingCPU cpu) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("bridgeId", craftJob == null ? -1 : craftJob.getId());
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
                if (jobStatus == null)
                    return false;

                return filter.testAE(jobStatus.crafting());
            }
        }

        return false;
    }

    /// External Storage
    /// Total

    public static double getMaxExternalItemStorage(IGrid grid) {
        return streamExternalItemStorage(grid)
            .mapToDouble((handler) -> {
                double total = 0;
                for (int i = 0; i < handler.getSlots(); i++) {
                    total += handler.getSlotLimit(i) / 64.0;
                }
                return total;
            })
            .sum();
    }

    public static long getMaxExternalItemCount(IGrid grid) {
        return streamExternalItemStorage(grid)
            .mapToLong((handler) -> {
                long total = 0;
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    total += stack.isEmpty() ? handler.getSlotLimit(i) : stack.getMaxStackSize();
                }
                return total;
            })
            .sum();
    }

    public static long getMaxExternalFluidStorage(IGrid grid) {
        return streamExternalFluidStorage(grid)
            .mapToLong((handler) -> {
                long total = 0;
                for (int i = 0; i < handler.getTanks(); i++) {
                    total += handler.getTankCapacity(i);
                }
                return total;
            })
            .sum();
    }

    /// Used

    public static double getUsedExternalItemStorage(IGrid grid) {
        return streamExternalItemStorage(grid)
            .mapToDouble((handler) -> {
                double total = 0;
                for (int i = 0; i < handler.getSlots(); i++) {
                    int limit = handler.getSlotLimit(i);
                    ItemStack stack = handler.getStackInSlot(i);
                    total += ((double) stack.getCount()) / Math.min(limit, stack.getMaxStackSize());
                }
                return total;
            })
            .sum();
    }

    public static long getUsedExternalItemCount(IGrid grid) {
        return streamExternalItemStorage(grid)
            .mapToLong((handler) -> {
                long total = 0;
                for (int i = 0; i < handler.getSlots(); i++) {
                    total += handler.getStackInSlot(i).getCount();
                }
                return total;
            })
            .sum();
    }

    public static long getUsedExternalFluidStorage(IGrid grid) {
        return streamExternalFluidStorage(grid)
            .mapToLong((handler) -> {
                long total = 0;
                for (int i = 0; i < handler.getTanks(); i++) {
                    total += handler.getFluidInTank(i).getAmount();
                }
                return total;
            })
            .sum();
    }

    /**
     * Calculates the available external item storage of a given grid node.
     *
     * @param node The grid node for which to calculate the available external item storage.
     * @return The available external item storage.
     */
    public static double getAvailableExternalItemStorage(IGrid grid) {
        return getMaxExternalItemStorage(grid) - getUsedExternalItemStorage(grid);
    }

    public static double getAvailableExternalItemCount(IGrid grid) {
        return getMaxExternalItemCount(grid) - getUsedExternalItemCount(grid);
    }

    /**
     * Calculates the available external fluid storage on a given grid node by subtracting the used external fluid storage
     * from the total external fluid storage.
     *
     * @param node The grid node on which to calculate the available external fluid storage.
     * @return The available external fluid storage on the grid node.
     */
    public static long getAvailableExternalFluidStorage(IGrid grid) {
        return getMaxExternalFluidStorage(grid) - getUsedExternalFluidStorage(grid);
    }

    /// Internal Storage
    /// Total

    public static long getMaxItemStorage(IGrid grid) {
        return streamWrappedCell(grid)
            .filter((cell) -> cell.keyType() == AEKeyType.items())
            .mapToLong(ICellWrapper::maxBytes)
            .sum();
    }

    public static long getMaxFluidStorage(IGrid grid) {
        return streamWrappedCell(grid)
            .filter((cell) -> cell.keyType() == AEKeyType.fluids())
            .mapToLong(ICellWrapper::maxBytes)
            .sum();
    }

    /// Used
    public static long getUsedItemStorage(IGrid grid) {
        return streamWrappedCell(grid)
            .filter((cell) -> cell.keyType() == AEKeyType.items())
            .mapToLong(ICellWrapper::usedBytes)
            .sum();
    }

    public static long getUsedFluidStorage(IGrid grid) {
        return streamWrappedCell(grid)
            .filter((cell) -> cell.keyType() == AEKeyType.fluids())
            .mapToLong(ICellWrapper::usedBytes)
            .sum();
    }

    /// Available Storage

    /**
     * Calculates the available item storage on a given grid.
     * It subtracts the used item storage from the total item storage.
     *
     * @param grid The grid to calculate the available item storage for.
     * @return The available item storage in bytes.
     */
    public static long getAvailableItemStorage(IGrid grid) {
        return streamWrappedCell(grid)
            .filter((cell) -> cell.keyType() == AEKeyType.items())
            .mapToLong(ICellWrapper::freeBytes)
            .sum();
    }

    /**
     * Calculates the available fluid storage in a given grid.
     *
     * @param grid The grid to calculate the available fluid storage for.
     * @return The available fluid storage in bytes.
     */
    public static long getAvailableFluidStorage(IGrid grid) {
        return streamWrappedCell(grid)
            .filter((cell) -> cell.keyType() == AEKeyType.fluids())
            .mapToLong(ICellWrapper::freeBytes)
            .sum();
    }

    public static ICraftingCPU getCraftingCPU(IGridNode node, String cpuName) {
        if (cpuName.isEmpty())
            return null;
        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        if (grid == null)
            return null;

        Iterator<ICraftingCPU> iterator = grid.getCpus().iterator();
        if (!iterator.hasNext())
            return null;

        while (iterator.hasNext()) {
            ICraftingCPU cpu = iterator.next();

            if (cpu.getName() != null && cpu.getName().getString().equals(cpuName)) {
                return cpu;
            }
        }

        return null;
    }

    public static List<Map<String, Object>> listCells(IGrid grid) {
        return streamCell(grid).map((pair) -> cellToLua(pair.left(), pair.right())).toList();
    }

    private interface ICellWrapper {
        Item item();
        AEKeyType keyType();
        long maxBytes();
        long usedBytes();
        long bytesPerType();
        long maxTypes();

        default long freeBytes() {
            return this.maxBytes() - this.usedBytes();
        }

        static ICellWrapper of(Item item, ItemStack stack) {
            if (item instanceof IBasicCellItem cell) {
                return new BasicCellWrapper(cell, stack);
            }
            if (item instanceof IDISKCellItem cell) {
                return new AE2ThingsDiskCellWrapper(cell, stack);
            }
            if (item instanceof AEDiskCellItem cell) {
                return new APDiskCellWrapper(cell, stack);
            }
            return null;
        }
    }

    private static final class BasicCellWrapper implements ICellWrapper {
        final IBasicCellItem cell;
        final ItemStack stack;
        final BasicCellInventory inv;

        BasicCellWrapper(IBasicCellItem cell, ItemStack stack) {
            this.cell = cell;
            this.stack = stack;
            this.inv = BasicCellHandler.INSTANCE.getCellInventory(stack, null);
        }

        @Override
        public Item item() {
            return (Item) this.cell;
        }

        @Override
        public AEKeyType keyType() {
            return this.cell.getKeyType();
        }

        @Override
        public long maxBytes() {
            return this.cell.getBytes(this.stack);
        }

        @Override
        public long usedBytes() {
            return this.inv == null ? 0 : this.inv.getUsedBytes();
        }

        @Override
        public long bytesPerType() {
            return this.cell.getBytesPerType(this.stack);
        }

        @Override
        public long maxTypes() {
            return this.cell.getTotalTypes(this.stack);
        }
    }

    private static final class AE2ThingsDiskCellWrapper implements ICellWrapper {
        final IDISKCellItem cell;
        final ItemStack stack;
        final DISKCellInventory inv;

        AE2ThingsDiskCellWrapper(IDISKCellItem cell, ItemStack stack) {
            this.cell = cell;
            this.stack = stack;
            this.inv = DISKCellHandler.INSTANCE.getCellInventory(stack, null);
        }

        @Override
        public Item item() {
            return (Item) this.cell;
        }

        @Override
        public AEKeyType keyType() {
            return this.cell.getKeyType();
        }

        @Override
        public long maxBytes() {
            return this.cell.getBytes(this.stack);
        }

        @Override
        public long usedBytes() {
            return this.inv == null ? 0 : this.inv.getStoredItemCount();
        }

        @Override
        public long bytesPerType() {
            return 0;
        }

        @Override
        public long maxTypes() {
            return Long.MAX_VALUE;
        }
    }

    private static final class APDiskCellWrapper implements ICellWrapper {
        final AEDiskCellItem cell;
        final ItemStack stack;
        final AEDiskCellStorage inv;

        APDiskCellWrapper(AEDiskCellItem cell, ItemStack stack) {
            this.cell = cell;
            this.stack = stack;
            this.inv = AEDiskHandler.INSTANCE.getCellInventory(stack, null);
        }

        @Override
        public Item item() {
            return this.cell;
        }

        @Override
        public AEKeyType keyType() {
            return AEDiskKeys.INSTANCE;
        }

        @Override
        public long maxBytes() {
            return this.cell.getMaxBytes();
        }

        @Override
        public long usedBytes() {
            return this.inv == null ? 0 : this.inv.getUsedBytes();
        }

        @Override
        public long bytesPerType() {
            return 0;
        }

        @Override
        public long maxTypes() {
            return 1;
        }
    }
}
