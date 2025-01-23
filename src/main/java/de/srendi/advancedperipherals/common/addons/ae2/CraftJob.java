package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.CalculationStrategy;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.api.networking.crafting.ICraftingSubmitResult;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

//TODO needs to persistent - should be stored in the me bridge
// We also need to do the same for the rs bridge. So we want to create a proper interface to keep the lua functions the same
public class CraftJob {

    private static final String CALCULATION_STARTED = "CALCULATION_STARTED";
    private static final String CRAFTING_STARTED = "CRAFTING_STARTED";
    private static final String JOB_CANCELED = "JOB_CANCELED";
    private static final String JOB_DONE = "JOB_DONE";
    private static final String NOT_CRAFTABLE = "NOT_CRAFTABLE";
    private static final String MISSING_ITEMS = "MISSING_ITEMS";
    private static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public static final String EVENT = "ae_crafting";

    private static volatile long idSeq = 0;

    private final long id = ++idSeq;
    private final IComputerAccess computer;
    private final IGridNode node;
    private final IActionSource source;
    private final ICraftingSimulationRequester requester;
    private final ICraftingCPU target;
    private final AEKey item;

    private final long amount;
    private final Level world;
    private Future<ICraftingPlan> futureJob;
    @Nullable
    private ICraftingLink jobLink; // Job after calculation was done
    private boolean startedCrafting = false;
    private boolean startedCalculation = false;
    private boolean calculationNotSuccessful = false;
    private boolean errorOccurred = false;
    private String debugMessage = "";

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

    @LuaFunction
    public final long getId() {
        return id;
    }

    @LuaFunction
    public final boolean isDone() {
        return jobLink != null && jobLink.isDone();
    }

    @LuaFunction
    public final boolean isCanceled() {
        return jobLink != null && jobLink.isCanceled();
    }

    @LuaFunction
    public final boolean isCraftingStarted() {
        return startedCrafting;
    }

    @LuaFunction
    public final boolean isCalculationStarted() {
        return startedCalculation;
    }

    @LuaFunction
    public final boolean isCalculationNotSuccessful() {
        return calculationNotSuccessful;
    }

    @LuaFunction
    public final boolean hasErrorOccurred() {
        return errorOccurred;
    }

    @LuaFunction
    public final boolean hasDebugMessage() {
        return !debugMessage.isEmpty();
    }

    @LuaFunction
    public final String getDebugMessage() {
        return debugMessage;
    }

    //TODO use pre defined constants as event arg
    protected void fireNotConnected() {
        fireEvent(false, false, true, false, false, "not connected");
    }

    public void setStartedCrafting(boolean startedCrafting) {
        this.startedCrafting = startedCrafting;
        fireEvent(true, true, false, false, false, CRAFTING_STARTED);
    }

    protected void fireEvent(boolean calculationStarted, boolean craftingStarted, boolean isDone, boolean wasCanceled, boolean error, String message) {
        this.computer.queueEvent(EVENT, calculationStarted, craftingStarted, isDone, wasCanceled, error, this.id, message);
        this.debugMessage = message;
        this.errorOccurred = error;
    }

    public boolean canBePurged() {
        return calculationNotSuccessful;
    }

    @Nullable
    public ICraftingLink getJobLink() {
        return jobLink;
    }

    public void startCalculation() {
        if (startedCalculation) {
            return;
        }
        startedCalculation = true;

        IGrid grid = node.getGrid();

        ICraftingService crafting = grid.getService(ICraftingService.class);

        if (item == null) {
            AdvancedPeripherals.debug("Could not get AEItem from monitor", org.apache.logging.log4j.Level.ERROR);
            return;
        }

        if (!crafting.isCraftable(item)) {
            fireEvent(false, false, true, false, false, NOT_CRAFTABLE);
            return;
        }

        futureJob = crafting.beginCraftingCalculation(world, this.requester, item, amount, CalculationStrategy.REPORT_MISSING_ITEMS);
        fireEvent(true, false, false, false, false, CALCULATION_STARTED);
    }

    public void tick(ICraftingRequester requester) {
        startCalculation();
        maybeCraft(requester);
    }

    public void maybeCraft(ICraftingRequester requester) {
        if (startedCrafting || futureJob == null || !futureJob.isDone()) {
            return;
        }
        ICraftingPlan job;

        try {
            job = futureJob.get();
        } catch (ExecutionException | InterruptedException ex) {
            AdvancedPeripherals.debug("Tried to get job, but job calculation is not done. Should be done.", org.apache.logging.log4j.Level.ERROR);
            ex.printStackTrace();
            fireEvent(true, false, false, false, true, UNKNOWN_ERROR);
            return;
        }

        if (job == null) {
            AdvancedPeripherals.debug("Job is null, should not be null.", org.apache.logging.log4j.Level.ERROR);
            fireEvent(true, false, false, false, true, UNKNOWN_ERROR);
            return;
        }

        KeyCounter missing = job.missingItems();
        if (!missing.isEmpty()) {
            fireEvent(true, false, false, false, true, MISSING_ITEMS);
            calculationNotSuccessful = true;
            return;
        }

        IGrid grid = node.getGrid();

        ICraftingService crafting = grid.getService(ICraftingService.class);
        ICraftingSubmitResult submitResult = crafting.submitJob(job, requester, target, false, this.source);
        if (!submitResult.successful()) {
            calculationNotSuccessful = true;
            fireEvent(true, true, false, false, true, submitResult.errorCode().toString());
            return;
        }

        this.jobLink = submitResult.link();
        this.futureJob = null;
        setStartedCrafting(true);
    }

    public void jobStateChange() {
        ICraftingLink jobLink = this.jobLink;
        if (jobLink == null) {
            fireEvent(true, true, true, false, true, UNKNOWN_ERROR);
            return;
        }

        if (jobLink.isCanceled()) {
            fireEvent(true, true, false, true, false, JOB_CANCELED);
        }

        if (jobLink.isDone()) {
            fireEvent(true, true, true, false, false, JOB_DONE);
        }
    }
}
