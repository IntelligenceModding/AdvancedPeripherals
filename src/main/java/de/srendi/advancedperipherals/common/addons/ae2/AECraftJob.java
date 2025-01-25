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
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;
import de.srendi.advancedperipherals.common.util.inventory.BasicCraftJob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

//TODO needs to persistent - should be stored in the me bridge
// We also need to do the same for the rs bridge. So we want to create a proper interface to keep the lua functions the same
public class AECraftJob extends BasicCraftJob {

    private final IGridNode node;
    private final IActionSource source;
    private final ICraftingSimulationRequester simulationRequester;
    private final ICraftingRequester requester;
    private final ICraftingCPU target;
    private final AEKey toCraft;

    private Future<ICraftingPlan> futureJob;
    @Nullable
    private ICraftingLink jobLink; // Job after calculation was done

    public AECraftJob(Level world, final IComputerAccess computer, IGridNode node, AEKey item, long amount, MeBridgeEntity bridge, ICraftingCPU target) {
        super(computer, "ae", world, amount);
        this.node = node;
        this.source = bridge;
        this.toCraft = item;
        this.simulationRequester = bridge;
        this.requester = bridge;
        this.target = target;
    }

    @Nullable
    public ICraftingLink getJobLink() {
        return jobLink;
    }

    public ICraftingCPU getTargetCpu() {
        return target;
    }

    public AEKey getToCraft() {
        return toCraft;
    }

    @Override
    protected boolean isJobDone() {
        return jobLink != null && jobLink.isDone();
    }

    @Override
    protected boolean isJobCanceled() {
        return jobLink != null && jobLink.isCanceled();
    }

    public void startCalculation() {
        if (startedCalculation) {
            return;
        }
        startedCalculation = true;

        IGrid grid = node.getGrid();

        ICraftingService crafting = grid.getService(ICraftingService.class);

        if (!crafting.isCraftable(toCraft)) {
            fireEvent(false, false, false, false, false, NOT_CRAFTABLE);
            calculationNotSuccessful = true;
            return;
        }

        futureJob = crafting.beginCraftingCalculation(world, this.simulationRequester, toCraft, amount, CalculationStrategy.REPORT_MISSING_ITEMS);
        fireEvent(true, false, false, false, false, CALCULATION_STARTED);
    }

    public void maybeCraft() {
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
            fireEvent(true, false, false, false, true, submitResult.errorCode().toString());
            return;
        }

        this.jobLink = submitResult.link();
        this.futureJob = null;
        setStartedCrafting();
    }

    public void jobStateChanged() {
        ICraftingLink jobLink = this.jobLink;
        if (jobLink == null) {
            fireEvent(true, true, true, false, true, UNKNOWN_ERROR);
            return;
        }

        if (jobLink.isCanceled() && !isJobCanceled) {
            fireEvent(true, true, false, true, false, JOB_CANCELED);
            setJobCanceled();
            return;
        }

        if (jobLink.isDone() && !isJobDone) {
            fireEvent(true, true, true, false, false, JOB_DONE);
            setJobDone();
        }
    }
}
