package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.NetworkComponent;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.energy.EnergyNetworkComponent;
import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.storage.StorageTypes;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSCraftJob;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RsApi;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RsChemicalHandler;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RsFluidHandler;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RsItemHandler;
import de.srendi.advancedperipherals.common.blocks.blockentities.RsBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import mekanism.api.chemical.IChemicalHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RSBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RsBridgeEntity>> implements IStorageSystemPeripheral {

    public static final String PERIPHERAL_TYPE = "rsBridge";

    private final RsBridgeEntity bridge;

    public RSBridgePeripheral(RsBridgeEntity owner) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(owner));
        this.bridge = owner;
    }

    @Override
    public boolean isEnabled() {
        return APAddons.refinedStorageLoaded && APConfig.PERIPHERALS_CONFIG.enableRSBridge.get();
    }

    private AbstractNetworkNode getNode() {
        return (AbstractNetworkNode) owner.tileEntity.getNode();
    }

    private Network getNetwork() {
        return getNode().getNetwork();
    }

    private MethodResult notConnected() {
        return MethodResult.of(null, "NOT_CONNECTED");
    }

    private <I extends NetworkComponent> I getComponent(@NotNull Class<I> componentClass) {
        return getNetwork().getComponent(componentClass);
    }

    private boolean isAvailable() {
        return true;
    }

    /**
     * exports an item out of the system to a valid inventory
     *
     * @param arguments       the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToChest(@NotNull IArguments arguments, @Nullable IItemHandler targetInventory) throws LuaException {
        RsItemHandler itemHandler = new RsItemHandler(getNetwork());
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetInventory == null)
            return MethodResult.of(0, "Target Inventory does not exist");

        return MethodResult.of(InventoryUtil.moveItem(itemHandler, targetInventory, filter.getLeft()), null);
    }

    /**
     * exports a fluid out of the system to a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToTank(@NotNull IArguments arguments, @Nullable IFluidHandler targetTank) throws LuaException {
        RsFluidHandler fluidHandler = new RsFluidHandler(getNetwork());
        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetTank == null)
            return MethodResult.of(0, "Target Tank does not exist");

        return MethodResult.of(FluidUtil.moveFluid(fluidHandler, targetTank, filter.getLeft()), null);
    }

    /**
     * exports a fluid out of the system to a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToTank(@NotNull IArguments arguments, @Nullable IChemicalHandler targetTank) throws LuaException {
        RsChemicalHandler chemicalHandler = new RsChemicalHandler(getNetwork());
        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetTank == null)
            return MethodResult.of(0, "Target Tank does not exist");

        return MethodResult.of(ChemicalUtil.moveChemical(chemicalHandler, targetTank, filter.getLeft()), null);
    }

    /**
     * imports an item to the system from a valid inventory
     *
     * @param arguments       the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToRS(@NotNull IArguments arguments, @Nullable IItemHandler targetInventory) throws LuaException {
        RsItemHandler itemHandler = new RsItemHandler(getNetwork());
        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetInventory == null)
            return MethodResult.of(0, "Target Inventory does not exist");

        return MethodResult.of(InventoryUtil.moveItem(targetInventory, itemHandler, filter.getLeft()), null);
    }

    /**
     * imports a fluid to the system from a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToRS(@NotNull IArguments arguments, @Nullable IFluidHandler targetTank) throws LuaException {
        RsFluidHandler fluidHandler = new RsFluidHandler(getNetwork());
        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetTank == null)
            return MethodResult.of(0, "Target Tank does not exist");

        return MethodResult.of(FluidUtil.moveFluid(targetTank, fluidHandler, filter.getLeft()), null);
    }

    /**
     * imports a fluid to the system from a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToRS(@NotNull IArguments arguments, @Nullable IChemicalHandler targetTank) throws LuaException {
        RsChemicalHandler chemicalHandler = new RsChemicalHandler(getNetwork());
        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        if (targetTank == null)
            return MethodResult.of(0, "Target Tank does not exist");

        return MethodResult.of(ChemicalUtil.moveChemical(targetTank, chemicalHandler, filter.getLeft()), null);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isConnected() {
        return MethodResult.of(isAvailable());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isOnline() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(getComponent(EnergyNetworkComponent.class).getStored() > 0);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, "EMPTY_FILTER");

        Map<?, ?> resourceProperties = RsApi.getParsedItem(getNetwork(), parsedFilter);
        if (resourceProperties == null)
            return MethodResult.of(null, "NOT_FOUND");

        return MethodResult.of(resourceProperties);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getFluid(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, "EMPTY_FILTER");

        Map<?, ?> resourceProperties = RsApi.getParsedFluid(getNetwork(), parsedFilter);
        if (resourceProperties == null)
            return MethodResult.of(null, "NOT_FOUND");

        return MethodResult.of(resourceProperties);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getChemical(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, "EMPTY_FILTER");

        Map<?, ?> resourceProperties = RsApi.getParsedChemical(getNetwork(), parsedFilter);
        if (resourceProperties == null)
            return MethodResult.of(null, "NOT_FOUND");

        return MethodResult.of(resourceProperties);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listItems(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.optTable(0, Map.of()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();

        Set<Map<String, Object>> resourceProperties = RsApi.getParsedItems(getNetwork(), parsedFilter);

        return MethodResult.of(resourceProperties);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listFluids(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.optTable(0, Map.of()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();

        Set<Map<String, Object>> resourceProperties = RsApi.getParsedFluids(getNetwork(), parsedFilter);

        return MethodResult.of(resourceProperties);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listChemicals(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.optTable(0, Map.of()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();

        Set<Map<String, Object>> resourceProperties = RsApi.getParsedCnemicals(getNetwork(), parsedFilter);

        return MethodResult.of(resourceProperties);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listCraftableItems(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.optTable(0, Map.of()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();

        Set<Map<String, Object>> resourceProperties = RsApi.getCraftableItems(getNetwork(), parsedFilter);

        return MethodResult.of(resourceProperties);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listCraftableFluids(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.optTable(0, Map.of()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();

        Set<Map<String, Object>> resourceProperties = RsApi.getCraftableFluids(getNetwork(), parsedFilter);

        return MethodResult.of(resourceProperties);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listCraftableChemicals(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.optTable(0, Map.of()));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();

        Set<Map<String, Object>> resourceProperties = RsApi.getCraftableChemicals(getNetwork(), parsedFilter);

        return MethodResult.of(resourceProperties);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listCells() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.listCells(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult listDrives() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.listDrives(getNetwork()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);

        if (inventory == null)
            inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));

        if (inventory == null)
            return MethodResult.of(0, "The target inventory does not exist. Make sure the bridge is exposed in the computer network. Reach out to our discord or our documentation for help.");

        return importToRS(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(arguments.getString(1), owner);

        if (inventory == null)
            inventory = InventoryUtil.getHandlerFromName(computer, arguments.getString(1));

        if (inventory == null)
            return MethodResult.of(0, "The target inventory does not exist. Make sure the bridge is exposed in the computer network. Reach out to our discord or our documentation for help.");

        return exportToChest(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IFluidHandler handler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        if (handler == null)
            handler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));

        if (handler == null)
            return MethodResult.of(0, "The target tank does not exist. Make sure the bridge is exposed in the computer network. Reach out to our discord or our documentation for help.");

        return importToRS(arguments, handler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IFluidHandler handler = FluidUtil.getHandlerFromDirection(arguments.getString(1), owner);
        if (handler == null)
            handler = FluidUtil.getHandlerFromName(computer, arguments.getString(1));

        if (handler == null)
            return MethodResult.of(0, "The target tank does not exist. Make sure the bridge is exposed in the computer network. Reach out to our discord or our documentation for help.");

        return exportToTank(arguments, handler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IChemicalHandler handler = ChemicalUtil.getHandlerFromDirection(arguments.getString(1), owner);
        if (handler == null)
            handler = ChemicalUtil.getHandlerFromName(computer, arguments.getString(1));

        if (handler == null)
            return MethodResult.of(0, "The target tank does not exist. Make sure the bridge is exposed in the computer network. Reach out to our discord or our documentation for help.");

        return importToRS(arguments, handler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        IChemicalHandler handler = ChemicalUtil.getHandlerFromDirection(arguments.getString(1), owner);
        if (handler == null)
            handler = ChemicalUtil.getHandlerFromName(computer, arguments.getString(1));

        if (handler == null)
            return MethodResult.of(0, "The target tank does not exist. Make sure the bridge is exposed in the computer network. Reach out to our discord or our documentation for help.");

        return exportToTank(arguments, handler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getStoredEnergy() {
        if (!isAvailable())
            return notConnected();

        EnergyNetworkComponent energyComponent = getNetwork().getComponent(EnergyNetworkComponent.class);

        return MethodResult.of(energyComponent.getStored());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyCapacity() {
        if (!isAvailable())
            return notConnected();

        EnergyNetworkComponent energyComponent = getNetwork().getComponent(EnergyNetworkComponent.class);

        return MethodResult.of(energyComponent.getCapacity());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyUsage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(0);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvgPowerInjection() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(0);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalExternItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalExternStorage(getNetwork(), StorageTypes.ITEM));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalExternFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalExternStorage(getNetwork(), StorageTypes.FLUID));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalExternChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalExternStorage(getNetwork(), ChemicalResourceType.STORAGE_TYPE));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalStorage(getNetwork(), StorageTypes.ITEM));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalStorage(getNetwork(), StorageTypes.FLUID));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalStorage(getNetwork(), ChemicalResourceType.STORAGE_TYPE));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedExternItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getUsedExternStorage(getNetwork(), StorageTypes.ITEM));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedExternFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getUsedExternStorage(getNetwork(), StorageTypes.FLUID));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedExternChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getUsedExternStorage(getNetwork(), ChemicalResourceType.STORAGE_TYPE));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getUsedStorage(getNetwork(), StorageTypes.ITEM));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getUsedStorage(getNetwork(), StorageTypes.FLUID));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getUsedStorage(getNetwork(), ChemicalResourceType.STORAGE_TYPE));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableExternItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalExternStorage(getNetwork(), StorageTypes.ITEM) - RsApi.getUsedExternStorage(getNetwork(), StorageTypes.ITEM));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableExternFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalExternStorage(getNetwork(), StorageTypes.FLUID) - RsApi.getUsedExternStorage(getNetwork(), StorageTypes.FLUID));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableExternChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalExternStorage(getNetwork(), ChemicalResourceType.STORAGE_TYPE) - RsApi.getUsedExternStorage(getNetwork(), ChemicalResourceType.STORAGE_TYPE));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableItemStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalStorage(getNetwork(), StorageTypes.ITEM) - RsApi.getUsedStorage(getNetwork(), StorageTypes.ITEM));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableFluidStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalStorage(getNetwork(), StorageTypes.FLUID) - RsApi.getUsedStorage(getNetwork(), StorageTypes.FLUID));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableChemicalStorage() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getTotalStorage(getNetwork(), ChemicalResourceType.STORAGE_TYPE) - RsApi.getUsedStorage(getNetwork(), ChemicalResourceType.STORAGE_TYPE));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ItemFilter, String> filter = ItemFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemResource stack = RsApi.getItem(getNetwork(), filter.getLeft());
        if (stack == null)
            return MethodResult.of(null, "NOT_CRAFTABLE");

        RSCraftJob job = new RSCraftJob(computer, getLevel(), filter.getLeft().getCount(), stack, getNetwork().getComponent(AutocraftingNetworkComponent.class));
        bridge.addJob(job);
        return MethodResult.of(job);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<FluidFilter, String> filter = FluidFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidResource stack = RsApi.getFluid(getNetwork(), filter.getLeft());
        if (stack == null)
            return MethodResult.of(null, "NOT_CRAFTABLE");

        RSCraftJob job = new RSCraftJob(computer, getLevel(), filter.getLeft().getCount(), stack, getNetwork().getComponent(AutocraftingNetworkComponent.class));
        bridge.addJob(job);
        return MethodResult.of(job);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult craftChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalResource stack = RsApi.getChemical(getNetwork(), filter.getLeft());
        if (stack == null)
            return MethodResult.of(null, "NOT_CRAFTABLE");

        RSCraftJob job = new RSCraftJob(computer, getLevel(), filter.getLeft().getCount(), stack, getNetwork().getComponent(AutocraftingNetworkComponent.class));
        bridge.addJob(job);
        return MethodResult.of(job);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingTasks() {
        if (!isAvailable())
            return notConnected();

        return MethodResult.of(RsApi.getCraftingTasks(bridge));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingTask(int id) {
        if (!isAvailable())
            return notConnected();

        RSCraftJob foundJob = null;

        for (RSCraftJob job : bridge.getJobs()) {
            if (job.getId() == id) {
                foundJob = job;
            }
        }
        return MethodResult.of(foundJob);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult cancelCraftingTasks(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(arguments.getTable(0));
        if (filter.getRight() != null)
            return MethodResult.of(null, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();

        AutocraftingNetworkComponent craftingManager = getComponent(AutocraftingNetworkComponent.class);
        int canceled = 0;

        for (TaskStatus status : craftingManager.getStatuses()) {
            if (parsedFilter.testRS(new ResourceAmount(status.info().resource(), 1))) {
                craftingManager.cancel(status.info().id());
                canceled++;
            }
        }

        return MethodResult.of(canceled);
    }


    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isCraftable(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(arguments.getTable(0));
        if (filter.getRight() != null)
            return MethodResult.of(null, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();

        return MethodResult.of(RsApi.findPatternFromFilters(getNetwork(), null, parsedFilter).getLeft() != null);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isCrafting(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(arguments.getTable(0));
        if (filter.getRight() != null)
            return MethodResult.of(null, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();

        AutocraftingNetworkComponent craftingManager = getComponent(AutocraftingNetworkComponent.class);

        for (TaskStatus status : craftingManager.getStatuses()) {
            if (parsedFilter.testRS(new ResourceAmount(status.info().resource(), 1))) {
                return MethodResult.of(true);
            }
        }

        return MethodResult.of(false);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getPatterns(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected();

        // Expected input is a table with either an input table, an output table or both to filter for both
        // If no table is provided or it's empty, return every pattern
        Map<?, ?> filterTable = arguments.optTable(0, Collections.emptyMap());
        if (filterTable.isEmpty()) {
            return MethodResult.of(RsApi.getPatterns(getNetwork()));
        }

        boolean hasInputFilter = filterTable.containsKey("input");
        boolean hasOutputFilter = filterTable.containsKey("output");
        boolean hasAnyFilter = hasInputFilter || hasOutputFilter;

        // If the player tries to filter for nothing, return nothing.
        if (!hasAnyFilter)
            return MethodResult.of(null, "NO_FILTER");

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

        Pair<Pattern, String> pattern = RsApi.findPatternFromFilters(getNetwork(), inputFilter, outputFilter);

        if (pattern.getRight() != null)
            return MethodResult.of(null, pattern.getRight());

        return MethodResult.of(RsApi.parsePattern(pattern.getLeft()));
    }
}
