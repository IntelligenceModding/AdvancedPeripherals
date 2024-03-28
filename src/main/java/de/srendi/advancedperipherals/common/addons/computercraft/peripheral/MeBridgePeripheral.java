package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.CraftJob;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MeFluidHandler;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MeItemHandler;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MeBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<MeBridgeEntity>> {

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
     * @param arguments the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToChest(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeItemHandler itemHandler = new MeItemHandler(monitor, tile);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if(filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(itemHandler, targetInventory, filter.getLeft()), null);
    }

    /**
     * exports a fluid out of the system to a valid tank
     *
     * @param arguments the arguments given by the computer
     * @param targetTank the give tank
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToTank(@NotNull IArguments arguments, @NotNull IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeFluidHandler fluidHandler = new MeFluidHandler(monitor, tile);
        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));

        if(filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveFluid(fluidHandler, targetTank, filter.getLeft()), null);
    }

    /**
     * imports an item to the system from a valid inventory
     *
     * @param arguments the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToME(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeItemHandler itemHandler = new MeItemHandler(monitor, tile);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if(filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(targetInventory, itemHandler, filter.getLeft()), null);
    }

    /**
     * imports a fluid to the system from a valid tank
     *
     * @param arguments the arguments given by the computer
     * @param targetTank the give tank
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToME(@NotNull IArguments arguments, @NotNull IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeFluidHandler fluidHandler = new MeFluidHandler(monitor, tile);
        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));

        if(filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveFluid(targetTank, fluidHandler, filter.getLeft()), null);
    }

    private MethodResult notConnected() {
        return MethodResult.of(null, "NOT_CONNECTED");
    }

    @LuaFunction(mainThread = true)
    public final boolean isConnected() {
        return node.getGrid() != null && node.hasGridBooted();
    }

    @LuaFunction
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if(filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = getCraftingCPU(cpuName);
        if(!cpuName.isEmpty() && target == null)
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

    @LuaFunction
    public final MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if(filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = getCraftingCPU(cpuName);
        if(!cpuName.isEmpty() && target == null)
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

    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyUsage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerUsage());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(node.getGrid().getEnergyService().getStoredPower());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvgPowerUsage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerUsage());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvgPowerInjection() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerInjection());
    }


    @LuaFunction(mainThread = true)
    public final MethodResult getMaxEnergyStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(node.getGrid().getEnergyService().getMaxStoredPower());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult isItemCrafting(IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        MEStorage monitor = AppEngApi.getMonitor(node);
        ICraftingService grid = node.getGrid().getService(ICraftingService.class);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if(filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");
        String cpuName = arguments.optString(1, "");
        ICraftingCPU craftingCPU = getCraftingCPU(cpuName);

        return MethodResult.of(AppEngApi.isItemCrafting(monitor, grid, parsedFilter, craftingCPU));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult isItemCraftable(IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if(filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        AEItemKey item = AEItemKey.of(parsedFilter.toItemStack());

        return MethodResult.of(getCraftingService().isCraftable(item));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportFluid(@NotNull IArguments arguments) throws LuaException {
        IFluidHandler handler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToTank(arguments, handler);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportFluidToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IFluidHandler handler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        return exportToTank(arguments, handler);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importFluid(IArguments arguments) throws LuaException {
        IFluidHandler handler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToME(arguments, handler);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importFluidFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IFluidHandler handler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToME(arguments, handler);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(@NotNull IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToChest(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return exportToChest(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToME(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToME(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        MEStorage monitor = AppEngApi.getMonitor(node);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if(filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, "EMPTY_FILTER");

        return MethodResult.of(AppEngApi.getObjectFromStack(AppEngApi.findAEStackFromFilter(monitor, getCraftingService(), parsedFilter), getCraftingService()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listItems() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.listStacks(AppEngApi.getMonitor(node), getCraftingService()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listCraftableItems() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.listCraftableStacks(AppEngApi.getMonitor(node), getCraftingService()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listFluid() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.listFluids(AppEngApi.getMonitor(node), getCraftingService()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listCraftableFluid() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.listCraftableFluids(AppEngApi.getMonitor(node), getCraftingService()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getTotalItemStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.getTotalItemStorage(node));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getTotalFluidStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.getTotalFluidStorage(node));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getUsedItemStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.getUsedItemStorage(node));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getUsedFluidStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.getUsedFluidStorage(node));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableItemStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.getAvailableItemStorage(node));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableFluidStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.getAvailableFluidStorage(node));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listCells() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(AppEngApi.listCells(node));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingCPUs() throws LuaException {
        if (!isConnected())
            return notConnected();

        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        List<Object> map = new ArrayList<>();

        for (ICraftingCPU iCraftingCPU : grid.getCpus()) {
            Object cpu = AppEngApi.getObjectFromCPU(iCraftingCPU);
            map.add(cpu);
        }
        return MethodResult.of(map);
    }

    public final ICraftingCPU getCraftingCPU(String cpuName) {
        if(cpuName.isEmpty()) return null;
        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        if (grid == null) return null;

        Iterator<ICraftingCPU> iterator = grid.getCpus().iterator();
        if (!iterator.hasNext()) return null;

        while (iterator.hasNext()) {
            ICraftingCPU cpu = iterator.next();

            if(Objects.requireNonNull(cpu.getName()).getString().equals(cpuName)) {
                return cpu;
            }
        }

        return null;
    }
}
