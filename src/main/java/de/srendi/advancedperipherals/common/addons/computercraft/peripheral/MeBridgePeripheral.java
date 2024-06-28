package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.apis.TableHelper;
import dan200.computercraft.core.computer.ComputerSide;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.CraftJob;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MeFluidHandler;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MeItemHandler;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import de.srendi.advancedperipherals.common.util.inventory.*;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MeBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<MeBridgeEntity>> implements IStorageSystemPeripheral {

    public static final String PERIPHERAL_TYPE = "meBridge";
    private final MeBridgeEntity tile;
    private IGridNode node;

    public MeBridgePeripheral(MeBridgeEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
        this.tile = tileEntity;
        this.node = tileEntity.getActionableNode();
    }

    public void setNode(IManagedGridNode node) {
        this.node = node.getNode();
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableMEBridge.get();
    }

    private ICraftingService getCraftingService() {
        return node.getGrid().getCraftingService();
    }

    /**
     * exports an item out of the system to a valid inventory
     *
     * @param arguments       the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToChest(@NotNull IArguments arguments, @Nullable IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeItemHandler itemHandler = new MeItemHandler(monitor, tile);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetInventory == null)
            return MethodResult.of(0, "Target Inventory does not exist");

        return MethodResult.of(InventoryUtil.moveItem(itemHandler, targetInventory, filter.getLeft()), null);
    }

    /**
     * exports a fluid out of the system to a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToTank(@NotNull IArguments arguments, @Nullable IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeFluidHandler fluidHandler = new MeFluidHandler(monitor, tile);
        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetTank == null)
            return MethodResult.of(0, "Target Tank does not exist");

        return MethodResult.of(InventoryUtil.moveFluid(fluidHandler, targetTank, filter.getLeft()), null);
    }

    /**
     * imports an item to the system from a valid inventory
     *
     * @param arguments       the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToME(@NotNull IArguments arguments, @Nullable IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeItemHandler itemHandler = new MeItemHandler(monitor, tile);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetInventory == null)
            return MethodResult.of(0, "Target Inventory does not exist");

        return MethodResult.of(InventoryUtil.moveItem(targetInventory, itemHandler, filter.getLeft()), null);
    }

    /**
     * imports a fluid to the system from a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToME(@NotNull IArguments arguments, @Nullable IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeFluidHandler fluidHandler = new MeFluidHandler(monitor, tile);
        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetTank == null)
            return MethodResult.of(0, "Target Tank does not exist");

        return MethodResult.of(InventoryUtil.moveFluid(targetTank, fluidHandler, filter.getLeft()), null);
    }

    private MethodResult notConnected() {
        return MethodResult.of(null, "NOT_CONNECTED");
    }

    private boolean isAvailable() {
        return node.getGrid() != null && node.hasGridBooted();
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isConnected() {
        return MethodResult.of(isAvailable());
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult isOnline() {
        return MethodResult.of(node.isOnline());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        MEStorage monitor = AppEngApi.getMonitor(node);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, "EMPTY_FILTER");

        return MethodResult.of(AppEngApi.getObjectFromStack(AppEngApi.findAEStackFromFilter(monitor, getCraftingService(), parsedFilter), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getFluid(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, "EMPTY_FILTER");

        return MethodResult.of(AppEngApi.findAEFluidFromFilter(AppEngApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listItems() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.listStacks(AppEngApi.getMonitor(node), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listFluids() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.listFluids(AppEngApi.getMonitor(node), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listCraftableItems() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.listCraftableStacks(AppEngApi.getMonitor(node), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listCraftableFluids() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.listCraftableStacks(AppEngApi.getMonitor(node), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listCells() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.listCells(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult listDrives() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.listDrives(node.getGrid()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        String side = arguments.getString(1);
        IItemHandler inventory;

        if (Direction.byName(side.toUpperCase(Locale.ROOT)) == null && ComputerSide.valueOfInsensitive(side.toUpperCase(Locale.ROOT)) == null) {
            inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        } else {
            inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        }

        return importToME(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(IComputerAccess computer, @NotNull IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        String side = arguments.getString(1);
        IItemHandler inventory;

        if (Direction.byName(side.toUpperCase(Locale.ROOT)) == null && ComputerSide.valueOfInsensitive(side.toUpperCase(Locale.ROOT)) == null) {
            inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        } else {
            inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        }

        return exportToChest(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getFilteredPatterns(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        // Expected input is a table with either an input table, an output table or both to filter for both
        Map<?, ?> filterTable;
        try {
            Optional<Map<?, ?>> optionalTable = arguments.optTable(0);
            if (optionalTable.isEmpty())
                return MethodResult.of(null, "EMPTY_INPUT");
            filterTable = optionalTable.get();
        } catch (LuaException e) {
            return MethodResult.of(null, "NO_TABLE");
        }

        boolean hasInputFilter = filterTable.containsKey("input");
        boolean hasOutputFilter = filterTable.containsKey("output");
        boolean hasAnyFilter = hasInputFilter || hasOutputFilter;

        // If the player tries to filter for nothing, return nothing.
        if (!hasAnyFilter)
            return MethodResult.of(null, "NO_FILTER");

        GenericFilter inputFilter = null;
        GenericFilter outputFilter = null;

        if (hasInputFilter) {
            Map<?, ?> inputFilterTable = TableHelper.getTableField(filterTable, "input");

            inputFilter = GenericFilter.parseGeneric(inputFilterTable).getLeft();
        }
        if (hasOutputFilter) {
            Map<?, ?> outputFilterTable = TableHelper.getTableField(filterTable, "output");

            outputFilter = GenericFilter.parseGeneric(outputFilterTable).getLeft();
        }


        Pair<IPatternDetails, String> pattern = AppEngApi.findPatternFromFilters(node.getGrid(), getLevel(), inputFilter, outputFilter);

        if (pattern.getRight() != null)
            return MethodResult.of(null, pattern.getRight());

        return MethodResult.of(AppEngApi.getObjectFromPattern(pattern.getLeft()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getPatterns() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.listPatterns(node.getGrid(), getLevel()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getStoredEnergy() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(node.getGrid().getEnergyService().getStoredPower());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyCapacity() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(node.getGrid().getEnergyService().getMaxStoredPower());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyUsage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerUsage());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvgPowerInjection() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerInjection());
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getTotalExternalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getTotalExternalFluidStorage(node));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getTotalExternalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getTotalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getTotalFluidStorage(node));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getTotalChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getTotalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getUsedExternalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getUsedExternalFluidStorage(node));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getUsedExternalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getUsedItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getUsedFluidStorage(node));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getUsedChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getUsedChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getAvailableExternalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getAvailableExternalFluidStorage(node));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getAvailableExternalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getAvailableItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getAvailableFluidStorage(node));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getAvailableChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(AppEngApi.getAvailableChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = AppEngApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null)
            return MethodResult.of(false, "CPU " + cpuName + " does not exists");

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEItemKey> stack = AppEngApi.findAEStackFromFilter(AppEngApi.getMonitor(tile.getGridNode()), craftingGrid, filter.getLeft());
        if (stack.getRight() == null && stack.getLeft() == 0)
            return MethodResult.of(null, "NOT_CRAFTABLE");

        CraftJob job = new CraftJob(owner.getLevel(), computer, node, stack.getRight(), parsedFilter.getCount(), tile, tile, target);
        tile.addJob(job);
        ServerWorker.add(job::startCrafting);
        return MethodResult.of(true);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = AppEngApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null)
            return MethodResult.of(false, "CPU " + cpuName + " does not exists");

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEFluidKey> stack = AppEngApi.findAEFluidFromFilter(AppEngApi.getMonitor(tile.getGridNode()), craftingGrid, filter.getLeft());
        if (stack.getRight() == null && stack.getLeft() == 0)
            return MethodResult.of(null, "NOT_CRAFTABLE");

        CraftJob job = new CraftJob(owner.getLevel(), computer, node, stack.getRight(), parsedFilter.getCount(), tile, tile, target);
        tile.addJob(job);
        ServerWorker.add(job::startCrafting);
        return MethodResult.of(true);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getCraftingTasks() {
        if (!isAvailable())
            return notConnected();

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        List<Object> jobs = new ArrayList<>();

        for (ICraftingCPU cpu : craftingGrid.getCpus()) {
            if (cpu.getJobStatus() != null)
                jobs.add(AppEngApi.getObjectFromJob(cpu.getJobStatus(), cpu));
        }
        return MethodResult.of(jobs);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult cancelCraftingTasks(IArguments arguments) {
        if (!isAvailable())
            return notConnected();

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        Map<?, ?> filterTable;
        try {
            Optional<Map<?, ?>> optionalTable = arguments.optTable(0);
            if (optionalTable.isEmpty())
                return MethodResult.of(null, "EMPTY_INPUT");
            filterTable = optionalTable.get();
        } catch (LuaException e) {
            return MethodResult.of(null, "NO_TABLE");
        }

        Pair<? extends GenericFilter, String> filter = GenericFilter.parseGeneric(filterTable);
        if (filter.getRight() != null)
            return MethodResult.of(null, filter.getRight());

        int jobsCanceled = 0;
        for (ICraftingCPU cpu : craftingGrid.getCpus()) {
            if (cpu.getJobStatus() != null && filter.getLeft().test(cpu.getJobStatus().crafting())) {
                if (cpu instanceof CraftingCPUCluster cpuCluster) {
                    cpuCluster.cancel();
                    jobsCanceled++;
                }
            }
        }
        return MethodResult.of(jobsCanceled, "SUCCESSFUL");
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isItemCraftable(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        AEItemKey item = AEItemKey.of(parsedFilter.toItemStack());

        return MethodResult.of(getCraftingService().isCraftable(item));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isFluidCrafting(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        MEStorage monitor = AppEngApi.getMonitor(node);
        ICraftingService grid = node.getGrid().getService(ICraftingService.class);

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");
        String cpuName = arguments.optString(1, "");
        ICraftingCPU craftingCPU = AppEngApi.getCraftingCPU(node, cpuName);

        return MethodResult.of(AppEngApi.isFluidCrafting(monitor, grid, parsedFilter, craftingCPU));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isFluidCraftable(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        AEFluidKey fluid = AEFluidKey.of(parsedFilter.toFluidStack());

        return MethodResult.of(getCraftingService().isCraftable(fluid));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        String side = arguments.getString(1);
        IFluidHandler fluidHandler;

        if (Direction.byName(side.toUpperCase(Locale.ROOT)) == null && ComputerSide.valueOfInsensitive(side.toUpperCase(Locale.ROOT)) == null) {
            fluidHandler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        } else {
            fluidHandler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        }

        if (fluidHandler == null)
            return MethodResult.of(0, "The target tank does not exist. Make sure the bridge is exposed in the computer network. Reach out to our discord or our documentation for help.");

        return exportToTank(arguments, fluidHandler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        String side = arguments.getString(1);
        IFluidHandler fluidHandler;

        if (Direction.byName(side.toUpperCase(Locale.ROOT)) == null && ComputerSide.valueOfInsensitive(side.toUpperCase(Locale.ROOT)) == null) {
            fluidHandler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        } else {
            fluidHandler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        }

        if (fluidHandler == null)
            return MethodResult.of(0, "The target tank does not exist. Make sure the bridge is exposed in the computer network. Reach out to our discord or our documentation for help.");

        return importToME(arguments, fluidHandler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isItemCrafting(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        MEStorage monitor = AppEngApi.getMonitor(node);
        ICraftingService grid = node.getGrid().getService(ICraftingService.class);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");
        String cpuName = arguments.optString(1, "");
        ICraftingCPU craftingCPU = AppEngApi.getCraftingCPU(node, cpuName);

        return MethodResult.of(AppEngApi.isItemCrafting(monitor, grid, parsedFilter, craftingCPU));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingCPUs() throws LuaException {
        if (!isAvailable())
            return notConnected();

        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        List<Object> map = new ArrayList<>();

        for (ICraftingCPU iCraftingCPU : grid.getCpus()) {
            Object cpu = AppEngApi.getObjectFromCPU(iCraftingCPU, false);
            map.add(cpu);
        }
        return MethodResult.of(map);
    }
}
