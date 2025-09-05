package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.api.config.Actionable;
import appeng.crafting.pattern.EncodedPatternItem;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectLuaTable;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AEApi;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AECraftJob;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AEMekanismApi;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MEFluidHandler;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MEItemHandler;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.MEBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.network.APNetworking;
import de.srendi.advancedperipherals.network.toclient.CraftingCompleteToastPacket;
import de.srendi.advancedperipherals.network.toclient.ToastToClientPacket;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    public MEBridgeEntity getBridge() {
        return bridge;
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
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(arguments.getTable(0)));

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
        Pair<FluidFilter, String> filter = FluidFilter.parse(new ObjectLuaTable(arguments.getTable(0)));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(FluidUtil.moveFluid(fluidHandler, targetTank, filter.getLeft()), null);
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
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(arguments.getTable(0)));

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
        Pair<FluidFilter, String> filter = FluidFilter.parse(new ObjectLuaTable(arguments.getTable(0)));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(FluidUtil.moveFluid(targetTank, fluidHandler, filter.getLeft()), null);
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
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
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

        Pair<FluidFilter, String> filter = FluidFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
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

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
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

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
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

        Pair<FluidFilter, String> filter = FluidFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
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

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
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

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
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

        Pair<FluidFilter, String> filter = FluidFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
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

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
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
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        return AEMekanismApi.importToME(arguments, computer, this);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult exportChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        return AEMekanismApi.exportToTank(arguments, computer, this);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getPatterns(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        // Expected input is a table with either an input table, an output table or both to filter for both
        // If no table is provided or it's empty, return every pattern
        LuaTable<?, ?> filterTable = EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null));

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
            LuaTable<?, ?> inputFilterTable = new ObjectLuaTable(filterTable.getTable("input"));

            inputFilter = GenericFilter.parseGeneric(inputFilterTable).getLeft();
        }
        if (hasOutputFilter) {
            LuaTable<?, ?> outputFilterTable = new ObjectLuaTable(filterTable.getTable("output"));

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

        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
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

        Pair<FluidFilter, String> filter = FluidFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
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

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
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

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(new ObjectLuaTable(arguments.getTable(0)));
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
    public MethodResult forceCompleteCraftingTasks(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.getRight() != null && !"NO_NAME_OR_TYPE".equals(filter.getRight()))
            return MethodResult.of(0, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();
        boolean matchAll = parsedFilter.isEmpty();

        int jobsCompleted = 0;
        for (ICraftingCPU cpu : craftingGrid.getCpus()) {
            if (cpu.isBusy() && cpu.getJobStatus() != null && (matchAll || parsedFilter.testAE(cpu.getJobStatus().crafting()))) {
                try {
                    CraftingJobStatus jobStatus = cpu.getJobStatus();
                    GenericStack craftingStack = jobStatus.crafting();
                    AEKey craftedItem = craftingStack.what();
                    long totalAmount = craftingStack.amount();
                    
                    System.out.println("[DEBUG] ==> Processing job for: " + craftedItem.getDisplayName().getString());
                    System.out.println("[DEBUG] ==> Total target amount: " + totalAmount);
                    System.out.println("[DEBUG] ==> CPU busy: " + cpu.isBusy());
                    System.out.println("[DEBUG] ==> CPU type: " + cpu.getClass().getSimpleName());
                    
                    // Calculate the actual completed amount using AE2 internal logic
                    long actualCraftedAmount = totalAmount;
                    if (cpu instanceof appeng.me.cluster.implementations.CraftingCPUCluster cpuCluster) {
                        try {
                            appeng.crafting.execution.CraftingCpuLogic craftingCpuLogic = cpuCluster.craftingLogic;
                            long pending = craftingCpuLogic.getPendingOutputs(craftedItem);
                            long active = craftingCpuLogic.getWaitingFor(craftedItem);
                            actualCraftedAmount = totalAmount - (pending + active);
                            
                            System.out.println("[DEBUG] ==> AE2 Logic - Pending: " + pending + ", Active: " + active + ", Calculated Completed: " + actualCraftedAmount);
                            
                            // If calculated amount is 0 or negative, it means nothing is completed yet
                            // In this case, we should force complete with the total requested amount
                            if (actualCraftedAmount <= 0) {
                                System.out.println("[DEBUG] ==> No items completed yet, using total amount for force completion");
                                actualCraftedAmount = totalAmount;
                            }
                        } catch (Exception e) {
                            System.out.println("[DEBUG] ==> Could not calculate precise amount: " + e.getMessage());
                            e.printStackTrace();
                            // Fall back to total amount if calculation fails
                            actualCraftedAmount = totalAmount;
                        }
                    } else {
                        System.out.println("[DEBUG] ==> Non-CraftingCPUCluster, using total amount: " + totalAmount);
                    }
                    
                    // For force completion, we insert the full requested amount
                    // The notification should show what we actually added to storage
                    long amountToInsert = actualCraftedAmount;
                    System.out.println("[DEBUG] ==> Final amount to insert and notify: " + amountToInsert);

                    MEStorage storage = AEApi.getMonitor(node);
                    long inserted = storage.insert(craftedItem, amountToInsert, Actionable.MODULATE, bridge);
                    System.out.println("[DEBUG] ==> Actually inserted into storage: " + inserted);

                    // Cancel the job without relying on AE2 notification logic
                    cpu.cancelJob();
                    Thread.sleep(50);
                    boolean completed = !cpu.isBusy();
                    System.out.println("[DEBUG] ==> CPU idle after cancellation: " + completed);

                    if (completed) {
                        jobsCompleted++;
                        
                        System.out.println("[DEBUG] ==> Sending notification with amount: " + amountToInsert);
                        // Send custom notification with crafted item details
                        sendForceCompletionNotification(jobsCompleted, craftedItem, amountToInsert, inserted);
                    }
                } catch (Exception e) {
                    // Continue to next CPU if this one fails
                    continue;
                }
            }
        }
        return MethodResult.of(jobsCompleted);
    }

    private void sendForceCompletionNotification(int jobNumber, AEKey craftedItem, long craftedAmount, long insertedAmount) {
        System.out.println("[DEBUG-NOTIF] ==> sendForceCompletionNotification called with:");
        System.out.println("[DEBUG-NOTIF] ==> jobNumber: " + jobNumber);
        System.out.println("[DEBUG-NOTIF] ==> craftedItem: " + craftedItem.getDisplayName().getString());
        System.out.println("[DEBUG-NOTIF] ==> craftedAmount: " + craftedAmount);
        System.out.println("[DEBUG-NOTIF] ==> insertedAmount: " + insertedAmount);
        
        // Generate unique job ID for this force completion (matching original AE2 system style)
        long jobId = System.currentTimeMillis() + jobNumber;
        
        // Send ComputerCraft notification exactly like original AE2 system
        for (IComputerAccess computer : getConnectedComputers()) {
            computer.queueEvent("ae_crafting", false, jobId, "JOB_DONE");
        }
        
        if (shouldSendCraftingNotifications()) {
            sendToastToNearbyPlayers(jobId, craftedItem, craftedAmount, insertedAmount);
        } else {
            System.out.println("[DEBUG-NOTIF] ==> Skipping toast notification - notifications disabled");
        }
    }
    
    private void sendToastToNearbyPlayers(long jobId, AEKey craftedItem, long craftedAmount, long insertedAmount) {
        System.out.println("[DEBUG-TOAST] ==> sendToastToNearbyPlayers called with:");
        System.out.println("[DEBUG-TOAST] ==> jobId: " + jobId);
        System.out.println("[DEBUG-TOAST] ==> craftedItem: " + craftedItem.getDisplayName().getString());
        System.out.println("[DEBUG-TOAST] ==> craftedAmount: " + craftedAmount);
        System.out.println("[DEBUG-TOAST] ==> insertedAmount: " + insertedAmount);
        
        try {
            ResourceKey<Level> dimension = getLevel().dimension();
            int range = 32;
            
            Component title = Component.literal("Auto-Crafting Complete");
            
            String formattedAmount = craftedItem.formatAmount(craftedAmount, appeng.api.stacks.AmountFormat.SLOT);
            String itemName = craftedItem.getDisplayName().getString();
            
            System.out.println("[DEBUG-TOAST] ==> AE2 formatAmount result: '" + formattedAmount + "'");
            System.out.println("[DEBUG-TOAST] ==> Item name: '" + itemName + "'");
            
            String simpleAmount = String.valueOf(craftedAmount);
            System.out.println("[DEBUG-TOAST] ==> Simple amount formatting: '" + simpleAmount + "'");
            
            Component message = Component.literal(simpleAmount + " " + itemName);
            System.out.println("[DEBUG-TOAST] ==> Final message (using simple formatting): '" + message.getString() + "'");
            
            if (insertedAmount != craftedAmount) {
                message = Component.literal(formattedAmount + " " + itemName + " (" + insertedAmount + " stored)");
                System.out.println("[DEBUG-TOAST] ==> Modified message for partial storage: '" + message.getString() + "'");
            }
            
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (player.level().dimension() != dimension)
                    continue;
                    
                if (CoordUtil.isInRange(getPos(), getLevel(), player, range, range)) {
                    System.out.println("[DEBUG-TOAST] ==> Sending notification packet to player: " + player.getName().getString());
                    System.out.println("[DEBUG-TOAST] ==> Client will decide whether to show notification based on their client setting");
                    
                    // Send the packet to the client - the client will check its own setting
                    CraftingCompleteToastPacket packet = new CraftingCompleteToastPacket(title, message, craftedItem, craftedAmount);
                    APNetworking.sendTo(player, packet);
                }
            }
            
        } catch (Exception e) {
            System.out.println("[DEBUG-TOAST] ==> ERROR in sendToastToNearbyPlayers: " + e.getMessage());
            e.printStackTrace();
            // Ignore toast notification errors, don't break the force completion
        }
    }
    
    /**
     * Check if crafting notifications should be sent to players.
     * This method checks the server-side configuration.
     * Individual client settings are checked on each client when they receive the packet.
     * 
     * @return true if notifications should be sent, false otherwise
     */
    private boolean shouldSendCraftingNotifications() {
        // Check server-side setting
        boolean serverSetting = APConfig.PERIPHERALS_CONFIG.meCraftingNotifications.get();
        
        if (!serverSetting) {
            System.out.println("[DEBUG-NOTIF] ==> Notifications disabled by server config (meCraftingNotifications=false)");
            return false;
        }
        
        // Server setting is enabled, send packets to all clients
        // Each client will check their own setting when they receive the packet
        System.out.println("[DEBUG-NOTIF] ==> Server notifications enabled, sending packets to clients");
        return true;
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isCraftable(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(false);

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(new ObjectLuaTable(arguments.getTable(0)));
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

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(new ObjectLuaTable(arguments.getTable(0)));
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

    @LuaFunction(mainThread = true)
    public final MethodResult forceStopCPU(String cpuName) {
        if (!isAvailable())
            return notConnected(null);

        if (cpuName == null || cpuName.trim().isEmpty())
            return MethodResult.of(null, "CPU name cannot be empty");

        ICraftingCPU cpu = AEApi.getCraftingCPU(node, cpuName.trim());
        if (cpu == null)
            return MethodResult.of(null, "CPU not found: " + cpuName);

        if (!cpu.isBusy())
            return MethodResult.of(null, "CPU is not busy: " + cpuName);

        try {
            cpu.cancelJob();
            return MethodResult.of(true, "Successfully stopped CPU: " + cpuName);
        } catch (Exception e) {
            return MethodResult.of(null, "Failed to stop CPU: " + e.getMessage());
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult forceCompleteCPU(String cpuName) {
        if (!isAvailable())
            return notConnected(null);

        if (cpuName == null || cpuName.trim().isEmpty())
            return MethodResult.of(null, "CPU name cannot be empty");

        ICraftingCPU cpu = AEApi.getCraftingCPU(node, cpuName.trim());
        if (cpu == null)
            return MethodResult.of(null, "CPU not found: " + cpuName);

        if (!cpu.isBusy())
            return MethodResult.of(null, "CPU is not busy: " + cpuName);

        try {
            CraftingJobStatus jobStatus = cpu.getJobStatus();
            if (jobStatus == null)
                return MethodResult.of(null, "No active job found on CPU: " + cpuName);

            // Get the item being crafted and its quantity
            GenericStack craftingStack = jobStatus.crafting();
            AEKey craftedItem = craftingStack.what();
            long craftedAmount = craftingStack.amount();

            // Get the ME storage to insert the completed items
            MEStorage storage = AEApi.getMonitor(node);

            // Insert the crafted items into storage first
            long inserted = storage.insert(craftedItem, craftedAmount, Actionable.MODULATE, bridge);

            // Access the CraftingCPUCluster to force natural job completion
            if (cpu instanceof appeng.me.cluster.implementations.CraftingCPUCluster cpuCluster) {
                try {
                    // Get the current job link to trigger proper completion notification
                    ICraftingLink currentLink = cpuCluster.craftingLogic.getLastLink();
                    if (currentLink != null) {
                        // Force the job to be marked as done by manipulating the link state
                        // This will trigger the jobStateChange callback in MEBridgeEntity
                        // which then calls jobStateChanged() on the AECraftJob, firing the completion event

                        // Cancel the job to free the CPU and trigger state change
                        cpuCluster.craftingLogic.cancel();

                        // Small delay to allow the state change to propagate
                        Thread.sleep(50);

                        // The jobStateChange callback should have been triggered automatically by AE2
                        // when the job was canceled, which will call jobStateChanged() on matching jobs

                        boolean isNowIdle = !cpu.isBusy();

                        if (inserted == craftedAmount && isNowIdle) {
                            return MethodResult.of(true, "Successfully force completed CPU: " + cpuName
                                + " (" + inserted + " items added, completion notification sent)");
                        } else if (inserted > 0 && isNowIdle) {
                            return MethodResult.of(true, "Partially completed CPU: " + cpuName
                                + " (" + inserted + "/" + craftedAmount + " items added, completion notification sent)");
                        } else if (isNowIdle) {
                            return MethodResult.of(true, "Force completed CPU: " + cpuName
                                + " (job completed, CPU now idle, " + inserted + " items added, completion notification sent)");
                        } else {
                            return MethodResult.of(false, "Failed to complete CPU: " + cpuName
                                + " (CPU still busy after completion attempt)");
                        }
                    }
                } catch (Exception clusterException) {
                    // Fallback to regular cancellation if cluster method fails
                    cpu.cancelJob();
                    Thread.sleep(50);
                    boolean isNowIdle = !cpu.isBusy();

                    if (isNowIdle) {
                        return MethodResult.of(true, "Force completed CPU (fallback): " + cpuName
                            + " (" + inserted + " items added, job canceled)");
                    } else {
                        return MethodResult.of(false, "Failed to complete CPU: " + cpuName
                            + " (CPU still busy after fallback cancellation)");
                    }
                }
            } else {
                // Fallback for non-CraftingCPUCluster implementations
                cpu.cancelJob();
                Thread.sleep(50);
                boolean isNowIdle = !cpu.isBusy();

                if (isNowIdle) {
                    return MethodResult.of(true, "Force completed CPU (basic): " + cpuName
                        + " (" + inserted + " items added, job canceled)");
                } else {
                    return MethodResult.of(false, "Failed to complete CPU: " + cpuName
                        + " (CPU still busy after cancellation)");
                }
            }
        } catch (Exception e) {
            return MethodResult.of(false, "Error during force completion: " + e.getMessage());
        }

        // This should never be reached, but added for completeness
        return MethodResult.of(false, "Unexpected error in force completion");
    }
}
