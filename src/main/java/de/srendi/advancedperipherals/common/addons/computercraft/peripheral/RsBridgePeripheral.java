package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.apis.TableHelper;
import dan200.computercraft.core.computer.ComputerSide;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSApi;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSNode;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RsFluidHandler;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RsItemHandler;
import de.srendi.advancedperipherals.common.blocks.blockentities.RsBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class RsBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RsBridgeEntity>> implements IStorageSystemPeripheral {

    public static final String PERIPHERAL_TYPE = "rs_bridge";

    public RsBridgePeripheral(RsBridgeEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    private RSNode getNode() {
        return owner.tileEntity.getNode();
    }

    private INetwork getNetwork() {
        return getNode().getNetwork();
    }

    private MethodResult notConnected(@Nullable Object defaultValue) {
        return MethodResult.of(defaultValue, StatusConstants.NOT_CONNECTED.toString());
    }


    private boolean isAvailable() {
        return getNetwork() != null;
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableRSBridge.get();
    }

    @Override
    @LuaFunction(mainThread = true)
    public final boolean isConnected() {
        return isAvailable();
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isOnline() {
        // Is there a more proper method?
        return MethodResult.of(getNode().isActive());
    }


    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getItems(IArguments arguments) {
        if (!isAvailable())
            return notConnected(null);

        return MethodResult.of(RSApi.listItems(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCraftableItems(IArguments arguments) {
        if (!isAvailable())
            return notConnected(null);

        List<Object> items = new ArrayList<>();
        RSApi.getCraftableItems(getNetwork()).forEach(item -> items.add(RSApi.parseItemStack(item, getNetwork())));
        return MethodResult.of(items);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCraftableFluids(IArguments arguments) {
        if (!isAvailable())
            return notConnected(null);

        List<Object> fluids = new ArrayList<>();
        RSApi.getCraftableFluids(getNetwork()).forEach(fluid -> fluids.add(RSApi.parseFluidStack(fluid, getNetwork())));
        return MethodResult.of(fluids);
    }

    @Override
    public MethodResult getCraftableChemicals(IArguments arguments) {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCells() {
        if (!isAvailable())
            return notConnected(null);

        return MethodResult.of(RSApi.getStorageDisks(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getDrives() {
        if (!isAvailable())
            return notConnected(null);

        return MethodResult.of(RSApi.getDiskDrives(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getMaxItemDiskStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getMaxFluidDiskStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(-1);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedExternalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getUsedItemExternalStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedExternalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getUsedFluidExternalStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(-1);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getUsedItemDiskStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getUsedFluidDiskStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(-1);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableExternalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getMaxItemExternalStorage(getNetwork()) - RSApi.getUsedItemExternalStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableExternalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getMaxFluidExternalStorage(getNetwork()) - RSApi.getUsedFluidExternalStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(-1);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getMaxItemDiskStorage(getNetwork()) - RSApi.getUsedItemDiskStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getMaxFluidDiskStorage(getNetwork()) - RSApi.getUsedFluidDiskStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(-1);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalExternalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getMaxItemExternalStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalExternalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(RSApi.getMaxFluidExternalStorage(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(-1);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getFluids(IArguments arguments) {
        if (!isAvailable())
            return notConnected(null);

        return MethodResult.of(RSApi.listFluids(getNetwork()));
    }

    @Override
    public MethodResult getChemicals(IArguments arguments) {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyUsage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(getNetwork().getEnergyUsage());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyCapacity() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(getNetwork().getEnergyStorage().getMaxEnergyStored());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getStoredEnergy() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(getNetwork().getEnergyStorage().getEnergyStored());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAverageEnergyInput() {
        return MethodResult.of(0);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getPatterns(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        // Expected input is a table with either an input table, an output table or both to filter for both
        Map<?, ?> filterTable;
        Optional<Map<?, ?>> optionalTable = arguments.optTable(0);
        if (optionalTable.isEmpty())
            return MethodResult.of(RSApi.getPatterns(getNetwork()));

        filterTable = optionalTable.get();

        boolean hasInputFilter = filterTable.containsKey("input");
        boolean hasOutputFilter = filterTable.containsKey("output");
        boolean hasAnyFilter = hasInputFilter || hasOutputFilter;

        // If the player tries to filter for nothing, return nothing.
        if (!hasAnyFilter)
            return MethodResult.of(null, StatusConstants.MISSING_FILTER.toString());

        GenericFilter<?> inputFilter = null;
        GenericFilter<?> outputFilter = null;

        if (hasInputFilter) {
            Map<?, ?> inputFilterTable = TableHelper.getTableField(filterTable, "input");

            inputFilter = GenericFilter.parseGeneric(inputFilterTable).getLeft();
        }
        if (hasOutputFilter) {
            Map<?, ?> outputFilterTable = TableHelper.getTableField(filterTable, "output");

            outputFilter = GenericFilter.parseGeneric(outputFilterTable).getLeft();
        }

        Pair<ICraftingPattern, String> pattern = RSApi.findPatternFromFilters(getNetwork(), inputFilter, outputFilter);

        if (pattern.getRight() != null)
            return MethodResult.of(null, pattern.getRight());

        return MethodResult.of(RSApi.parsePattern(pattern.getLeft(), getNetwork()));
    }

    protected MethodResult exportToChest(@NotNull IArguments arguments, @Nullable IItemHandler targetInventory) throws LuaException {
        RsItemHandler itemHandler = new RsItemHandler(getNetwork());
        if (targetInventory == null)
            return MethodResult.of(0, StatusConstants.TARGET_NOT_FOUND);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(itemHandler, targetInventory, filter.getLeft()), null);
    }

    protected MethodResult importToSystem(@NotNull IArguments arguments, @Nullable IItemHandler targetInventory) throws LuaException {
        RsItemHandler itemHandler = new RsItemHandler(getNetwork());
        if (targetInventory == null)
            return MethodResult.of(0, StatusConstants.TARGET_NOT_FOUND);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(targetInventory, itemHandler, filter.getLeft()), null);
    }

    protected MethodResult exportToTank(@NotNull IArguments arguments, @Nullable IFluidHandler targetInventory) throws LuaException {
        RsFluidHandler itemHandler = new RsFluidHandler(getNetwork());
        if (targetInventory == null)
            return MethodResult.of(0, StatusConstants.TARGET_NOT_FOUND);

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveFluid(itemHandler, targetInventory, filter.getLeft()), null);
    }

    protected MethodResult importToSystem(@NotNull IArguments arguments, @Nullable IFluidHandler targetInventory) throws LuaException {
        RsFluidHandler itemHandler = new RsFluidHandler(getNetwork());
        if (targetInventory == null)
            return MethodResult.of(0, StatusConstants.TARGET_NOT_FOUND);

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveFluid(targetInventory, itemHandler, filter.getLeft()), null);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IItemHandler inventory;

        if (Direction.byName(side.toUpperCase(Locale.ROOT)) == null && ComputerSide.valueOfInsensitive(side.toUpperCase(Locale.ROOT)) == null) {
            inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        } else {
            inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        }

        return exportToChest(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IItemHandler inventory;

        if (Direction.byName(side.toUpperCase(Locale.ROOT)) == null && ComputerSide.valueOfInsensitive(side.toUpperCase(Locale.ROOT)) == null) {
            inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);
        } else {
            inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));
        }

        return importToSystem(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IFluidHandler fluidHandler;

        if (Direction.byName(side.toUpperCase(Locale.ROOT)) == null && ComputerSide.valueOfInsensitive(side.toUpperCase(Locale.ROOT)) == null) {
            fluidHandler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        } else {
            fluidHandler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        }

        return exportToTank(arguments, fluidHandler);
    }

    @Override
    public MethodResult importChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    public MethodResult exportChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IFluidHandler fluidHandler;

        if (Direction.byName(side.toUpperCase(Locale.ROOT)) == null && ComputerSide.valueOfInsensitive(side.toUpperCase(Locale.ROOT)) == null) {
            fluidHandler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        } else {
            fluidHandler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));
        }

        return importToSystem(arguments, fluidHandler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        return MethodResult.of(RSApi.parseItemStack(RSApi.findStackFromFilter(getNetwork(), getNetwork().getCraftingManager(), filter.getLeft()), getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getFluid(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        return MethodResult.of(RSApi.parseFluidStack(RSApi.findFluidFromFilter(getNetwork(), getNetwork().getCraftingManager(), filter.getLeft()), getNetwork()));
    }

    @Override
    public MethodResult getChemical(IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult craftItem(IComputerAccess computerAccess, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemStack stack = RSApi.findStackFromFilter(getNetwork(), getNetwork().getCraftingManager(), filter.getLeft());
        if (stack.isEmpty())
            return MethodResult.of(null, StatusConstants.NOT_CRAFTABLE.toString());

        ICalculationResult result = getNetwork().getCraftingManager().create(stack, filter.getLeft().getCount());
        CalculationResultType type = result.getType();
        if (type == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        AdvancedPeripherals.debug("Crafting Result of '" + ItemUtil.getRegistryKey(stack).toString() + "':" + type);
        return MethodResult.of(type == CalculationResultType.OK);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult craftFluid(IComputerAccess computerAccess, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidStack stack = RSApi.findFluidFromFilter(getNetwork(), getNetwork().getCraftingManager(), filter.getLeft());
        if (stack.isEmpty())
            return MethodResult.of(null, StatusConstants.NOT_CRAFTABLE.toString());

        ICalculationResult result = getNetwork().getCraftingManager().create(stack, filter.getLeft().getCount());
        CalculationResultType type = result.getType();
        if (type == CalculationResultType.OK)
            getNetwork().getCraftingManager().start(result.getTask());
        AdvancedPeripherals.debug("Crafting Result of '" + FluidUtil.getRegistryKey(stack).toString() + "':" + type);
        return MethodResult.of(type == CalculationResultType.OK);
    }

    @Override
    public MethodResult craftChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        return null;
    }

    @Override
    public MethodResult getCraftingTask(int id) {
        return null;
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getCraftingTasks() {
        if (!isAvailable())
            return notConnected(null);

        return MethodResult.of(RSApi.getCraftingTasks(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult cancelCraftingTasks(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(arguments.getTable(0));
        if (filter.getRight() != null)
            return MethodResult.of(null, filter.getRight());

        ICraftingManager craftingManager = getNetwork().getCraftingManager();
        int canceled = 0;

        for (ICraftingTask task : craftingManager.getTasks()) {
            if (filter.getLeft() instanceof ItemFilter itemFilter) {
                if (itemFilter.test(task.getRequested().getItem())) {
                    craftingManager.cancel(task.getId());
                    canceled++;
                }
            }

            if (filter.getLeft() instanceof FluidFilter fluidFilter) {
                if (fluidFilter.test(task.getRequested().getFluid())) {
                    craftingManager.cancel(task.getId());
                    canceled++;
                }
            }
        }

        return MethodResult.of(canceled);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isCrafting(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(false);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemStack stack = RSApi.findStackFromFilter(getNetwork(), getNetwork().getCraftingManager(), filter.getLeft());
        if (stack.isEmpty())
            return MethodResult.of(null, "NOT_CRAFTABLE");

        for (ICraftingTask task : getNetwork().getCraftingManager().getTasks()) {
            ItemStack taskStack = task.getRequested().getItem();
            if (taskStack != null && taskStack.sameItem(stack))
                return MethodResult.of(true);
        }
        return MethodResult.of(false);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isCraftable(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(false);

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(false, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, "EMPTY_FILTER");

        return MethodResult.of(RSApi.isItemCraftable(getNetwork(), parsedFilter.toItemStack()));
    }
}
