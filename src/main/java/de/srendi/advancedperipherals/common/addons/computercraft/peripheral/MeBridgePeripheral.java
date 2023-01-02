package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.CraftJob;
import de.srendi.advancedperipherals.common.addons.base.IStoragePeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.InventoryUtil;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MeBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<MeBridgeEntity>> implements IStoragePeripheral {

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

    private boolean canRun() {
        return node.isActive();
    }

    /**
     * exports an item out of the system to a valid inventory
     *
     * @param arguments       the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the exportable amount
     * @throws LuaException if stack does not exist or the system is offline - will be removed in 0.8
     */
    protected MethodResult exportToChest(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        if (stack.isEmpty())
            return MethodResult.of(0, "Could not find item");
        AEItemKey targetStack = AEItemKey.of(stack);

        long extracted = monitor.extract(targetStack, stack.getCount(), Actionable.SIMULATE, tile.getActionSource());
        if (extracted == 0)
            return MethodResult.of(0, "Item " + stack + " does not exists in the ME system");

        long transferableAmount = extracted;

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(targetInventory, stack, true);
        if (!remaining.isEmpty())
            transferableAmount -= remaining.getCount();

        if (transferableAmount == 0)
            return MethodResult.of(transferableAmount);

        extracted = monitor.extract(targetStack, transferableAmount, Actionable.MODULATE, tile.getActionSource());
        stack.setCount((int) extracted);
        remaining = ItemHandlerHelper.insertItemStacked(targetInventory, stack, false);

        if (!remaining.isEmpty())
            monitor.insert(AEItemKey.of(remaining), remaining.getCount(), Actionable.MODULATE, tile.getActionSource());

        return MethodResult.of(transferableAmount);
    }

    /**
     * imports an item to the system from a valid inventory
     *
     * @param arguments       the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the imported amount
     * @throws LuaException if system is offline - will be removed in 0.8
     */
    protected MethodResult importToME(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        if (stack.isEmpty())
            return MethodResult.of(0, "Could not find item");
        AEItemKey targetStack = AEItemKey.of(stack);
        int amount = stack.getCount();

        if (stack.getCount() == 0)
            return MethodResult.of(0);

        int transferableAmount = 0;

        for (int i = 0; i < targetInventory.getSlots(); i++) {
            if (targetInventory.getStackInSlot(i).sameItem(stack)) {
                int countInSlot = targetInventory.getStackInSlot(i).getCount();
                int extractCount = Math.min(countInSlot, amount);
                amount -= extractCount;
                int extracted = (int) monitor.insert(targetStack, extractCount, Actionable.MODULATE, tile.getActionSource());
                targetInventory.extractItem(i, extracted, false);
                transferableAmount += extracted;
            }
        }
        return MethodResult.of(transferableAmount);
    }

    @Override
    public final MethodResult isConnected() {
        return MethodResult.of(node.isOnline());
    }

    @Override
    public MethodResult isOnline() {
        if (!node.isOnline())
            return MethodResult.of(false, "Not connected");
        return MethodResult.of(canRun());
    }

    @Override
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");

        MEStorage monitor = AppEngApi.getMonitor(node);
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        if (stack.isEmpty())
            return MethodResult.of(null, "Could not find item");
        //TODO: We already do something like this when retrieving the stack. Check if we still need this before next release
        for (Object2LongMap.Entry<AEKey> potentialStack : monitor.getAvailableStacks()) {
            if (potentialStack.getKey() instanceof AEItemKey itemKey && itemKey.matches(stack)) {
                return MethodResult.of(AppEngApi.getObjectFromStack(Pair.of(potentialStack.getLongValue(), itemKey), getCraftingService()));
            }
        }
        return MethodResult.of(null, "Could not find item");
    }

    @Override
    public MethodResult getFluid(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    public final MethodResult listItems() {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");

        return MethodResult.of(AppEngApi.listStacks(AppEngApi.getMonitor(node), getCraftingService(), 0));
    }

    @Override
    public final MethodResult listCraftableItems() {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");

        return MethodResult.of(AppEngApi.listStacks(AppEngApi.getMonitor(node), getCraftingService(), 2));
    }

    @Override
    public final MethodResult listFluids() {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");

        return MethodResult.of(AppEngApi.listFluids(AppEngApi.getMonitor(node), getCraftingService(), 0));
    }

    @Override
    public final MethodResult listCraftableFluids() {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");

        return MethodResult.of(AppEngApi.listFluids(AppEngApi.getMonitor(node), getCraftingService(), 2));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingCPUs() throws LuaException {
        if (!canRun())
            return MethodResult.of(null, "System not connected or offline");

        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        List<Object> list = new ArrayList<>();
        Iterator<ICraftingCPU> iterator = grid.getCpus().iterator();
        if (!iterator.hasNext())
            return null;
        int i = 1;
        while (iterator.hasNext()) {
            list.add(i++, AppEngApi.getObjectFromCPU(iterator.next()));
        }

        return MethodResult.of(list);
    }

    @Override
    public final MethodResult importItem(IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToME(arguments, inventory);
    }

    @Override
    public final MethodResult exportItem(@NotNull IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToChest(arguments, inventory);
    }

    @Override
    public final MethodResult importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");

        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToME(arguments, inventory);
    }

    @Override
    public final MethodResult exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return exportToChest(arguments, inventory);
    }

    @Override
    public MethodResult getPattern(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    public MethodResult getPatterns() {
        return null;
    }

    @Override
    public final MethodResult getEnergyUsage() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(node.getGrid().getEnergyService().getIdlePowerUsage());
    }

    @Override
    public final MethodResult getStoredEnergy() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(node.getGrid().getEnergyService().getStoredPower());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvgPowerUsage() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerUsage());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvgPowerInjection() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerInjection());
    }

    @Override
    public final MethodResult getEnergyCapacity() {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        return MethodResult.of(node.getGrid().getEnergyService().getMaxStoredPower());
    }

    @Override
    public MethodResult getMaxItemExternalStorage() {
        return null;
    }

    @Override
    public MethodResult getMaxFluidExternalStorage() {
        return null;
    }

    @Override
    public MethodResult getMaxItemDiskStorage() {
        return null;
    }

    @Override
    public MethodResult getMaxFluidDiskStorage() {
        return null;
    }

    @Override
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(false, "System not connected or offline");

        MEStorage monitor = AppEngApi.getMonitor(node);
        ItemStack itemToCraft = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        if (itemToCraft.isEmpty())
            return MethodResult.of(false, "Item " + itemToCraft + " does not exists");
        CraftJob job = new CraftJob(owner.getLevel(), computer, node, itemToCraft, tile, tile);
        tile.addJob(job);
        ServerWorker.add(job::startCrafting);
        return MethodResult.of(job);
    }

    @Override
    public MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    public final MethodResult isItemCraftable(IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(false, "System not connected or offline");
        MEStorage monitor = AppEngApi.getMonitor(node);
        ICraftingService crafting = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEItemKey> stack = AppEngApi.findAEStackFromItemStack(monitor, crafting, ItemUtil.getItemStack(arguments.getTable(0), monitor));

        if (stack == null)
            return MethodResult.of(false, "Could not find item");

        return MethodResult.of(getCraftingService().isCraftable(stack.getRight()));
    }

    @Override
    public final MethodResult isItemCrafting(IArguments arguments) throws LuaException {
        if (!canRun())
            return MethodResult.of(0, "System not connected or offline");
        MEStorage monitor = AppEngApi.getMonitor(node);
        ICraftingService grid = node.getGrid().getService(ICraftingService.class);

        ItemStack itemStack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        if (itemStack.isEmpty())
            return MethodResult.of(false, "Could not find item");
        return MethodResult.of(AppEngApi.isItemCrafting(monitor, grid, itemStack));
    }

}
