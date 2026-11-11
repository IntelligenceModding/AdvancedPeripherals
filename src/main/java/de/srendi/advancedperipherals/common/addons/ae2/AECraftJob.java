package de.srendi.advancedperipherals.common.addons.ae2;

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
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.blockentities.MEBridgeEntity;
import de.srendi.advancedperipherals.common.util.ServerWorker;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.BasicCraftJob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class AECraftJob extends BasicCraftJob {
    private final Level world;
    private final IGridNode node;
    private final IActionSource source;
    private final ICraftingSimulationRequester simulationRequester;
    private final ICraftingRequester requester;
    private ICraftingCPU targetCpu;
    private final GenericStack toCraft;

    private Future<ICraftingPlan> futureJob;
    private ICraftingPlan currentJob;
    @Nullable
    private ICraftingLink jobLink; // Job after calculation was done
    // In the case the job is done and would return null, we have this cached one.
    private CraftingJobStatus cachedStatus;

    public AECraftJob(GenericStack toCraft, ICraftingLink jobLink, ICraftingCPU cpu) {
        super(-1, jobLink.getCraftingID(), null, "ae");
        this.world = null;
        this.node = null;
        this.source = null;
        this.simulationRequester = null;
        this.requester = null;
        this.targetCpu = cpu;
        this.toCraft = toCraft;
        this.currentJob = null;
        this.jobLink = jobLink;
        this.startedCalculation = true;
        this.startedCrafting = true;
        prepareCPUAndStatus();
    }

    public AECraftJob(Level world, IComputerAccess computer, GenericStack toCraft, IGridNode node, MEBridgeEntity bridge, ICraftingCPU cpu) {
        super(computer, "ae");
        this.world = world;
        this.node = node;
        this.source = bridge;
        this.toCraft = toCraft;
        this.simulationRequester = bridge;
        this.requester = bridge;
        this.targetCpu = cpu;
    }

    @LuaFunction
    public final Object getCraftingCPU() {
        if (targetCpu == null) {
            return null;
        }
        return AEApi.parseCraftingCPU(targetCpu, true);
    }

    @Nullable
    public ICraftingLink getJobLink() {
        return jobLink;
    }

    public ICraftingCPU getTargetCpu() {
        return targetCpu;
    }

    public AEKey getToCraft() {
        return toCraft.what();
    }

    protected boolean isCrafting() {
        UUID uuid = this.uuid;
        if (uuid == null || jobLink == null || targetCpu == null) {
            return false;
        }
        if (!targetCpu.isBusy()) {
            return false;
        }
        ICpuWrapper wrapped = ICpuWrapper.of(targetCpu);
        return wrapped != null && uuid.equals(wrapped.getCraftingID());
    }

    @Override
    protected boolean isJobDone() {
        // link.isDone does not work on player requested craftings.
        return jobLink != null && !isJobCanceled() && !isCrafting();
    }

    @Override
    protected boolean isJobCanceled() {
        return jobLink != null && jobLink.isCanceled();
    }

    @Override
    public Object getParsedRequestedItemImpl() {
        return AEApi.parseGenericStack(toCraft);
    }

    @Override
    public long getElapsedTimeImpl() {
        CraftingJobStatus status = getJobStatus();
        if (status == null) {
            return -1;
        }
        return status.elapsedTimeNanos() / 1000000;
    }

    @Override
    public double getItemProgressImpl() {
        CraftingJobStatus status = getJobStatus();
        if (status == null) {
            return -1;
        }
        return ((double) status.progress()) / status.totalItems();
    }

    @Override
    public Object getEmittedItemsImpl() {
        if (currentJob == null) {
            return null;
        }
        return AEApi.parseKeyCounter(currentJob.emittedItems());
    }

    @Override
    public Object getUsedItemsImpl() {
        if (currentJob == null) {
            return null;
        }
        return AEApi.parseKeyCounter(currentJob.usedItems());
    }

    @Override
    public Object getMissingItemsImpl() {
        if (currentJob == null) {
            return null;
        }
        return AEApi.parseKeyCounter(currentJob.missingItems());
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
        return AEApi.parseGenericStack(currentJob.finalOutput());
    }

    @Override
    public boolean cancelImpl() {
        ICraftingLink jobLink = this.jobLink;
        if (jobLink == null) {
            return false;
        }
        if (isCrafting()) {
            jobLink.cancel();
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

    @Override
    public void startCalculation() {
        if (startedCalculation) {
            return;
        }
        startedCalculation = true;

        IGrid grid = node.getGrid();

        ICraftingService craftingService = grid.getService(ICraftingService.class);

        if (!craftingService.isCraftable(toCraft.what())) {
            fireEvent(true, StatusConstants.NOT_CRAFTABLE);
            calculationNotSuccessful = true;
            return;
        }

        futureJob = craftingService.beginCraftingCalculation(world, this.simulationRequester, toCraft.what(), toCraft.amount(), CalculationStrategy.REPORT_MISSING_ITEMS);
        fireEvent(false, StatusConstants.CALCULATION_STARTED);
    }

    @Override
    public void maybeCraft() {
        if (startedCrafting || futureJob == null || !futureJob.isDone()) {
            return;
        }
        ICraftingPlan job;

        try {
            job = futureJob.get();
        } catch (ExecutionException | InterruptedException ex) {
            AdvancedPeripherals.debug(org.apache.logging.log4j.Level.ERROR, "Tried to get job, but job calculation is not done. Should be done.");
            ex.printStackTrace();
            fireEvent(true, StatusConstants.UNKNOWN_ERROR);
            return;
        }

        if (job == null) {
            AdvancedPeripherals.debug(org.apache.logging.log4j.Level.ERROR, "Job is null, should not be null.");
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
        ICraftingSubmitResult submitResult = craftingService.submitJob(job, requester, this.targetCpu, false, this.source);
        if (!submitResult.successful()) {
            calculationNotSuccessful = true;
            fireEvent(true, submitResult.errorCode().toString());
            return;
        }

        ICraftingLink jobLink = submitResult.link();
        this.jobLink = jobLink;
        this.uuid = jobLink.getCraftingID();
        this.futureJob = null;
        setStartedCrafting();
        this.targetCpu = searchJobCPU(this.uuid, craftingService);
        prepareCPUAndStatus();
    }

    @Override
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

    private void prepareCPUAndStatus() {
        if (!startedCrafting) {
            return;
        }
        ICraftingCPU cpu = this.targetCpu;
        ICpuWrapper wrapped = ICpuWrapper.of(cpu);
        if (wrapped != null) {
            wrapped.addListener(new Consumer<>() {
                private boolean canceled = false;

                @Override
                public void accept(AEKey key) {
                    // The last time the listeners are called from the cpu logic is when the job is finished
                    // These listeners are not intended by ae2 to be used like this, but it works, and we don't modify the key
                    if (canceled) {
                        return;
                    }
                    CraftingJobStatus newStatus = cpu.getJobStatus();
                    if (newStatus == null) {
                        return;
                    }
                    cachedStatus = newStatus;
                    if (newStatus.progress() < newStatus.totalItems()) {
                        return;
                    }
                    canceled = true;
                    ServerWorker.add(() -> wrapped.removeListener(this));
                }
            });
        }
    }

    private CraftingJobStatus getJobStatus() {
        ICpuWrapper wrapped = ICpuWrapper.of(this.targetCpu);
        if (wrapped != null && this.jobLink != null) {
            if (this.jobLink.getCraftingID().equals(wrapped.getCraftingID())) {
                this.cachedStatus = Objects.requireNonNull(this.targetCpu.getJobStatus());
            }
        }
        return this.cachedStatus;
    }

    @Nullable
    public static ICraftingCPU searchJobCPU(UUID id, ICraftingService service) {
        for (ICraftingCPU cpu : service.getCpus()) {
            ICpuWrapper wrapped = ICpuWrapper.of(cpu);
            if (wrapped != null && id.equals(wrapped.getCraftingID())) {
                return cpu;
            }
        }
        return null;
    }
}
