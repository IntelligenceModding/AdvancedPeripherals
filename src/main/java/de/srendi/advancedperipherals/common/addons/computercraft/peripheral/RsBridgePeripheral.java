package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
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

    private MethodResult notConnected() {
        return MethodResult.of(null, "NOT_CONNECTED");
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
        if (!isConnected())
            return notConnected();

        return MethodResult.of(RefinedStorage.listItems(getNetwork()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listCraftableItems() {
        if (!isConnected())
            return notConnected();

        List<Object> items = new ArrayList<>();
        RefinedStorage.getCraftableItems(getNetwork()).forEach(item -> items.add(RefinedStorage.getObjectFromStack(item.copy(), getNetwork())));
        return MethodResult.of(items);
    }

    @LuaFunction(mainThread = true)
    public final Object listCraftableFluids() {
        if (!isConnected())
            return notConnected();

        List<Object> fluids = new ArrayList<>();
        RefinedStorage.getCraftableFluids(getNetwork()).forEach(fluid -> fluids.add(RefinedStorage.getObjectFromFluid(fluid, getNetwork())));
        return MethodResult.of(fluids);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxItemDiskStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(RefinedStorage.getMaxItemDiskStorage(getNetwork()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxFluidDiskStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(RefinedStorage.getMaxFluidDiskStorage(getNetwork()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxItemExternalStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(RefinedStorage.getMaxItemExternalStorage(getNetwork()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxFluidExternalStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(RefinedStorage.getMaxFluidExternalStorage(getNetwork()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listFluids() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(RefinedStorage.listFluids(getNetwork()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyUsage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(getNetwork().getEnergyUsage());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxEnergyStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(getNetwork().getEnergyStorage().getMaxEnergyStored());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyStorage() {
        if (!isConnected())
            return notConnected();

        return MethodResult.of(getNetwork().getEnergyStorage().getEnergyStored());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getPattern(IArguments arguments) throws LuaException {
        if (!isConnected())
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
        return importToSystem(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToSystem(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportFluid(@NotNull IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        IFluidHandler inventory = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return exportToTank(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportFluidToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        IFluidHandler inventory = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        return exportToTank(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importFluid(IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        IFluidHandler inventory = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        return importToSystem(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importFluidFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        IFluidHandler inventory = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        return importToSystem(arguments, inventory);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        if (!isConnected())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        return MethodResult.of(RefinedStorage.getObjectFromStack(RefinedStorage.findStackFromFilter(getNetwork(), getNetwork().getCraftingManager(), filter.getLeft()), getNetwork()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult craftItem(IArguments arguments) throws LuaException {
        if (!isConnected())
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

    @LuaFunction(mainThread = true)
    public final MethodResult craftFluid(IArguments arguments, int count) throws LuaException {
        if (!isConnected())
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

    @LuaFunction(mainThread = true)
    public final MethodResult isItemCrafting(IArguments arguments) throws LuaException {
        if (!isConnected())
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

    @LuaFunction(mainThread = true)
    public final MethodResult isItemCraftable(IArguments arguments) throws LuaException {
        if (!isConnected())
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
