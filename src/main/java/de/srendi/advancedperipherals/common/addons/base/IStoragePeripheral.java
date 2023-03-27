package de.srendi.advancedperipherals.common.addons.base;

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
public interface IStoragePeripheral {

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

    @LuaFunction(mainThread = true)
    MethodResult importItem(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult exportItem(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult importItemFromPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult exportItemToPeripheral(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult getPattern(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult getPatterns();

    @LuaFunction(mainThread = true)
    MethodResult getStoredEnergy();

    @LuaFunction(mainThread = true)
    MethodResult getEnergyCapacity();

    @LuaFunction(mainThread = true)
    MethodResult getEnergyUsage();

    @LuaFunction(mainThread = true)
    MethodResult getMaxItemExternalStorage();

    @LuaFunction(mainThread = true)
    MethodResult getMaxFluidExternalStorage();

    @LuaFunction(mainThread = true)
    MethodResult getMaxItemDiskStorage();

    @LuaFunction(mainThread = true)
    MethodResult getMaxFluidDiskStorage();

    @LuaFunction(mainThread = true)
    MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult isItemCraftable(IArguments arguments) throws LuaException;

    @LuaFunction(mainThread = true)
    MethodResult isItemCrafting(IArguments arguments) throws LuaException;

}
