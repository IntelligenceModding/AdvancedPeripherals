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
     * @throws LuaException if stack does not exist or the system is offline - will be removed in 0.8
     */
    protected long exportToChest(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        AEItemKey targetStack = AEItemKey.of(stack);
        if (targetStack == null) throw new LuaException("Illegal AE2 state ...");

        long extracted = monitor.extract(targetStack, stack.getCount(), Actionable.SIMULATE, tile.getActionSource());
        if (extracted == 0)
            throw new LuaException("Item " + stack + " does not exists in the ME system or the system is offline");

        long transferableAmount = extracted;

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(targetInventory, stack, true);
        if (!remaining.isEmpty()) {
            transferableAmount -= remaining.getCount();
        }

        if (transferableAmount == 0) return transferableAmount;

        extracted = monitor.extract(targetStack, transferableAmount, Actionable.MODULATE, tile.getActionSource());
        stack.setCount((int) extracted);
        remaining = ItemHandlerHelper.insertItemStacked(targetInventory, stack, false);

        if (!remaining.isEmpty()) {
            monitor.insert(AEItemKey.of(remaining), remaining.getCount(), Actionable.MODULATE, tile.getActionSource());
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
    protected int importToME(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        AEItemKey aeStack = AEItemKey.of(stack);
        int amount = stack.getCount();

        if (aeStack == null) throw new LuaException("Illegal AE2 state ...");

        if (stack.getCount() == 0) return 0;

        int transferableAmount = 0;

        for (int i = 0; i < targetInventory.getSlots(); i++) {
            if (targetInventory.getStackInSlot(i).sameItem(stack)) {
                if (targetInventory.getStackInSlot(i).getCount() >= (amount - transferableAmount)) {
                    ItemStack extracted = targetInventory.extractItem(i, amount, false);
                    monitor.insert(aeStack, extracted.getCount(), Actionable.MODULATE, tile.getActionSource());
                    transferableAmount += extracted.getCount();
                    break;
                } else {
                    ItemStack extracted = targetInventory.extractItem(i, amount, false);
                    amount -= extracted.getCount();
                    monitor.insert(aeStack, extracted.getCount(), Actionable.MODULATE, tile.getActionSource());
                    transferableAmount += extracted.getCount();
                }
            }
        }
        return transferableAmount;
    }

    @LuaFunction
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        ItemStack itemToCraft = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        if (itemToCraft.isEmpty()) return MethodResult.of(false, "Item " + itemToCraft + " does not exists");

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = getCraftingCPU(cpuName);
        if(!cpuName.isEmpty() && target == null) return MethodResult.of(false, "CPU " + cpuName + " does not exists");

        CraftJob job = new CraftJob(owner.getLevel(), computer, node, itemToCraft, tile, tile, target);
        tile.addJob(job);
        ServerWorker.add(job::startCrafting);
        return MethodResult.of(true);
        //TODO - 0.8: This needs our attention. We need to return better and more useful data to the user. See https://github.com/Seniorendi/AdvancedPeripherals/issues/323
        //return MethodResult.pullEvent("crafting", job);
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
    public final boolean isItemCrafting(IArguments arguments) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        ICraftingService grid = node.getGrid().getService(ICraftingService.class);

        ItemStack itemStack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        String cpuName = arguments.optString(1, "");
        ICraftingCPU craftingCPU = getCraftingCPU(cpuName);

        return AppEngApi.isItemCrafting(monitor, grid, itemStack, craftingCPU);
    }

    @LuaFunction(mainThread = true)
    public final boolean isItemCraftable(IArguments arguments) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        ICraftingService crafting = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEItemKey> stack = AppEngApi.findAEStackFromItemStack(monitor, crafting, ItemUtil.getItemStack(arguments.getTable(0), monitor));

        if (stack == null) {
            // If the item stack does not exist, it cannot be craftable.
            return false;
        }

        return getCraftingService().isCraftable(stack.getRight());
    }

    @LuaFunction(mainThread = true)
    public final long exportItem(@NotNull IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToChest(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final long exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return exportToChest(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final int importItem(IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToME(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final int importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToME(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        MEStorage monitor = AppEngApi.getMonitor(node);
        ItemStack stack = ItemUtil.getItemStack(arguments.getTable(0), monitor);
        if (stack.isEmpty()) return MethodResult.of(null, "Cannot determinate item for search");
        for (Object2LongMap.Entry<AEKey> potentialStack : monitor.getAvailableStacks()) {
            if (potentialStack.getKey() instanceof AEItemKey itemKey) {
                if (itemKey.matches(stack))
                    return MethodResult.of(AppEngApi.getObjectFromStack(new Pair<>(potentialStack.getLongValue(), itemKey), getCraftingService()));
            }
        }
        return MethodResult.of((Object) null);
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

    public final ICraftingCPU getCraftingCPU(String cpuName)  {
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
