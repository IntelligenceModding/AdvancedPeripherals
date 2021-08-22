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
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.CraftJob;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MeBridgePeripheral extends BasePeripheral {

    public static final String TYPE = "meBridge";

    private IGridNode node;
    private final IActionSource source;

    public MeBridgePeripheral(IActionSource source, PeripheralTileEntity<?> tileEntity) {
        super(TYPE, tileEntity);
        this.source = source;
    }

    public void setNode(IGridNode node) {
        this.node = node;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableMeBridge;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack itemToCraft = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        if (itemToCraft.isEmpty())
            throw new NullPointerException("Item " + itemToCraft + " does not exists");
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
    public final int exportItem(IArguments arguments) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        IAEItemStack aeStack = AppEngApi.getInstance().findAEStackFromItemStack(monitor, stack);
        if (aeStack == null)
            throw new LuaException("Item " + stack + " does not exists in the ME system or the system is offline");
        Direction direction = validateSide(arguments.getString(1));

        TileEntity targetEntity = owner.getWorld().getBlockEntity(owner.getPos().relative(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory at " + direction);

        aeStack.setStackSize(stack.getCount());
        IAEItemStack extracted = monitor.extractItems(aeStack, Actionable.SIMULATE, source);
        if (extracted == null)
            throw new LuaException("Item " + stack + " does not exists in the ME system or the system is offline");

        int transferableAmount = (int) extracted.getStackSize();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted.createItemStack(), true);
        if (!remaining.isEmpty()) {
            transferableAmount -= remaining.getCount();
        }

        aeStack.setStackSize(transferableAmount);
        extracted = monitor.extractItems(aeStack, Actionable.MODULATE, source);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted.createItemStack(), false);

        if (!remaining.isEmpty()) {
            aeStack.setStackSize(remaining.getCount());
            monitor.injectItems(aeStack, Actionable.MODULATE, source);
        }
        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int importItem(IArguments arguments) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        IAEItemStack aeStack = AEItemStack.fromItemStack(stack);
        if (aeStack == null)
            throw new LuaException("Item " + stack + " does not exists in the ME system or the system is offline");
        Direction direction = validateSide(arguments.getString(1));

        TileEntity targetEntity = owner.getWorld().getBlockEntity(owner.getPos().relative(direction));
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory at " + direction);

        int amount = stack.getCount();
        int transferableAmount = 0;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).sameItem(stack)) {
                if (inventory.getStackInSlot(i).getCount() >= amount) {
                    transferableAmount += amount;
                    aeStack.setStackSize(amount);
                    monitor.injectItems(aeStack, Actionable.MODULATE, source);
                    inventory.extractItem(i, amount, false);
                    break;
                } else {
                    amount -= inventory.getStackInSlot(i).getCount();
                    transferableAmount += inventory.getStackInSlot(i).getCount();
                    aeStack.setStackSize(inventory.getStackInSlot(i).getCount());
                    monitor.injectItems(aeStack, Actionable.MODULATE, source);
                    inventory.extractItem(i, inventory.getStackInSlot(i).getCount(), false);
                }
            }
        }
        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        IAEItemStack aeStack = AEItemStack.fromItemStack(stack);
        if (aeStack == null)
            throw new LuaException("Item " + stack + " does not exists in the ME system or the system is offline");

        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        if (chest == null)
            throw new LuaException("No valid chest for " + arguments.getString(1));

        TileEntity targetEntity = (TileEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory for " + arguments.getString(1));

        aeStack.setStackSize(stack.getCount());
        IAEItemStack extracted = monitor.extractItems(aeStack, Actionable.SIMULATE, source);
        if (extracted == null)
            throw new LuaException("Item " + stack + " does not exists in the ME system or the system is offline");

        int transferableAmount = (int) extracted.getStackSize();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted.createItemStack(), true);
        if (!remaining.isEmpty()) {
            transferableAmount -= remaining.getCount();
        }

        aeStack.setStackSize(transferableAmount);
        extracted = monitor.extractItems(aeStack, Actionable.MODULATE, source);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted.createItemStack(), false);

        if (!remaining.isEmpty()) {
            aeStack.setStackSize(remaining.getCount());
            monitor.injectItems(aeStack, Actionable.MODULATE, source);
        }
        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        IAEItemStack aeStack = AEItemStack.fromItemStack(stack);
        if (aeStack == null)
            throw new LuaException("Item " + stack + " does not exists in the ME system or the system is offline");

        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        if (chest == null)
            throw new LuaException("Peripheral not found" + arguments.getString(1));

        TileEntity targetEntity = (TileEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory for " + arguments.getString(1));

        int amount = stack.getCount();
        int transferableAmount = 0;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).sameItem(stack)) {
                if (inventory.getStackInSlot(i).getCount() >= amount) {
                    transferableAmount += amount;
                    aeStack.setStackSize(amount);
                    monitor.injectItems(aeStack, Actionable.MODULATE, source);
                    inventory.extractItem(i, amount, false);
                    break;
                } else {
                    amount -= inventory.getStackInSlot(i).getCount();
                    transferableAmount += inventory.getStackInSlot(i).getCount();
                    aeStack.setStackSize(inventory.getStackInSlot(i).getCount());
                    monitor.injectItems(aeStack, Actionable.MODULATE, source);
                    inventory.extractItem(i, inventory.getStackInSlot(i).getCount(), false);
                }
            }
        }
        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final Object[] getItem(IArguments arguments) throws LuaException {
        IMEMonitor<IAEItemStack> monitor = ((IStorageGrid) node.getGrid().getCache(IStorageGrid.class)).getInventory(AppEngApi.getInstance().getApi().storage().getStorageChannel(IItemStorageChannel.class));
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        IAEItemStack aeStack = AEItemStack.fromItemStack(stack);
        if (aeStack == null)
            return new Object[]{}; //Return nothing instead of crashing the program
        //throw new LuaException("Item " + stack + " does not exists in the ME system or the system is offline");
        return new Object[]{AppEngApi.getInstance().getMapFromStack(aeStack)};
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