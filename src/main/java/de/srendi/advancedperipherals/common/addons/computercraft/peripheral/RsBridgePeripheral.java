package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RsItemHandler;
import de.srendi.advancedperipherals.common.blocks.blockentities.RsBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.InventoryUtil;
import de.srendi.advancedperipherals.common.util.ItemFilter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RsBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RsBridgeEntity>> {

    public static final String PERIPHERAL_TYPE = "rsBridge";

    public RsBridgePeripheral(RsBridgeEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    private RefinedStorageNode getNode() {
        return owner.tileEntity.getNode();
    }

    private INetwork getNetwork() {
        return getNode().getNetwork();
    }

    /**
     * Used to avoid NPE exceptions when the system is offline or the bridge not connected
     *
     * @param defaultValue return value if block is not connected
     * @param returnValue  return value if block is connected
     * @return defaultValue if system is not connected, returnValue if it is
     */
    private MethodResult ensureIsConnected(Object defaultValue, Supplier<MethodResult> returnValue) {
        if (!isConnected() || !getNetwork().canRun()) return MethodResult.of(defaultValue, "NOT_CONNECTED");
        return returnValue.get();
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableRSBridge.get();
    }

    @LuaFunction(mainThread = true)
    public final boolean isConnected() {
        return getNetwork() != null;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listItems() {
        return ensureIsConnected(null, () -> MethodResult.of(RefinedStorage.listItems(getNetwork())));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listCraftableItems() {
        return ensureIsConnected(null, () -> {
            List<Object> items = new ArrayList<>();
            RefinedStorage.getCraftableItems(getNetwork()).forEach(item -> items.add(RefinedStorage.getObjectFromStack(item, getNetwork())));
            return MethodResult.of(items);
        });
    }

    @LuaFunction(mainThread = true)
    public final Object listCraftableFluids() {
        return ensureIsConnected(null, () -> {
            List<Object> fluids = new ArrayList<>();
            RefinedStorage.getCraftableFluids(getNetwork()).forEach(fluid -> fluids.add(RefinedStorage.getObjectFromFluid(fluid, getNetwork())));
            return MethodResult.of(fluids);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxItemDiskStorage() {
        return ensureIsConnected(0, () -> MethodResult.of(RefinedStorage.getMaxItemDiskStorage(getNetwork())));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxFluidDiskStorage() {
        return ensureIsConnected(0, () -> MethodResult.of(RefinedStorage.getMaxFluidDiskStorage(getNetwork())));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxItemExternalStorage() {
        return ensureIsConnected(0, () -> MethodResult.of(RefinedStorage.getMaxItemExternalStorage(getNetwork())));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxFluidExternalStorage() {
        return ensureIsConnected(0, () -> MethodResult.of(RefinedStorage.getMaxFluidExternalStorage(getNetwork())));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listFluids() {
        return ensureIsConnected(null, () -> MethodResult.of(RefinedStorage.listFluids(getNetwork())));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyUsage() {
        return ensureIsConnected(0, () -> MethodResult.of(getNetwork().getEnergyUsage()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxEnergyStorage() {
        return ensureIsConnected(0, () -> MethodResult.of(getNetwork().getEnergyStorage().getMaxEnergyStored()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyStorage() {
        return ensureIsConnected(0, () -> MethodResult.of(getNetwork().getEnergyStorage().getEnergyStored()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getPattern(IArguments arguments) throws LuaException {
        return ensureIsConnected(null, () -> {
            Pair<ItemFilter, String> filter;
            try {
                filter = ItemFilter.parse(arguments.getTable(0));
            } catch (LuaException e) {
                throw new RuntimeException(e);
            }
            if (filter.rightPresent())
                return MethodResult.of(false, filter.getRight());

            ItemFilter parsedFilter = filter.getLeft();
            if (parsedFilter.isEmpty())
                return MethodResult.of(false, "EMPTY_FILTER");

            ItemStack patternItem = RefinedStorage.findStackFromFilter(getNetwork(), getNetwork().getCraftingManager(), parsedFilter);

            return MethodResult.of(RefinedStorage.getObjectFromPattern(getNetwork().getCraftingManager().getPattern(patternItem), getNetwork()));
        });
    }

    protected MethodResult exportToChest(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        RsItemHandler itemHandler = new RsItemHandler(getNetwork());
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if(filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(itemHandler, targetInventory, filter.getLeft()), null);
    }

    protected MethodResult importToSystem(@NotNull IArguments arguments, @NotNull IItemHandler targetInventory) throws LuaException {
        RsItemHandler itemHandler = new RsItemHandler(getNetwork());
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if(filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(targetInventory, itemHandler, filter.getLeft()), null);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToChest(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IArguments arguments) throws LuaException {
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToSystem(arguments, inventory);
    } /*

    @LuaFunction(mainThread = true)
    public final int exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isConnected())
            return 0;
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        if (chest == null)
            throw new LuaException("No valid inventory block for " + arguments.getString(1));

        BlockEntity targetEntity = (BlockEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory for " + arguments.getString(1));

        ItemStack extracted = getNetwork().extractItem(stack, stack.getCount(), 1, Action.SIMULATE);
        if (extracted.isEmpty())
            return 0;
        //throw new LuaException("Item " + item + " does not exists in the RS system or the system is offline");

        int transferableAmount = extracted.getCount();

        ItemStack remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, true);
        if (!remaining.isEmpty())
            transferableAmount -= remaining.getCount();

        extracted = getNetwork().extractItem(stack, transferableAmount, 1, Action.PERFORM);
        remaining = ItemHandlerHelper.insertItemStacked(inventory, extracted, false);

        if (!remaining.isEmpty())
            getNetwork().insertItem(remaining, remaining.getCount(), Action.PERFORM);

        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final int importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isConnected())
            return 0;
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        IPeripheral chest = computer.getAvailablePeripheral(arguments.getString(1));
        int count = stack.getCount();
        if (chest == null)
            throw new LuaException("No inventory block for " + arguments.getString(1));

        BlockEntity targetEntity = (BlockEntity) chest.getTarget();
        IItemHandler inventory = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().orElse(null) : null;
        if (inventory == null)
            throw new LuaException("No valid inventory for " + arguments.getString(1));

        int amount = count;

        int transferableAmount = 0;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).sameItem(stack)) {
                if (inventory.getStackInSlot(i).getCount() >= (amount - transferableAmount)) {
                    ItemStack extracted = inventory.extractItem(i, amount, false);
                    getNetwork().insertItem(stack, extracted.getCount(), Action.PERFORM);
                    transferableAmount += extracted.getCount();
                    break;
                } else {
                    ItemStack extracted = inventory.extractItem(i, amount, false);
                    amount -= extracted.getCount();
                    getNetwork().insertItem(stack, extracted.getCount(), Action.PERFORM);
                    transferableAmount += extracted.getCount();
                }
            }
        }
        return transferableAmount;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) {
        return (MethodResult) ensureIsConnected(null, () -> {
            try {
                return MethodResult.of(RefinedStorage.getItem(getNetwork(), ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()))));
            } catch (LuaException e) {
                return MethodResult.of(null, "unknown: " + e.getMessage());
            }
        });
    }

    @LuaFunction(mainThread = true)
    public final boolean craftItem(IArguments arguments) throws LuaException {
        if (!isConnected())
            return false;
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        if (stack == null)
            throw new LuaException("The item " + arguments.getTable(0).get("name") + "is not craftable");
        ICalculationResult result = getNetwork().getCraftingManager().create(stack, stack.getCount());
        CalculationResultType type = result.getType();
        if (result.getType() == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        return type == CalculationResultType.OK;
    }

    @LuaFunction(mainThread = true)
    public final boolean craftFluid(String fluid, int count) {
        if (!isConnected())
            return false;
        ICalculationResult result = getNetwork().getCraftingManager().create(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluid)), 0), count);
        CalculationResultType type = result.getType();
        if (result.getType() == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        return type == CalculationResultType.OK;
    }*/

    @LuaFunction(mainThread = true)
    public final boolean isItemCrafting(String item) {
        if (!isConnected())
            return false;
        ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
        for (ICraftingTask task : getNetwork().getCraftingManager().getTasks()) {
            ItemStack taskStack = task.getRequested().getItem();
            if (taskStack.sameItem(stack))
                return true;
        }
        return false;
    }

    /*@LuaFunction(mainThread = true)
    public final boolean isItemCraftable(IArguments arguments) throws LuaException {
        if (!isConnected())
            return false;
        ItemStack stack = ItemUtil.getItemStackRS(arguments.getTable(0), RefinedStorage.getItems(getNetwork()));
        return RefinedStorage.isItemCraftable(getNetwork(), stack);
    }*/
}
