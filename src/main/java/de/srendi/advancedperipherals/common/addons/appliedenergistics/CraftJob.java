package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.*;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CraftJob implements ILuaCallback {

    public static final String EVENT = "crafting";

    private final IComputerAccess computer;
    private final IGridNode node;
    private final IActionSource source;
    private final ICraftingSimulationRequester requester;
    private final ICraftingCPU target;
    private final AEKey item;

    private final long amount;
    private final Level world;
    private Future<ICraftingPlan> futureJob;
    private boolean startedCrafting = false;

    private MethodResult result;
    private LuaException exception;

    public CraftJob(Level world, final IComputerAccess computer, IGridNode node, AEKey item, long amount, IActionSource source,
                    ICraftingSimulationRequester requester, ICraftingCPU target) {
        this.computer = computer;
        this.node = node;
        this.world = world;
        this.source = source;
        this.item = item;
        this.amount = amount;
        this.requester = requester;
        this.target = target;
    }

    protected void fireEvent(boolean success, @Nullable String exception) {
        this.result = MethodResult.of(success, exception);
        this.exception = new LuaException(exception);
        this.computer.queueEvent(EVENT, success, exception);
    }

    protected void fireNotConnected() {
        fireEvent(false, "not connected");
    }

    public void setStartedCrafting(boolean startedCrafting) {
        this.startedCrafting = startedCrafting;
    }

    public boolean isCraftingStarted() {
        return startedCrafting;
    }

    public void startCrafting() {
        IGrid grid = node.getGrid();
        if (grid == null) { //true when the block is not connected
            fireNotConnected();
            return;
        }

        ICraftingService crafting = grid.getService(ICraftingService.class);

        if (item == null) {
            AdvancedPeripherals.debug("Could not get AEItem from monitor", org.apache.logging.log4j.Level.FATAL);
            return;
        }

        if (!crafting.isCraftable(item)) {
            fireEvent(false, item.getId().toString() + " is not craftable");
            return;
        }

        futureJob = crafting.beginCraftingCalculation(world, this.requester, item, amount, CalculationStrategy.REPORT_MISSING_ITEMS);
        fireEvent(true, "Started calculation of the recipe. After it's finished, the system will start crafting the item.");
    }

    public void maybeCraft() {
        if (startedCrafting || futureJob == null || !futureJob.isDone())
            return;
        ICraftingPlan job;
        try {
            job = futureJob.get();
        } catch (ExecutionException | InterruptedException ex) {
            AdvancedPeripherals.debug("Tried to get job, but job calculation is not done. Should be done.", org.apache.logging.log4j.Level.FATAL);
            ex.printStackTrace();
            return;
        }

        if (job == null) {
            AdvancedPeripherals.debug("Job is null, should not be null.", org.apache.logging.log4j.Level.FATAL);
            return;
        }

        if (job.simulation()) {
            return;
        }

        IGrid grid = node.getGrid();
        if (grid == null) {
            return;
        }

        //TODO: Create events or methods like `isCraftingFinished` or `getCraftingJobState`
        ICraftingService crafting = grid.getService(ICraftingService.class);
        crafting.submitJob(job, null, target, false, this.source);

        setStartedCrafting(true);

        this.futureJob = null;
    }

    @NotNull
    @Override
    public MethodResult resume(Object[] objects) {
        if (result != null) return result;
        if (exception != null) return MethodResult.of(exception);
        return MethodResult.of();
    }
}
