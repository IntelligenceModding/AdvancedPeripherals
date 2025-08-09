package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MEBridgePeripheral;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Implementation for common storage peripheral functions. Used for AE2 {@link MEBridgePeripheral}
 * and RS {@link de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral}
 * <p>
 * This ensures that these both bridges use the same methods. This makes it easier to support both in the same script
 * In case there is a new mod which adds new ways to store and craft items, this ensures that the new peripheral
 * has the same functions as the other ones
 * <p>
 * Implementation needs to override {@link dan200.computercraft.api.lua.LuaFunction}
 */
public interface IStorageSystemPeripheral {

        boolean isConnected();

        MethodResult isOnline();

        MethodResult getItem(IArguments arguments) throws LuaException;

        MethodResult getFluid(IArguments arguments) throws LuaException;

        MethodResult getChemical(IArguments arguments) throws LuaException;

        MethodResult getItems(IArguments arguments) throws LuaException;

        MethodResult getFluids(IArguments arguments) throws LuaException;

        MethodResult getChemicals(IArguments arguments) throws LuaException;

        MethodResult getCraftableItems(IArguments arguments) throws LuaException;

        MethodResult getCraftableFluids(IArguments arguments) throws LuaException;

        MethodResult getCraftableChemicals(IArguments arguments) throws LuaException;

        MethodResult getCells();

        MethodResult getDrives();

        MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException;

        MethodResult exportItem(IComputerAccess computer, IArguments arguments) throws LuaException;

        MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

        MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

        MethodResult importChemical(IComputerAccess computer, IArguments arguments) throws LuaException;

        MethodResult exportChemical(IComputerAccess computer, IArguments arguments) throws LuaException;

        MethodResult getStoredEnergy();

        MethodResult getEnergyCapacity();

        MethodResult getEnergyUsage();

        MethodResult getAverageEnergyInput();

        MethodResult getTotalExternalItemStorage();

        MethodResult getTotalExternalFluidStorage();

        MethodResult getTotalExternalChemicalStorage();

        MethodResult getTotalItemStorage();

        MethodResult getTotalFluidStorage();

        MethodResult getTotalChemicalStorage();

        MethodResult getUsedExternalItemStorage();

        MethodResult getUsedExternalFluidStorage();

        MethodResult getUsedExternalChemicalStorage();

        MethodResult getUsedItemStorage();

        MethodResult getUsedFluidStorage();

        MethodResult getUsedChemicalStorage();

        MethodResult getAvailableExternalItemStorage();

        MethodResult getAvailableExternalFluidStorage();

        MethodResult getAvailableExternalChemicalStorage();

        MethodResult getAvailableItemStorage();

        MethodResult getAvailableFluidStorage();

        MethodResult getAvailableChemicalStorage();

        MethodResult getCraftingTasks();

        // A function to get our BasicCraftJob object with the id
        MethodResult getCraftingTask(int id);

        MethodResult cancelCraftingTasks(IArguments arguments) throws LuaException;

        MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException;

        MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException;

        MethodResult craftChemical(IComputerAccess computer, IArguments arguments) throws LuaException;

        MethodResult isCraftable(IArguments arguments) throws LuaException;

        MethodResult isCrafting(IArguments arguments) throws LuaException;

        MethodResult getPatterns(IArguments arguments) throws LuaException;

    // TODO: In 1.20.1 we should use the mainThread descriptor instead
    @Deprecated(forRemoval = true, since = "1.20.1")
    class CraftJobCallback implements ILuaCallback {
        public static final String EVENT_ID = "_bridge_craft_requested";
        private static final AtomicInteger ID_SEQ = new AtomicInteger();

        public final MethodResult pull = MethodResult.pullEvent(EVENT_ID, this);
        private final int id = ID_SEQ.incrementAndGet();
        private volatile MethodResult result;

        public CraftJobCallback(IComputerAccess computer, Supplier<MethodResult> worker) {
            ServerWorker.add(() -> {
                this.result = worker.get();
                computer.queueEvent(EVENT_ID, this.id);
            });
        }

        @NotNull
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
