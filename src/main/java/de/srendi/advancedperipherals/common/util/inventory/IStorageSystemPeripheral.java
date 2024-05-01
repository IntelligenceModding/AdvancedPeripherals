/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;

/**
 * Implementation for common storage peripheral functions. Used for AE2
 * {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral}
 * and RS
 * {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral}
 * <p>
 * This ensures that these both bridges use the same methods. This makes it
 * easier to support both in the same script In case there is a new mod which
 * adds new ways to store and craft items, this ensures that the new peripheral
 * has the same functions as the other ones
 * <p>
 * Implementation needs to override
 * {@link dan200.computercraft.api.lua.LuaFunction}
 */
public interface IStorageSystemPeripheral {

    MethodResult isConnected();

    MethodResult isOnline();

    MethodResult getItem(IArguments arguments) throws LuaException;

    MethodResult getFluid(IArguments arguments) throws LuaException;

    MethodResult listItems();

    MethodResult listFluids();

    MethodResult listCraftableItems();

    MethodResult listCraftableFluids();

    MethodResult listCells();

    MethodResult listDrives();

    MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult exportItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult getFilteredPatterns(IArguments arguments) throws LuaException;

    MethodResult getPatterns();

    MethodResult getStoredEnergy();

    MethodResult getEnergyCapacity();

    MethodResult getEnergyUsage();

    MethodResult getAvgPowerInjection();

    MethodResult getTotalExternItemStorage();

    MethodResult getTotalExternFluidStorage();

    MethodResult getTotalItemStorage();

    MethodResult getTotalFluidStorage();

    MethodResult getUsedExternItemStorage();

    MethodResult getUsedExternFluidStorage();

    MethodResult getUsedItemStorage();

    MethodResult getUsedFluidStorage();

    MethodResult getAvailableExternItemStorage();

    MethodResult getAvailableExternFluidStorage();

    MethodResult getAvailableItemStorage();

    MethodResult getAvailableFluidStorage();

    MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult getCraftingTasks();

    MethodResult cancelCraftingTasks(IArguments arguments) throws LuaException;

    MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult isItemCraftable(IArguments arguments) throws LuaException;

    MethodResult isItemCrafting(IArguments arguments) throws LuaException;

    MethodResult isFluidCraftable(IArguments arguments) throws LuaException;

    MethodResult isFluidCrafting(IArguments arguments) throws LuaException;

}
