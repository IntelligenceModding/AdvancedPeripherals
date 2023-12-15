package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;

/**
 * Implementation for common storage peripheral functions. Used for AE2 {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral}
 * and RS {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral}
 * <p>
 * This ensures that these both bridges use the same methods. This makes it easier to support both in the same script
 * In case there is a new mod which adds new ways to store and craft items, this ensures that the new peripheral
 * has the same functions as the other ones
 */
public interface IStorageSystemPeripheral {

    @LuaFunction(mainThread = true)
    MethodResult isConnected();

    @LuaFunction(mainThread = true)
    MethodResult isOnline();

    @LuaFunction(mainThread = true)
    MethodResult getItem(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult getFluid(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult listItems();

    @LuaFunction(mainThread = true)
    MethodResult listFluids();

    @LuaFunction(mainThread = true)
    MethodResult listCraftableItems();

    @LuaFunction(mainThread = true)
    MethodResult listCraftableFluids();

    @LuaFunction
    MethodResult listCells();

    @LuaFunction(mainThread = true)
    MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult exportItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult getFilteredPatterns(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult getPatterns();

    @LuaFunction(mainThread = true)
    MethodResult getStoredEnergy();

    @LuaFunction(mainThread = true)
    MethodResult getEnergyCapacity();

    @LuaFunction(mainThread = true)
    MethodResult getEnergyUsage();

    @LuaFunction(mainThread = true)
    MethodResult getTotalExternItemStorage();

    @LuaFunction(mainThread = true)
    MethodResult getTotalExternFluidStorage();

    @LuaFunction(mainThread = true)
    MethodResult getTotalItemStorage();

    @LuaFunction(mainThread = true)
    MethodResult getTotalFluidStorage();

    @LuaFunction(mainThread = true)
    MethodResult getUsedExternItemStorage();

    @LuaFunction(mainThread = true)
    MethodResult getUsedExternFluidStorage();

    @LuaFunction(mainThread = true)
    MethodResult getUsedItemStorage();

    @LuaFunction(mainThread = true)
    MethodResult getUsedFluidStorage();

    @LuaFunction(mainThread = true)
    MethodResult getAvailableExternItemStorage();

    @LuaFunction(mainThread = true)
    MethodResult getAvailableExternFluidStorage();

    @LuaFunction(mainThread = true)
    MethodResult getAvailableItemStorage();

    @LuaFunction(mainThread = true)
    MethodResult getAvailableFluidStorage();

    @LuaFunction(mainThread = true)
    MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult isItemCraftable(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult isItemCrafting(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult isFluidCraftable(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult isFluidCrafting(IArguments arguments) throws LuaException;

}
