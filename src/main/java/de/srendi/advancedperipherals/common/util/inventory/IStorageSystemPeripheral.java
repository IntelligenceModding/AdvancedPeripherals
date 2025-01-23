package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.util.ServerWorker;

import java.util.function.Supplier;

/**
 * Implementation for common storage peripheral functions. Used for AE2 {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral}
 * and RS {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral}
 * <p>
 * This ensures that these both bridges use the same methods. This makes it easier to support both in the same script
 * In case there is a new mod which adds new ways to store and craft items, this ensures that the new peripheral
 * has the same functions as the other ones
 * <p>
 * Implementation needs to override {@link dan200.computercraft.api.lua.LuaFunction}
 */
public interface IStorageSystemPeripheral {

    MethodResult isConnected();

    MethodResult isOnline();

    MethodResult getItem(IArguments arguments) throws LuaException;

    MethodResult getFluid(IArguments arguments) throws LuaException;

    MethodResult getChemical(IArguments arguments) throws LuaException;

    MethodResult listItems();

    MethodResult listFluids();

    MethodResult listChemicals();

    MethodResult listCraftableItems();

    MethodResult listCraftableFluids();

    MethodResult listCraftableChemicals();

    MethodResult listCells();

    MethodResult listDrives();

    MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult exportItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult importChemical(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult exportchemical(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult getFilteredPatterns(IArguments arguments) throws LuaException;

    MethodResult getPatterns();

    MethodResult getStoredEnergy();

    MethodResult getEnergyCapacity();

    MethodResult getEnergyUsage();

    MethodResult getAvgPowerInjection();

    MethodResult getTotalExternItemStorage();

    MethodResult getTotalExternFluidStorage();

    MethodResult getTotalExternChemicalStorage();

    MethodResult getTotalItemStorage();

    MethodResult getTotalFluidStorage();

    MethodResult getTotalChemicalStorage();

    MethodResult getUsedExternItemStorage();

    MethodResult getUsedExternFluidStorage();

    MethodResult getUsedExternChemicalStorage();

    MethodResult getUsedItemStorage();

    MethodResult getUsedFluidStorage();

    MethodResult getUsedChemicalStorage();

    MethodResult getAvailableExternItemStorage();

    MethodResult getAvailableExternFluidStorage();

    MethodResult getAvailableExternChemicalStorage();

    MethodResult getAvailableItemStorage();

    MethodResult getAvailableFluidStorage();

    MethodResult getAvailableChemicalStorage();

    MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult getCraftingJobs();

    MethodResult cancelCraftingJobs(IArguments arguments) throws LuaException;

    MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult craftChemical(IComputerAccess computer, IArguments arguments) throws LuaException;

    MethodResult isItemCraftable(IArguments arguments) throws LuaException;

    MethodResult isItemCrafting(IArguments arguments) throws LuaException;

    MethodResult isFluidCraftable(IArguments arguments) throws LuaException;

    MethodResult isFluidCrafting(IArguments arguments) throws LuaException;

    MethodResult isChemicalCraftable(IArguments arguments) throws LuaException;

    MethodResult isChemicalCrafting(IArguments arguments) throws LuaException;

    class CraftJobCallback implements ILuaCallback {
        public static final String EVENT_ID = "bridge_craft_requested";
        private static volatile int idSeq = 0;

        public final MethodResult pull = MethodResult.pullEvent(EVENT_ID, this);
        private final int id;
        private final Supplier<MethodResult> worker;
        private volatile MethodResult result;

        public CraftJobCallback(IComputerAccess computer, Supplier<MethodResult> worker) {
            this.id = ++idSeq;
            this.worker = worker;
            ServerWorker.add(() -> {
                this.result = worker.get();
                computer.queueEvent(EVENT_ID, this.id);
            });
        }

        @Override
        public MethodResult resume(Object[] datas) {
            if (!(datas[0] instanceof String event) || !(datas[1] instanceof Number taskId)) {
                return this.pull;
            }
            if (!event.equals(EVENT_ID) || taskId.intValue() != this.id) {
                return this.pull;
            }
            return this.result;
        }
    }
}
