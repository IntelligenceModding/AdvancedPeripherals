package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.config.Actionable;
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
import de.srendi.advancedperipherals.common.addons.appliedenergistics.MeItemHandler;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.*;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

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
     * @return the exportable amount
     */
    protected MethodResult exportToChest(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeItemHandler itemHandler = new MeItemHandler(monitor, getCraftingService(), tile);
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
     * @return the exportable amount
     * @throws LuaException if stack does not exist or the system is offline - will be removed in 0.8
     */
    protected long exportToTank(@NotNull IArguments arguments, @NotNull IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        FluidStack stack = FluidUtil.getFluidStack(arguments.getTable(0), monitor);
        AEFluidKey targetStack = AEFluidKey.of(stack);
        if (targetStack == null) throw new LuaException("Illegal AE2 state ...");

        long extracted = monitor.extract(targetStack, stack.getAmount(), Actionable.SIMULATE, tile.getActionSource());
        if (extracted == 0)
            throw new LuaException("Fluid " + stack + " does not exists in the ME system or the system is offline");

        long transferableAmount = extracted;

        int filled = targetTank.fill(stack, IFluidHandler.FluidAction.SIMULATE);
        int remaining = ((int) extracted) - filled;

        if (remaining > 0) {
            transferableAmount -= remaining;
        }

        if (transferableAmount == 0) return transferableAmount;

        extracted = monitor.extract(targetStack, transferableAmount, Actionable.MODULATE, tile.getActionSource());
        stack.setAmount((int) extracted);
        filled = targetTank.fill(stack, IFluidHandler.FluidAction.EXECUTE);
        remaining = ((int) extracted) - filled;

        if (remaining > 0) {
            monitor.insert(AEFluidKey.of(new FluidStack(stack.getFluid(), remaining)), remaining, Actionable.MODULATE, tile.getActionSource());
        }
        return transferableAmount;
    }

    /**
     * imports an item to the system from a valid inventory
     *
     * @param arguments the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the imported amount
     * @throws LuaException if system is offline - will be removed in 0.8
     */
    protected MethodResult importToME(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        MeItemHandler itemHandler = new MeItemHandler(monitor, getCraftingService(), tile);
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
     * @return the imported amount
     * @throws LuaException if system is offline - will be removed in 0.8
     */
    protected int importToME(@NotNull IArguments arguments, @NotNull IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        FluidStack stack = FluidUtil.getFluidStack(arguments.getTable(0), monitor);
        AEFluidKey aeStack = AEFluidKey.of(stack);
        int amount = stack.getAmount();

        if (aeStack == null) throw new LuaException("Illegal AE2 state ...");

        if (stack.getAmount() == 0) return 0;

        int transferableAmount = 0;

        for (int i = 0; i < targetTank.getTanks(); i++) {
            if (targetTank.getFluidInTank(i).isFluidEqual(stack)) {
                if (targetTank.getFluidInTank(i).getAmount() >= (amount - transferableAmount)) {
                    FluidStack extracted = targetTank.drain(new FluidStack(targetTank.getFluidInTank(i), amount), IFluidHandler.FluidAction.EXECUTE);
                    monitor.insert(aeStack, extracted.getAmount(), Actionable.MODULATE, tile.getActionSource());
                    transferableAmount += extracted.getAmount();
                    break;
                } else {
                    FluidStack extracted = targetTank.drain(new FluidStack(targetTank.getFluidInTank(i), amount), IFluidHandler.FluidAction.EXECUTE);
                    amount -= extracted.getAmount();
                    monitor.insert(aeStack, extracted.getAmount(), Actionable.MODULATE, tile.getActionSource());
                    transferableAmount += extracted.getAmount();
                }
            }
        }
        return transferableAmount;
    }

    @LuaFunction
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if(filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = getCraftingCPU(cpuName);
        if(!cpuName.isEmpty() && target == null) return MethodResult.of(false, "CPU " + cpuName + " does not exists");

        CraftJob job = new CraftJob(owner.getLevel(), computer, node, new ItemStack(parsedFilter.getItem(), parsedFilter.getCount()), tile, tile, target);
        tile.addJob(job);
        ServerWorker.add(job::startCrafting);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final double getEnergyUsage() {
        return node.getGrid().getEnergyService().getAvgPowerUsage();
    }

    @LuaFunction(mainThread = true)
    public final double getEnergyStorage() {
        return node.getGrid().getEnergyService().getStoredPower();
    }

    @LuaFunction(mainThread = true)
    public final double getAvgPowerUsage() {
        return node.getGrid().getEnergyService().getAvgPowerUsage();
    }

    @LuaFunction(mainThread = true)
    public final double getAvgPowerInjection() {
        return node.getGrid().getEnergyService().getAvgPowerInjection();
    }


    @LuaFunction(mainThread = true)
    public final double getMaxEnergyStorage() {
        return node.getGrid().getEnergyService().getMaxStoredPower();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult isItemCrafting(IArguments arguments) throws LuaException {
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

        // No need to search in the system for the item. That would just be a waste of time
        // But we still use a filter here to maintain a better compatibility with older scripts
        return MethodResult.of(AppEngApi.isItemCrafting(monitor, grid, new ItemStack(parsedFilter.getItem()), craftingCPU));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult isItemCraftable(IArguments arguments) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if(filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        // No need to search in the system for the item. That would just be a waste of time
        // But we still use a filter here to maintain a better compatibility with older scripts
        AEItemKey item = AEItemKey.of(new ItemStack(parsedFilter.getItem()));

        return MethodResult.of(getCraftingService().isCraftable(item));
    }

    /*@LuaFunction(mainThread = true)
    public final long exportFluid(@NotNull IArguments arguments) throws LuaException {
        IFluidHandler handler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToTank(arguments, handler);
    }

    @LuaFunction(mainThread = true)
    public final long exportFluidToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IFluidHandler handler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        return exportToTank(arguments, handler);
    }

    @LuaFunction(mainThread = true)
    public final int importFluid(IArguments arguments) throws LuaException {
        IFluidHandler handler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToME(arguments, handler);
    }

    @LuaFunction(mainThread = true)
    public final int importFluidFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IFluidHandler handler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToME(arguments, handler);
    }*/

    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(@NotNull IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToChest(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return exportToChest(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToME(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToME(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if(filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, "EMPTY_FILTER");

        return MethodResult.of(AppEngApi.getObjectFromStack(AppEngApi.findAEStackFromItemStack(monitor, getCraftingService(), parsedFilter), getCraftingService()));
    }

    @LuaFunction(mainThread = true)
    public final Object[] listItems() {
        return new Object[]{AppEngApi.listStacks(AppEngApi.getMonitor(node), getCraftingService(), 0)};
    }

    @LuaFunction(mainThread = true)
    public final Object[] listCraftableItems() {
        return new Object[]{AppEngApi.listStacks(AppEngApi.getMonitor(node), getCraftingService(), 2)};
    }

    @LuaFunction(mainThread = true)
    public final Object[] listFluid() {
        return new Object[]{AppEngApi.listFluids(AppEngApi.getMonitor(node), getCraftingService(), 0)};
    }

    @LuaFunction(mainThread = true)
    public final Object[] listCraftableFluid() {
        return new Object[]{AppEngApi.listFluids(AppEngApi.getMonitor(node), getCraftingService(), 2)};
    }

    @LuaFunction(mainThread = true)
    public final long getTotalItemStorage() {
        return AppEngApi.getTotalItemStorage(node);
    }

    @LuaFunction(mainThread = true)
    public final long getTotalFluidStorage() {
        return AppEngApi.getTotalFluidStorage(node);
    }

    @LuaFunction(mainThread = true)
    public final long getUsedItemStorage() {
        return AppEngApi.getUsedItemStorage(node);
    }

    @LuaFunction(mainThread = true)
    public final long getUsedFluidStorage() {
        return AppEngApi.getUsedFluidStorage(node);
    }

    @LuaFunction(mainThread = true)
    public final long getAvailableItemStorage() {
        return AppEngApi.getAvailableItemStorage(node);
    }

    @LuaFunction(mainThread = true)
    public final long getAvailableFluidStorage() {
        return AppEngApi.getAvailableFluidStorage(node);
    }

    @LuaFunction(mainThread = true)
    public final Object[] listCells() {
        return new Object[]{AppEngApi.listCells(node)};
    }

    @LuaFunction(mainThread = true)
    public final Object[] getCraftingCPUs() throws LuaException {
        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        if (grid == null) throw new LuaException("Not connected");
        Map<Integer, Object> map = new HashMap<>();
        Iterator<ICraftingCPU> iterator = grid.getCpus().iterator();
        if (!iterator.hasNext()) return null;
        int i = 1;
        while (iterator.hasNext()) {
            Object o = AppEngApi.getObjectFromCPU(iterator.next());
            map.put(i++, o);
        }
        return new Object[]{map};
    }

    public final ICraftingCPU getCraftingCPU(String cpuName) {
        if(cpuName.equals("")) return null;
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
