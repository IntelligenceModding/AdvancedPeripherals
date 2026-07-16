package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectLuaTable;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IStorageSystemPeripheralOwner;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import mekanism.api.chemical.IChemicalHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 * Implementation for common storage peripheral functions.
 * Used for AE2 {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MEBridgePeripheral}
 * and RS {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RSBridgePeripheral}
 * <p>
 * This ensures that these both bridges use the same methods. This makes it easier to support both in the same script
 * In case there is a new mod which adds new ways to store and craft items, this ensures that the new peripheral
 * has the same functions as the other ones
 *
 * @param <O> peripheral owner type
 */
public abstract class AbstractStorageSystemPeripheral<O extends IStorageSystemPeripheralOwner> extends BasePeripheral<O> {
    static final MethodResult NOT_CONNECTED_RESULT = MethodResult.of(null, StatusConstants.NOT_CONNECTED.toString());

    protected AbstractStorageSystemPeripheral(String type, @NotNull O owner) {
        super(type, owner);
    }

    @Override
    public Set<String> getAdditionalTypes() {
        return Set.of("storage_bridge");
    }

    @Nullable
    public abstract APAddon getChemicalOpAddon();

    public MethodResult checkChemicalOperation() {
        APAddon chemAddon = this.getChemicalOpAddon();
        if (chemAddon == null) {
            return MethodResult.of(null, "UNSUPPORTED_OPERATION");
        }
        if (!chemAddon.isLoaded()) {
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.toString(), chemAddon.getModId());
        }
        return null;
    }

    @NotNull
    public IItemHandler getStorageSystemItemHandler() {
        return this.owner;
    }

    @NotNull
    public IFluidHandler getStorageSystemFluidHandler() {
        return this.owner;
    }

    @NotNull
    public abstract Object /*IChemicalHandler*/ getStorageSystemChemicalHandler();

    public abstract boolean isAvailable();

    @LuaFunction(mainThread = true)
    public final boolean isConnected() {
        return this.isAvailable();
    }

    public abstract boolean isOnlineImpl();

    @LuaFunction(mainThread = true)
    public final boolean isOnline() {
        if (!this.isAvailable()) {
            return false;
        }
        return this.isOnlineImpl();
    }

    public abstract MethodResult getItemImpl(ItemFilter filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getItem(Map<?, ?> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<ItemFilter, String> parsedFilter = ItemFilter.parse(new ObjectLuaTable(filter));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }
        if (parsedFilter.left().isEmpty()) {
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());
        }

        return this.getItemImpl(parsedFilter.left());
    }

    public abstract MethodResult getFluidImpl(FluidFilter filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getFluid(Map<?, ?> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<FluidFilter, String> parsedFilter = FluidFilter.parse(new ObjectLuaTable(filter));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }
        if (parsedFilter.left().isEmpty()) {
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());
        }

        return this.getFluidImpl(parsedFilter.left());
    }

    public abstract MethodResult getChemicalImpl(Object /*ChemicalFilter*/ filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getChemical(Map<?, ?> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        MethodResult chemCheck = this.checkChemicalOperation();
        if (chemCheck != null) {
            return chemCheck;
        }
        Pair<ChemicalFilter, String> parsedFilter = ChemicalFilter.parse(new ObjectLuaTable(filter));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }
        if (parsedFilter.left().isEmpty()) {
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());
        }

        return this.getChemicalImpl(parsedFilter.left());
    }

    public abstract MethodResult getItemsImpl(ItemFilter filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getItems(Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<ItemFilter, String> parsedFilter = ItemFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return this.getItemsImpl(parsedFilter.left());
    }

    public abstract MethodResult getFluidsImpl(FluidFilter filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getFluids(Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<FluidFilter, String> parsedFilter = FluidFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return this.getFluidsImpl(parsedFilter.left());
    }

    public abstract MethodResult getChemicalsImpl(Object /*ChemicalFilter*/ filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getChemicals(Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        MethodResult chemCheck = this.checkChemicalOperation();
        if (chemCheck != null) {
            return chemCheck;
        }
        Pair<ChemicalFilter, String> parsedFilter = ChemicalFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return this.getChemicalsImpl(parsedFilter.left());
    }

    public abstract MethodResult getCraftableItemsImpl(ItemFilter filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftableItems(Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<ItemFilter, String> parsedFilter = ItemFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return this.getCraftableItemsImpl(parsedFilter.left());
    }

    public abstract MethodResult getCraftableFluidsImpl(FluidFilter filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftableFluids(Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<FluidFilter, String> parsedFilter = FluidFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return this.getCraftableFluidsImpl(parsedFilter.left());
    }

    public abstract MethodResult getCraftableChemicalsImpl(Object /*ChemicalFilter*/ filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftableChemicals(Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        MethodResult chemCheck = this.checkChemicalOperation();
        if (chemCheck != null) {
            return chemCheck;
        }
        Pair<ChemicalFilter, String> parsedFilter = ChemicalFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return this.getCraftableChemicalsImpl(parsedFilter.left());
    }

    public abstract List<?> getCellsImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getCells() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getCellsImpl());
    }

    public abstract List<?> getDrivesImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getDrives() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getDrivesImpl());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IComputerAccess computer, String fromName, Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        IItemHandler toInventory = this.getStorageSystemItemHandler();
        IItemHandler fromInventory = this.getItemHandlerOrNull(computer, fromName);
        if (fromInventory == null) {
            return MethodResult.of(null, StatusConstants.INVENTORY_NOT_FOUND.name());
        }

        Pair<ItemFilter, String> parsedFilter = ItemFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return MethodResult.of(ItemUtil.moveItem(fromInventory, toInventory, parsedFilter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(IComputerAccess computer, String toName, Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        IItemHandler fromInventory = this.getStorageSystemItemHandler();
        IItemHandler toInventory = this.getItemHandlerOrNull(computer, toName);
        if (toInventory == null) {
            return MethodResult.of(null, StatusConstants.INVENTORY_NOT_FOUND.name());
        }

        Pair<ItemFilter, String> parsedFilter = ItemFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return MethodResult.of(ItemUtil.moveItem(fromInventory, toInventory, parsedFilter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importFluid(IComputerAccess computer, String fromName, Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        IFluidHandler toInventory = this.getStorageSystemFluidHandler();
        IFluidHandler fromInventory = this.getFluidHandlerOrNull(computer, fromName);
        if (fromInventory == null) {
            return MethodResult.of(null, StatusConstants.INVENTORY_NOT_FOUND.name());
        }

        Pair<FluidFilter, String> parsedFilter = FluidFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return MethodResult.of(FluidUtil.moveFluid(fromInventory, toInventory, parsedFilter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportFluid(IComputerAccess computer, String toName, Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        IFluidHandler fromInventory = this.getStorageSystemFluidHandler();
        IFluidHandler toInventory = this.getFluidHandlerOrNull(computer, toName);
        if (toInventory == null) {
            return MethodResult.of(null, StatusConstants.INVENTORY_NOT_FOUND.name());
        }

        Pair<FluidFilter, String> parsedFilter = FluidFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return MethodResult.of(FluidUtil.moveFluid(fromInventory, toInventory, parsedFilter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importChemical(IComputerAccess computer, String fromName, Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        MethodResult chemCheck = this.checkChemicalOperation();
        if (chemCheck != null) {
            return chemCheck;
        }

        IChemicalHandler toInventory = (IChemicalHandler) this.getStorageSystemChemicalHandler();
        IChemicalHandler fromInventory = (IChemicalHandler) this.getChemicalHandlerOrNull(computer, fromName);
        if (fromInventory == null) {
            return MethodResult.of(null, StatusConstants.INVENTORY_NOT_FOUND.name());
        }

        Pair<ChemicalFilter, String> parsedFilter = ChemicalFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return MethodResult.of(ChemicalUtil.moveChemical(fromInventory, toInventory, parsedFilter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportChemical(IComputerAccess computer, String toName, Optional<Map<?, ?>> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        MethodResult chemCheck = this.checkChemicalOperation();
        if (chemCheck != null) {
            return chemCheck;
        }

        IChemicalHandler fromInventory = (IChemicalHandler) this.getStorageSystemChemicalHandler();
        IChemicalHandler toInventory = (IChemicalHandler) this.getChemicalHandlerOrNull(computer, toName);
        if (toInventory == null) {
            return MethodResult.of(null, StatusConstants.INVENTORY_NOT_FOUND.name());
        }

        Pair<ChemicalFilter, String> parsedFilter = ChemicalFilter.parse(EmptyLuaTable.orEmpty(filter.orElse(null)));
        if (parsedFilter.rightPresent()) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return MethodResult.of(ChemicalUtil.moveChemical(fromInventory, toInventory, parsedFilter.left()));
    }

    public abstract double getStoredEnergyImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getStoredEnergy() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getStoredEnergyImpl());
    }

    public abstract double getEnergyCapacityImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyCapacity() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getEnergyCapacityImpl());
    }

    public abstract double getEnergyUsageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyUsage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getEnergyUsageImpl());
    }

    public abstract double getAverageEnergyInputImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getAverageEnergyInput() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getAverageEnergyInputImpl());
    }

    public abstract double getMaxExternalItemStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxExternalItemStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getMaxExternalItemStorageImpl());
    }

    public abstract double getMaxExternalItemCountImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxExternalItemCount() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getMaxExternalItemCountImpl());
    }

    public abstract double getMaxExternalFluidStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxExternalFluidStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getMaxExternalFluidStorageImpl());
    }

    public abstract double getMaxExternalChemicalStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxExternalChemicalStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getMaxExternalChemicalStorageImpl());
    }

    public abstract double getMaxItemStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxItemStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getMaxItemStorageImpl());
    }

    public abstract double getMaxFluidStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxFluidStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getMaxFluidStorageImpl());
    }

    public abstract double getMaxChemicalStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getMaxChemicalStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getMaxChemicalStorageImpl());
    }

    public abstract double getUsedExternalItemStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getUsedExternalItemStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getUsedExternalItemStorageImpl());
    }

    public abstract double getUsedExternalItemCountImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getUsedExternalItemCount() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getUsedExternalItemCountImpl());
    }

    public abstract double getUsedExternalFluidStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getUsedExternalFluidStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getUsedExternalFluidStorageImpl());
    }

    public abstract double getUsedExternalChemicalStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getUsedExternalChemicalStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getUsedExternalChemicalStorageImpl());
    }

    public abstract double getUsedItemStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getUsedItemStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getUsedItemStorageImpl());
    }

    public abstract double getUsedFluidStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getUsedFluidStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getUsedFluidStorageImpl());
    }

    public abstract double getUsedChemicalStorageImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getUsedChemicalStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getUsedChemicalStorageImpl());
    }

    public double getAvailableExternalItemStorageImpl() {
        return this.getMaxExternalItemStorageImpl() - this.getUsedExternalItemStorageImpl();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableExternalItemStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getAvailableExternalItemStorageImpl());
    }

    public double getAvailableExternalItemCountImpl() {
        return this.getMaxExternalItemCountImpl() - this.getUsedExternalItemCountImpl();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableExternalItemCount() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getAvailableExternalItemCountImpl());
    }

    public double getAvailableExternalFluidStorageImpl() {
        return this.getMaxExternalFluidStorageImpl() - this.getUsedExternalFluidStorageImpl();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableExternalFluidStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getAvailableExternalFluidStorageImpl());
    }

    public double getAvailableExternalChemicalStorageImpl() {
        return this.getMaxExternalChemicalStorageImpl() - this.getUsedExternalChemicalStorageImpl();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableExternalChemicalStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getAvailableExternalChemicalStorageImpl());
    }

    public double getAvailableItemStorageImpl() {
        return this.getMaxItemStorageImpl() - this.getUsedItemStorageImpl();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableItemStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getAvailableItemStorageImpl());
    }

    public double getAvailableFluidStorageImpl() {
        return this.getMaxFluidStorageImpl() - this.getUsedFluidStorageImpl();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableFluidStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getAvailableFluidStorageImpl());
    }

    public double getAvailableChemicalStorageImpl() {
        return this.getMaxChemicalStorageImpl() - this.getUsedChemicalStorageImpl();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableChemicalStorage() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getAvailableChemicalStorageImpl());
    }

    public abstract List<?> getCraftingTasksImpl();

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingTasks() {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getCraftingTasksImpl());
    }

    public abstract Object getCraftingTaskImpl(int id);

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingTask(int id) {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        return MethodResult.of(this.getCraftingTaskImpl(id));
    }

    public abstract int cancelCraftingTasksImpl(GenericFilter<?> filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult cancelCraftingTasks(Map<?, ?> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<? extends GenericFilter<?>, String> parsedFilter = GenericFilter.parseGeneric(new ObjectLuaTable(filter));
        if (parsedFilter.right() != null) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return MethodResult.of(this.cancelCraftingTasksImpl(parsedFilter.left()));
    }

    public abstract MethodResult craftItemImpl(IComputerAccess computer, IArguments arguments, ItemFilter filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<ItemFilter, String> parsedFilter = ItemFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
        if (parsedFilter.right() != null) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return this.craftItemImpl(computer, arguments, parsedFilter.left());
    }

    public abstract MethodResult craftFluidImpl(IComputerAccess computer, IArguments arguments, FluidFilter filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }

        Pair<FluidFilter, String> parsedFilter = FluidFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
        if (parsedFilter.right() != null) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return this.craftFluidImpl(computer, arguments, parsedFilter.left());
    }

    public abstract MethodResult craftChemicalImpl(IComputerAccess computer, IArguments arguments, Object /*ChemicalFilter*/ filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult craftChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        MethodResult chemCheck = this.checkChemicalOperation();
        if (chemCheck != null) {
            return chemCheck;
        }

        Pair<ChemicalFilter, String> parsedFilter = ChemicalFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
        if (parsedFilter.right() != null) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return this.craftChemicalImpl(computer, arguments, parsedFilter.left());
    }

    public abstract boolean isCraftableImpl(GenericFilter<?> filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult isCraftable(Map<?, ?> filter) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<? extends GenericFilter<?>, String> parsedFilter = GenericFilter.parseGeneric(new ObjectLuaTable(filter));
        if (parsedFilter.right() != null) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return MethodResult.of(this.isCraftableImpl(parsedFilter.left()));
    }

    public abstract MethodResult isCraftingImpl(IArguments arguments, GenericFilter<?> filter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult isCrafting(IArguments arguments) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Pair<? extends GenericFilter<?>, String> parsedFilter = GenericFilter.parseGeneric(new ObjectLuaTable(arguments.getTable(0)));
        if (parsedFilter.right() != null) {
            return MethodResult.of(null, parsedFilter.right());
        }

        return MethodResult.of(this.isCraftingImpl(arguments, parsedFilter.left()));
    }

    public abstract MethodResult getPatternsImpl(@Nullable GenericFilter<?> inputFilter, @Nullable GenericFilter<?> outputFilter) throws LuaException;

    @LuaFunction(mainThread = true)
    public final MethodResult getPatterns(Optional<Map<?, ?>> optFilters) throws LuaException {
        if (!this.isAvailable()) {
            return NOT_CONNECTED_RESULT;
        }
        Map<?, ?> filters = optFilters.orElse(null);
        if (filters == null) {
            return this.getPatternsImpl(null, null);
        }

        LuaTable<?, ?> filterTable = EmptyLuaTable.orEmpty(filters);
        boolean hasInputFilter = filterTable.containsKey("input");
        boolean hasOutputFilter = filterTable.containsKey("output");
        boolean hasAnyFilter = hasInputFilter || hasOutputFilter;

        // If the player tries to filter for nothing, return nothing.
        if (!hasAnyFilter) {
            return MethodResult.of(null, "NO_FILTER");
        }

        GenericFilter<?> inputFilter = null;
        GenericFilter<?> outputFilter = null;

        if (hasInputFilter) {
            Pair<? extends GenericFilter<?>, String> parsedFilter = GenericFilter.parseGeneric(new ObjectLuaTable(filterTable.getTable("input")));
            if (parsedFilter.rightPresent()) {
                return MethodResult.of(null, parsedFilter.right());
            }
            inputFilter = parsedFilter.left();
        }
        if (hasOutputFilter) {
            Pair<? extends GenericFilter<?>, String> parsedFilter = GenericFilter.parseGeneric(new ObjectLuaTable(filterTable.getTable("output")));
            if (parsedFilter.rightPresent()) {
                return MethodResult.of(null, parsedFilter.right());
            }
            outputFilter = parsedFilter.left();
        }

        return this.getPatternsImpl(inputFilter, outputFilter);
    }
}
