package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.CalculationStrategy;
import appeng.api.networking.crafting.CraftingJobStatus;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.api.networking.crafting.ICraftingSubmitResult;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.BasicCraftJob;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

//TODO needs to persistent - should be stored in the me bridge
// We also need to do the same for the rs bridge. So we want to create a proper interface to keep the lua functions the same
public class AECraftJob extends BasicCraftJob {

    private final IGridNode node;
    private final IActionSource source;
    private final ICraftingSimulationRequester simulationRequester;
    private final ICraftingRequester requester;
    private ICraftingCPU targetCpu;
    private final AEKey toCraft;

    private Future<ICraftingPlan> futureJob;
    private ICraftingPlan currentJob;
    @Nullable
    private ICraftingLink jobLink; // Job after calculation was done
    // Because the properties in `CraftingJobStatus` are set when the object is created, we need to create a supplier which
    // always re-fetches the object from the cpu
    private Supplier<CraftingJobStatus> jobStatus;
    // In the case the job is done and would return null, we have this cached one.
    private CraftingJobStatus cachedStatus;

    public AECraftJob(Level world, final IComputerAccess computer, IGridNode node, AEKey item, long amount, MeBridgeEntity bridge, ICraftingCPU target) {
        super(computer, "ae", world, amount);
        this.node = node;
        this.source = bridge;
        this.toCraft = item;
        this.simulationRequester = bridge;
        this.requester = bridge;
        this.targetCpu = target;
    }

    @LuaFunction
    public final Object getCraftingCPU() {
        if (targetCpu == null) {
            return null;
        }
        return AppEngApi.parseCraftingCPU(targetCpu, true);
    }

    @Nullable
    public ICraftingLink getJobLink() {
        return jobLink;
    }

    public ICraftingCPU getTargetCpu() {
        return targetCpu;
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

    @Override
    public Object getParsedRequestedItemImpl() {
        return AppEngApi.parseGenericStack(new GenericStack(toCraft, amount));
    }

    @Override
    public long getElapsedTimeImpl() {
        if (getJobStatus() == null) {
            return -1;
        }
        return getJobStatus().elapsedTimeNanos();
    }

    @Override
    public long getTotalItemsImpl() {
        if (getJobStatus() == null) {
            return -1;
        }
        return getJobStatus().totalItems();
    }

    @Override
    public long getItemProgressImpl() {
        if (getJobStatus() == null) {
            return -1;
        }
        return getJobStatus().progress();
    }

    @Override
    public Object getEmittedItemsImpl() {
        if (currentJob == null) {
            return null;
        }
        return AppEngApi.parseKeyCounter(currentJob.emittedItems());
    }

    @Override
    public Object getUsedItemsImpl() {
        if (currentJob == null) {
            return null;
        }
        return AppEngApi.parseKeyCounter(currentJob.usedItems());
    }

    @Override
    public Object getMissingItemsImpl() {
        if (currentJob == null) {
            return null;
        }
        return AppEngApi.parseKeyCounter(currentJob.missingItems());
    }

    @Override
    public boolean hasMultiplePathsImpl() {
        if (currentJob == null) {
            return false;
        }
        return currentJob.multiplePaths();
    }

    @Override
    public Object getFinalOutputImpl() {
        if (currentJob == null) {
            return null;
        }
        return AppEngApi.parseGenericStack(currentJob.finalOutput());
    }

    @Override
    public boolean cancelImpl() {
        if (targetCpu.isBusy()) {
            targetCpu.cancelJob();
            return true;
        }
        return false;
    }

    @LuaFunction
    public long getUsedBytes() {
        if (currentJob == null) {
            return -1;
        }
        return currentJob.bytes();
    }

    public AECraftJob withJobStatus(Supplier<CraftingJobStatus> jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public AECraftJob withCPU(ICraftingCPU craftingCpu) {
        if (this.targetCpu == null) {
            this.targetCpu = craftingCpu;
        }
        return this;
    }

    public void startCalculation() {
        if (startedCalculation) {
            return;
        }
        startedCalculation = true;

        IGrid grid = node.getGrid();

        ICraftingService craftingService = grid.getService(ICraftingService.class);

        if (!craftingService.isCraftable(toCraft)) {
            fireEvent(true, StatusConstants.NOT_CRAFTABLE);
            calculationNotSuccessful = true;
            return;
        }

        futureJob = craftingService.beginCraftingCalculation(world, this.simulationRequester, toCraft, amount, CalculationStrategy.REPORT_MISSING_ITEMS);
        fireEvent(false, StatusConstants.CALCULATION_STARTED);
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
            fireEvent(true, StatusConstants.UNKNOWN_ERROR);
            return;
        }

        if (job == null) {
            AdvancedPeripherals.debug("Job is null, should not be null.", org.apache.logging.log4j.Level.ERROR);
            fireEvent(true, StatusConstants.UNKNOWN_ERROR);
            return;
        }
        this.currentJob = job;

        KeyCounter missing = job.missingItems();
        if (!missing.isEmpty()) {
            fireEvent(true, StatusConstants.MISSING_ITEMS);
            calculationNotSuccessful = true;
            return;
        }

        IGrid grid = node.getGrid();

        ICraftingService craftingService = grid.getService(ICraftingService.class);
        ICraftingSubmitResult submitResult = craftingService.submitJob(job, requester, targetCpu, false, this.source);
        if (!submitResult.successful()) {
            calculationNotSuccessful = true;
            fireEvent(true, submitResult.errorCode().toString());
            return;
        }

        this.jobLink = submitResult.link();
        this.futureJob = null;
        setStartedCrafting();
        prepareCPUAndStatus(craftingService);
    }

    public void jobStateChanged() {
        ICraftingLink jobLink = this.jobLink;
        if (jobLink == null) {
            fireEvent(true, StatusConstants.UNKNOWN_ERROR);
            return;
        }

        if (jobLink.isCanceled() && !isJobCanceled) {
            fireEvent(false, StatusConstants.JOB_CANCELED);
            setJobCanceled();
            return;
        }

        if (jobLink.isDone() && !isJobDone) {
            fireEvent(false, StatusConstants.JOB_DONE);
            setJobDone();
        }
    }

    private void prepareCPUAndStatus(ICraftingService service) {
        if (jobLink == null || jobStatus != null || !startedCrafting) {
            return;
        }
        for (ICraftingCPU cpu : service.getCpus()) {
            if (cpu instanceof CraftingCPUCluster cpuCluster) {
                if (cpuCluster.craftingLogic.getLastLink() != null && cpuCluster.craftingLogic.getLastLink().getCraftingID().equals(jobLink.getCraftingID())) {
                    this.jobStatus = () -> {
                        // Compare the id of the job in the cpu. This job object can exist longer than the job needs time to complete. So the cpu could have a new job
                        if (cpuCluster.craftingLogic.getLastLink() != null && cpuCluster.craftingLogic.getLastLink().getCraftingID().equals(jobLink.getCraftingID()))
                            return cpuCluster.getJobStatus();
                        return null;
                    };
                    cpuCluster.craftingLogic.addListener((key) -> {
                        // The last time the listeners are called from the cpu logic is when the job is finished
                        // These listeners are not intended by ae2 to be used like this, but it works, and we don't modify the key
                        if (cpuCluster.getJobStatus() != null) {
                            this.cachedStatus = cpuCluster.getJobStatus();
                        }
                    });
                    this.targetCpu = cpu;
                    return;
                }
            }
        }
        AdvancedPeripherals.debug("Could not find CPU or job link even after job started", org.apache.logging.log4j.Level.WARN);
    }

    private CraftingJobStatus getJobStatus() {
        if (jobStatus == null || jobStatus.get() == null && cachedStatus != null) {
            return cachedStatus;
        }
        cachedStatus = jobStatus.get();
        return jobStatus.get();
    }
}
