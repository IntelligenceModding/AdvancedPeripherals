package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.crafting.pattern.EncodedPatternItem;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AEApi;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AECraftJob;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MEChemicalHandler;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MEFluidHandler;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MEItemHandler;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.MEBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.IChemicalHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MEBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<MEBridgeEntity>> implements IStorageSystemPeripheral {

    public static final String PERIPHERAL_TYPE = "me_bridge";

    private final MEBridgeEntity bridge;
    private IGridNode node;

    public MEBridgePeripheral(MEBridgeEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
        this.bridge = tileEntity;
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
    protected MethodResult exportToChest(@NotNull IArguments arguments, IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEItemHandler itemHandler = new MEItemHandler(monitor, bridge);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(itemHandler, targetInventory, filter.getLeft()), null);
    }

    /**
     * exports a fluid out of the system to a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToTank(@NotNull IArguments arguments, IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEFluidHandler fluidHandler = new MEFluidHandler(monitor, bridge);
        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(FluidUtil.moveFluid(fluidHandler, targetTank, filter.getLeft()), null);
    }

    /**
     * exports a fluid out of the system to a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToTank(@NotNull IArguments arguments, IChemicalHandler targetTank) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEChemicalHandler chemicalHandler = new MEChemicalHandler(monitor, bridge);
        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(ChemicalUtil.moveChemical(chemicalHandler, targetTank, filter.getLeft()));
    }

    /**
     * imports an item to the system from a valid inventory
     *
     * @param arguments       the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToME(@NotNull IArguments arguments, IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEItemHandler itemHandler = new MEItemHandler(monitor, bridge);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(targetInventory, itemHandler, filter.getLeft()), null);
    }

    /**
     * imports a fluid to the system from a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToME(@NotNull IArguments arguments, IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEFluidHandler fluidHandler = new MEFluidHandler(monitor, bridge);
        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(FluidUtil.moveFluid(targetTank, fluidHandler, filter.getLeft()), null);
    }

    /**
     * imports a fluid to the system from a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToME(@NotNull IArguments arguments, IChemicalHandler targetTank) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEChemicalHandler chemicalHandler = new MEChemicalHandler(monitor, bridge);
        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(ChemicalUtil.moveChemical(targetTank, chemicalHandler, filter.getLeft()));
    }

    private MethodResult notConnected(@Nullable Object defaultValue) {
        return MethodResult.of(defaultValue, StatusConstants.NOT_CONNECTED.toString());
    }

    private boolean isAvailable() {
        return node.hasGridBooted();
    }

    @Override
    @LuaFunction(mainThread = true)
    public final boolean isConnected() {
        return isAvailable();
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
            return notConnected(null);

        MEStorage monitor = AEApi.getMonitor(node);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        return MethodResult.of(AEApi.parseAeStack(AEApi.findAEStackFromFilter(monitor, getCraftingService(), parsedFilter), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getFluid(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        return MethodResult.of(AEApi.parseAeStack(AEApi.findAEFluidFromFilter(AEApi.getMonitor(node), getCraftingService(), parsedFilter), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getChemical(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        return MethodResult.of(AEApi.parseAeStack(AEApi.findAEChemicalFromFilter(AEApi.getMonitor(node), getCraftingService(), parsedFilter), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getItems(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.optTable(0, Collections.emptyMap()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listItems(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getFluids(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.optTable(0, Collections.emptyMap()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listFluids(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getChemicals(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.optTable(0, Collections.emptyMap()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listChemicals(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCraftableItems(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.optTable(0, Collections.emptyMap()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listCraftableItems(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCraftableFluids(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.optTable(0, Collections.emptyMap()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listCraftableFluids(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    public MethodResult getCraftableChemicals(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.optTable(0, Collections.emptyMap()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listCraftableChemicals(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCells() {
        if (!isAvailable())
            return notConnected(null);

        return MethodResult.of(AEApi.listCells(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getDrives() {
        if (!isAvailable())
            return notConnected(null);

        return MethodResult.of(AEApi.listDrives(node.getGrid()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(side, owner);

        if (inventory == null) {
            inventory = InventoryUtil.getHandlerFromName(computer, side);
        }

        if (inventory == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return importToME(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(IComputerAccess computer, @NotNull IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(side, owner);

        if (inventory == null) {
            inventory = InventoryUtil.getHandlerFromName(computer, side);
        }

        if (inventory == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return exportToChest(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IFluidHandler fluidHandler = FluidUtil.getHandlerFromDirection(side, owner);

        if (fluidHandler == null) {
            fluidHandler = FluidUtil.getHandlerFromName(computer, side);
        }

        if (fluidHandler == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return importToME(arguments, fluidHandler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IFluidHandler fluidHandler = FluidUtil.getHandlerFromDirection(side, owner);

        if (fluidHandler == null) {
            fluidHandler = FluidUtil.getHandlerFromName(computer, side);
        }

        if (fluidHandler == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return exportToTank(arguments, fluidHandler);
    }


    @Override
    @LuaFunction(mainThread = true)
    public MethodResult importChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(0);

        String side = arguments.getString(1);
        IChemicalHandler chemicalHandler = ChemicalUtil.getHandlerFromDirection(side, owner);

        if (chemicalHandler == null) {
            chemicalHandler = ChemicalUtil.getHandlerFromName(computer, side);
        }

        if (chemicalHandler == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return importToME(arguments, chemicalHandler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult exportChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        if (APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(0);

        String side = arguments.getString(1);
        IChemicalHandler chemicalHandler = ChemicalUtil.getHandlerFromDirection(side, owner);

        if (chemicalHandler == null) {
            chemicalHandler = ChemicalUtil.getHandlerFromName(computer, side);
        }

        if (chemicalHandler == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return exportToTank(arguments, chemicalHandler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getPatterns(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        // Expected input is a table with either an input table, an output table or both to filter for both
        // If no table is provided or it's empty, return every pattern
        Map<?, ?> filterTable = arguments.optTable(0, Collections.emptyMap());
        if (filterTable.isEmpty()) {
            return MethodResult.of(AEApi.listPatterns(node.getGrid(), getLevel()));
        }

        boolean hasInputFilter = filterTable.containsKey("input");
        boolean hasOutputFilter = filterTable.containsKey("output");
        boolean hasAnyFilter = hasInputFilter || hasOutputFilter;

        // If the player tries to filter for nothing, return nothing.
        if (!hasAnyFilter)
            return MethodResult.of(null, "NO_FILTER");

        GenericFilter<?> inputFilter = null;
        GenericFilter<?> outputFilter = null;

        if (hasInputFilter) {
            Map<?, ?> inputFilterTable = TableHelper.getTableField(filterTable, "input");

            inputFilter = GenericFilter.parseGeneric(inputFilterTable).getLeft();
        }
        if (hasOutputFilter) {
            Map<?, ?> outputFilterTable = TableHelper.getTableField(filterTable, "output");

            outputFilter = GenericFilter.parseGeneric(outputFilterTable).getLeft();
        }

        Pair<Pair<EncodedPatternItem<?>, IPatternDetails>, String> pattern = AEApi.findPatternFromFilters(node.getGrid(), getLevel(), inputFilter, outputFilter);

        if (pattern.getRight() != null)
            return MethodResult.of(null, pattern.getRight());

        return MethodResult.of(AEApi.parsePattern(pattern.getLeft()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getStoredEnergy() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(node.getGrid().getEnergyService().getStoredPower());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyCapacity() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(node.getGrid().getEnergyService().getMaxStoredPower());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyUsage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerUsage());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAverageEnergyInput() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerInjection());
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalExternalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalExternalFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalExternalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedExternalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedExternalFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedExternalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableExternalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableExternalFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableExternalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableChemicalStorage(node));
    }

    @Override
    @LuaFunction
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        String cpuName = arguments.optString(1, "");

        ICraftingCPU target = AEApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null) {
            return MethodResult.of(null, StatusConstants.CPU_NOT_FOUND.withInfo(cpuName));
        }

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEItemKey> stack = AEApi.findAEStackFromFilter(AEApi.getMonitor(bridge.getGridNode()), craftingGrid, parsedFilter);
        if (stack.getRight() == null && stack.getLeft() == 0) {
            return MethodResult.of(null, StatusConstants.NOT_CRAFTABLE.toString());
        }

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, node, stack.getRight(), parsedFilter.getCount(), bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job.withCPU(target));
    }

    @Override
    @LuaFunction
    public final MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = AEApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null)
            return MethodResult.of(null, StatusConstants.CPU_NOT_FOUND.withInfo(cpuName));

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEFluidKey> stack = AEApi.findAEFluidFromFilter(AEApi.getMonitor(bridge.getGridNode()), craftingGrid, parsedFilter);
        if (stack.getRight() == null && stack.getLeft() == 0)
            return MethodResult.of(false, StatusConstants.NOT_CRAFTABLE.toString());

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, node, stack.getRight(), parsedFilter.getCount(), bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job.withCPU(target));
    }

    @Override
    public MethodResult craftChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = AEApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null)
            return MethodResult.of(null, StatusConstants.CPU_NOT_FOUND.withInfo(cpuName));

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, MekanismKey> stack = AEApi.findAEChemicalFromFilter(AEApi.getMonitor(bridge.getGridNode()), craftingGrid, parsedFilter);
        if (stack.getRight() == null && stack.getLeft() == 0)
            return MethodResult.of(false, StatusConstants.NOT_CRAFTABLE.toString());

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, node, stack.getRight(), parsedFilter.getCount(), bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job.withCPU(target));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getCraftingTasks() {
        if (!isAvailable())
            return notConnected(null);

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        List<Object> jobs = new ArrayList<>();

        for (AECraftJob job : bridge.getJobs()) {
            for (ICraftingCPU cpu : craftingGrid.getCpus()) {
                if (cpu.isBusy() && job.getToCraft().matches(cpu.getJobStatus().crafting()))
                    jobs.add(AEApi.parseCraftingJob(cpu.getJobStatus(), job, cpu));
            }
        }
        return MethodResult.of(jobs);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getCraftingTask(int id) {
        if (!isAvailable())
            return notConnected(null);

        AECraftJob foundJob = null;

        for (AECraftJob job : bridge.getJobs()) {
            if (job.getId() == id) {
                foundJob = job;
                break;
            }
        }
        return MethodResult.of(foundJob);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult cancelCraftingTasks(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(arguments.getTable(0));
        if (filter.getRight() != null)
            return MethodResult.of(0, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();

        int jobsCanceled = 0;
        for (ICraftingCPU cpu : craftingGrid.getCpus()) {
            if (cpu.getJobStatus() != null && parsedFilter.testAE(cpu.getJobStatus().crafting())) {
                cpu.cancelJob();
                jobsCanceled++;
            }
        }
        return MethodResult.of(jobsCanceled);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isCraftable(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(false);

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(arguments.getTable(0));
        if (filter.getRight() != null)
            return MethodResult.of(false, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, StatusConstants.EMPTY_FILTER.toString());

        return MethodResult.of(AEApi.findPatternFromFilters(node.getGrid(), getLevel(), null, parsedFilter).getLeft() != null);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isCrafting(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(false);

        ICraftingService grid = node.getGrid().getService(ICraftingService.class);

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(arguments.getTable(0));
        if (filter.getRight() != null)
            return MethodResult.of(false, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, StatusConstants.EMPTY_FILTER.toString());

        String cpuName = arguments.optString(1, "");
        ICraftingCPU craftingCPU = AEApi.getCraftingCPU(node, cpuName);

        return MethodResult.of(AEApi.isCrafting(grid, parsedFilter, craftingCPU));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingCPUs() {
        if (!isAvailable())
            return notConnected(null);

        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        List<Object> map = new ArrayList<>();

        for (ICraftingCPU iCraftingCPU : grid.getCpus()) {
            Object cpu = AEApi.parseCraftingCPU(iCraftingCPU, false);
            map.add(cpu);
        }
        return MethodResult.of(map);
    }
}
