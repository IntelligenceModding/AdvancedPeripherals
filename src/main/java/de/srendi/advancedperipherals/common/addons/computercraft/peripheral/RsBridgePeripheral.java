package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RsFluidHandler;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RsItemHandler;
import de.srendi.advancedperipherals.common.blocks.blockentities.RsBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.*;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RsBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RsBridgeEntity>> implements IStoragePeripheral {

    public static final String PERIPHERAL_TYPE = "rs_bridge";

    public RsBridgePeripheral(RsBridgeEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    private RefinedStorageNode getNode() {
        return owner.tileEntity.getNode();
    }

    private INetwork getNetwork() {
        return getNode().getNetwork();
    }

    private MethodResult notConnected() {
        return MethodResult.of(null, "NOT_CONNECTED");
    }

    private boolean isAvailable() {
        return getNetwork() != null;
    }

    @Override
    public final MethodResult isConnected() {
        return MethodResult.of(isAvailable());
    }

    @Override
    public MethodResult isOnline() {
        // Is there a more proper method?
        return MethodResult.of(getNode().isActive());
    }
    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableRSBridge.get();
    }


    @Override
    public final MethodResult listItems() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RefinedStorage.listItems(getNetwork()));
    }

    @Override
    public final MethodResult listCraftableItems() {
        if (!isAvailable())
            return notConnected();

        List<Object> items = new ArrayList<>();
        RefinedStorage.getCraftableItems(getNetwork()).forEach(item -> items.add(RefinedStorage.getObjectFromStack(item.copy(), getNetwork())));
        return MethodResult.of(items);
    }

    @Override
    public final MethodResult listCraftableFluids() {
        if (!isAvailable())
            return notConnected();

        List<Object> fluids = new ArrayList<>();
        RefinedStorage.getCraftableFluids(getNetwork()).forEach(fluid -> fluids.add(RefinedStorage.getObjectFromFluid(fluid, getNetwork())));
        return MethodResult.of(fluids);
    }

    @Override
    public MethodResult listCells() {
        return null;
    }

    @Override
    public final MethodResult getTotalItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RefinedStorage.getMaxItemDiskStorage(getNetwork()));
    }

    @Override
    public final MethodResult getTotalFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RefinedStorage.getMaxFluidDiskStorage(getNetwork()));
    }

    @Override
    public MethodResult getUsedExternItemStorage() {
        return null;
    }

    @Override
    public MethodResult getUsedExternFluidStorage() {
        return null;
    }

    @Override
    public MethodResult getUsedItemStorage() {
        return null;
    }

    @Override
    public MethodResult getUsedFluidStorage() {
        return null;
    }

    @Override
    public MethodResult getAvailableExternItemStorage() {
        return null;
    }

    @Override
    public MethodResult getAvailableExternFluidStorage() {
        return null;
    }

    @Override
    public MethodResult getAvailableItemStorage() {
        return null;
    }

    @Override
    public MethodResult getAvailableFluidStorage() {
        return null;
    }

    @Override
    public final MethodResult getTotalExternItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RefinedStorage.getMaxItemExternalStorage(getNetwork()));
    }

    @Override
    public final MethodResult getTotalExternFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RefinedStorage.getMaxFluidExternalStorage(getNetwork()));
    }

    @Override
    public final MethodResult listFluids() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RefinedStorage.listFluids(getNetwork()));
    }

    @Override
    public final MethodResult getEnergyUsage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(getNetwork().getEnergyUsage());
    }

    @Override
    public final MethodResult getEnergyCapacity() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(getNetwork().getEnergyStorage().getMaxEnergyStored());
    }

    @Override
    public final MethodResult getStoredEnergy() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(getNetwork().getEnergyStorage().getEnergyStored());
    }

    @Override
    public final MethodResult getPattern(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        ItemStack patternItem = RefinedStorage.findStackFromFilter(getNetwork(), getNetwork().getCraftingManager(), parsedFilter);

        return MethodResult.of(RefinedStorage.getObjectFromPattern(getNetwork().getCraftingManager().getPattern(patternItem), getNetwork()));
    }

    @Override
    public MethodResult getPatterns() {
        return null;
    }

    protected MethodResult exportToChest(@NotNull IArguments arguments, @Nullable IItemHandler targetInventory) throws LuaException {
        RsItemHandler itemHandler = new RsItemHandler(getNetwork());
        if (targetInventory == null)
            return MethodResult.of(0, "INVALID_TARGET");

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(itemHandler, targetInventory, filter.getLeft()), null);
    }

    protected MethodResult importToSystem(@NotNull IArguments arguments, @Nullable IItemHandler targetInventory) throws LuaException {
        RsItemHandler itemHandler = new RsItemHandler(getNetwork());
        if (targetInventory == null)
            return MethodResult.of(0, "INVALID_TARGET");

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(targetInventory, itemHandler, filter.getLeft()), null);
    }

    protected MethodResult exportToTank(@NotNull IArguments arguments, @Nullable IFluidHandler targetInventory) throws LuaException {
        RsFluidHandler itemHandler = new RsFluidHandler(getNetwork());
        if (targetInventory == null)
            return MethodResult.of(0, "INVALID_TARGET");

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveFluid(itemHandler, targetInventory, filter.getLeft()), null);
    }

    protected MethodResult importToSystem(@NotNull IArguments arguments, @Nullable IFluidHandler targetInventory) throws LuaException {
        RsFluidHandler itemHandler = new RsFluidHandler(getNetwork());
        if (targetInventory == null)
            return MethodResult.of(0, "INVALID_TARGET");

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveFluid(targetInventory, itemHandler, filter.getLeft()), null);
    }

    @Override
    public final MethodResult exportItem(@NotNull IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToChest(arguments, inventory);
    }

    @Override
    public final MethodResult exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return exportToChest(arguments, inventory);
    }

    @Override
    public final MethodResult importItem(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToSystem(arguments, inventory);
    }

    @Override
    public final MethodResult importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToSystem(arguments, inventory);
    }

    @Override
    public final MethodResult exportFluid(@NotNull IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IFluidHandler inventory = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToTank(arguments, inventory);
    }

    @Override
    public final MethodResult exportFluidToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IFluidHandler inventory = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        return exportToTank(arguments, inventory);
    }

    @Override
    public final MethodResult importFluid(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IFluidHandler inventory = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToSystem(arguments, inventory);
    }

    @Override
    public final MethodResult importFluidFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IFluidHandler inventory = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToSystem(arguments, inventory);
    }

    @Override
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        return MethodResult.of(RefinedStorage.getObjectFromStack(RefinedStorage.findStackFromFilter(getNetwork(), getNetwork().getCraftingManager(), filter.getLeft()), getNetwork()));
    }

    @Override
    public MethodResult getFluid(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    public final MethodResult craftItem(IComputerAccess computerAccess, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();


        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemStack stack = RefinedStorage.findStackFromFilter(getNetwork(), getNetwork().getCraftingManager(), filter.getLeft());
        if (stack == null)
            return MethodResult.of(null, "NOT_CRAFTABLE");

        ICalculationResult result = getNetwork().getCraftingManager().create(stack, filter.getLeft().getCount());
        CalculationResultType type = result.getType();
        if (type == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        AdvancedPeripherals.debug("Crafting Result of '" + ItemUtil.getRegistryKey(stack).toString() + "':" + type);
        return MethodResult.of(type == CalculationResultType.OK);
    }

    @Override
    public final MethodResult craftFluid(IComputerAccess computerAccess, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidStack stack = RefinedStorage.findFluidFromFilter(getNetwork(), null, filter.getLeft());
        if (stack == null)
            return MethodResult.of(null, "NOT_CRAFTABLE");

        ICalculationResult result = getNetwork().getCraftingManager().create(stack, filter.getLeft().getCount());
        CalculationResultType type = result.getType();
        if (result.getType() == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        AdvancedPeripherals.debug("Crafting Result of '" + FluidUtil.getRegistryKey(stack).toString() + "':" + type);
        return MethodResult.of(type == CalculationResultType.OK);
    }

    @Override
    public final MethodResult isItemCrafting(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemStack stack = RefinedStorage.findStackFromFilter(getNetwork(), null, filter.getLeft());
        if (stack == null)
            return MethodResult.of(null, "NOT_CRAFTABLE");

        for (ICraftingTask task : getNetwork().getCraftingManager().getTasks()) {
            ItemStack taskStack = task.getRequested().getItem();
            if (taskStack != null && taskStack.sameItem(stack))
                return MethodResult.of(true);
        }
        return MethodResult.of(false);
    }

    @Override
    public MethodResult isFluidCraftable(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    public MethodResult isFluidCrafting(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    public final MethodResult isItemCraftable(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");
        return MethodResult.of(RefinedStorage.isItemCraftable(getNetwork(), parsedFilter.toItemStack()));
    }
}
