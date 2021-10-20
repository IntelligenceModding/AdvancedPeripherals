package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.CraftJob;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.InventoryUtil;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TileEntityPeripheralOwner;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MeBridgePeripheral extends BasePeripheral<TileEntityPeripheralOwner<?>> {

    public static final String TYPE = "meBridge";
    private final IActionSource source;
    private IGridNode node;

    public MeBridgePeripheral(IActionSource source, PeripheralTileEntity<?> tileEntity) {
        super(TYPE, new TileEntityPeripheralOwner<>(tileEntity));
        this.source = source;
    }

    public void setNode(IGridNode node) {
        this.node = node;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableMeBridge;
    }

    protected int _exportItem(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        AppEngApi appEngApi = AppEngApi.getInstance();
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(appEngApi.getApi().storage().getStorageChannel(IItemStorageChannel.class));

        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        IAEItemStack targetStack = AEItemStack.fromItemStack(stack);
        if (targetStack == null)
            throw new LuaException("Illegal AE2 state ...");

        IAEItemStack extracted = monitor.extractItems(targetStack, Actionable.SIMULATE, source);
        if (extracted == null)
            throw new LuaException("Item " + stack + " does not exists in the ME system or the system is offline");

        int transferableAmount = (int) extracted.getStackSize();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(targetInventory, extracted.createItemStack(), true);
        if (!remaining.isEmpty()) {
            transferableAmount -= remaining.getCount();
        }

        if (transferableAmount == 0)
            return transferableAmount;

        targetStack.setStackSize(transferableAmount);

        extracted = monitor.extractItems(targetStack, Actionable.MODULATE, source);
        remaining = ItemHandlerHelper.insertItemStacked(targetInventory, extracted.createItemStack(), false);

        if (!remaining.isEmpty()) {
            monitor.injectItems(AEItemStack.fromItemStack(remaining), Actionable.MODULATE, source);
        }
        return transferableAmount;
    }

    protected int _importItem(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        IAEItemStack aeStack = AEItemStack.fromItemStack(stack);
        if (aeStack == null)
            throw new LuaException("Illegal AE2 state ...");

        IAEItemStack remaining = monitor.injectItems(aeStack, Actionable.SIMULATE, source);
        if (remaining != null && remaining.getStackSize() != 0)
            aeStack.setStackSize(aeStack.getStackSize() - remaining.getStackSize());

        if (aeStack.getStackSize() == 0)
            return 0;


        int amount = (int) aeStack.getStackSize();
        int transferableAmount = 0;

        for (int i = 0; i < targetInventory.getSlots(); i++) {
            if (targetInventory.getStackInSlot(i).sameItem(stack)) {
                int countInSlot = targetInventory.getStackInSlot(i).getCount();
                int extractCount = Math.min(countInSlot, amount);
                IAEItemStack extractionStack = aeStack.copy();
                extractionStack.setStackSize(extractCount);
                remaining = monitor.injectItems(extractionStack, Actionable.MODULATE, source);
                if (remaining != null)
                    extractCount -= remaining.getStackSize();
                targetInventory.extractItem(i, extractCount, false);
                transferableAmount += extractCount;
            }
        }
        return transferableAmount;
    }

    @LuaFunction
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack itemToCraft = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        if (itemToCraft.isEmpty())
            throw new LuaException("Item " + itemToCraft + " does not exists");
        CraftJob job = new CraftJob(owner.getWorld(), computer, node, itemToCraft, source);
        ServerWorker.add(job::startCrafting);
        return MethodResult.pullEvent("crafting", job);
    }

    @LuaFunction(mainThread = true)
    public final double getEnergyUsage() {
        return ((IEnergyGrid) node.getGrid().getCache(IEnergyGrid.class)).getAvgPowerUsage();
    }

    @LuaFunction(mainThread = true)
    public final double getEnergyStorage() {
        return ((IEnergyGrid) node.getGrid().getCache(IEnergyGrid.class)).getStoredPower();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergyStorage() {
        return ((IEnergyGrid) node.getGrid().getCache(IEnergyGrid.class)).getMaxStoredPower();
    }

    @LuaFunction(mainThread = true)
    public final boolean isItemCrafting(IArguments arguments) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ICraftingGrid grid = node.getGrid().getCache(ICraftingGrid.class);
        return grid.isRequesting(AppEngApi.getInstance().findAEStackFromItemStack(monitor, ItemUtil.getItemStack(arguments.getTable(0), monitor)));
    }

    @LuaFunction(mainThread = true)
    public final boolean isItemCraftable(IArguments arguments) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        return AppEngApi.getInstance().findAEStackFromItemStack(monitor, ItemUtil.getItemStack(arguments.getTable(0), monitor)).isCraftable();
    }

    @LuaFunction(mainThread = true)
    public final int exportItem(@NotNull IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return _exportItem(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final int exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return _exportItem(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final int importItem(IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return _importItem(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final int importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return _importItem(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        AppEngApi INSTANCE = AppEngApi.getInstance();
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(INSTANCE.getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        IAEItemStack aeStack = AEItemStack.fromItemStack(stack);
        if (aeStack == null)
            return MethodResult.of(null, "Cannot determinate item for search");
        for (IAEItemStack potentialStack : monitor.getStorageList()) {
            if (potentialStack.isSameType(aeStack))
                return MethodResult.of(INSTANCE.getMapFromStack(potentialStack));
        }
        aeStack.setStackSize(0);
        return MethodResult.of(INSTANCE.getMapFromStack(aeStack));
    }

    @LuaFunction(mainThread = true)
    public final Object[] listItems() {
        IMEMonitor<IAEItemStack> inventory = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        return new Object[]{AppEngApi.getInstance().iteratorToMapStack(inventory.getStorageList().iterator(), 0)};
    }

    @LuaFunction(mainThread = true)
    public final Object[] listCraftableItems() {
        IMEMonitor<IAEItemStack> inventory = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        return new Object[]{AppEngApi.getInstance().iteratorToMapStack(inventory.getStorageList().iterator(), 2)};
    }

    @LuaFunction(mainThread = true)
    public final Object[] listFluid() {
        IMEMonitor<IAEFluidStack> inventory = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IFluidStorageChannel.class));
        return new Object[]{AppEngApi.getInstance().iteratorToMapFluid(inventory.getStorageList().iterator(), 0)};
    }

    @LuaFunction(mainThread = true)
    public final Object[] listCraftableFluid() {
        IMEMonitor<IAEFluidStack> inventory = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IFluidStorageChannel.class));
        return new Object[]{AppEngApi.getInstance().iteratorToMapFluid(inventory.getStorageList().iterator(), 2)};
    }

    @LuaFunction(mainThread = true)
    public final Object[] getCraftingCPUs() throws LuaException {
        ICraftingGrid grid = node.getGrid().getCache(ICraftingGrid.class);
        if (grid == null)
            throw new LuaException("Not connected");
        Map<Integer, Object> map = new HashMap<>();
        Iterator<ICraftingCPU> iterator = grid.getCpus().iterator();
        if (!iterator.hasNext())
            return null;
        int i = 1;
        while (iterator.hasNext()) {
            Object o = AppEngApi.getInstance().getObjectFromCPU(iterator.next());
            if (o != null)
                map.put(i++, o);
        }
        return new Object[]{map};
    }
}