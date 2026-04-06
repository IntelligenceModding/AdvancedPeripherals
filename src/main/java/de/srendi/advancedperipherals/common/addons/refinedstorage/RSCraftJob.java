package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.autocrafting.preview.Preview;
import com.refinedmods.refinedstorage.api.autocrafting.preview.PreviewType;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.autocrafting.task.TaskId;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.impl.autocrafting.TimeoutableCancellationToken;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.BasicCraftJob;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class RSCraftJob extends BasicCraftJob {

    private final AutocraftingNetworkComponent autocraftingComponent;
    private final ResourceKey toCraft;

    private TaskStatus craftingTask;
    private Preview preview;
    private Future<Optional<Preview>> futureCalculationResult;
    private Optional<TaskId> futureTask = Optional.empty();

    public RSCraftJob(IComputerAccess computer, Level world, long amount, ResourceKey toCraft, AutocraftingNetworkComponent calculationResult) {
        super(computer, "rs", world, amount);
        this.autocraftingComponent = calculationResult;
        this.toCraft = toCraft;
    }

    @Override
    protected boolean isJobDone() {
        return craftingTask != null && craftingTask.percentageCompleted() >= 100;
    }

    @Override
    protected boolean isJobCanceled() {
        return craftingTask != null && craftingTask.percentageCompleted() < 100 && autocraftingComponent.getStatuses().stream().noneMatch(task -> task.info().id() == craftingTask.info().id());
    }

    @Override
    public Object getParsedRequestedItemImpl() {
        return RSApi.getObjectFromResourceKey(toCraft, amount, autocraftingComponent);
    }

    @Override
    public long getElapsedTimeImpl() {
        if (craftingTask == null) {
            return -1;
        }
        return System.currentTimeMillis() - craftingTask.info().startTime();
    }

    @Override
    public long getTotalItemsImpl() {
        if (craftingTask == null) {
            return -1;
        }

        return craftingTask.items().size();
    }

    @Override
    public long getItemProgressImpl() {
        return craftingTask == null ? 0 : craftingTask.items().stream().mapToLong(TaskStatus.Item::stored).sum();
    }

    @Override
    public Object getEmittedItemsImpl() {
        if (preview == null) {
            return Collections.emptyList();
        }

        return preview.items().stream().filter(item -> item.toCraft() > 0).map(item -> RSApi.getObjectFromResourceKey(item.resource(), item.toCraft(), autocraftingComponent)).collect(Collectors.toList());
    }

    @Override
    public Object getUsedItemsImpl() {
        // Not supported for rs2
        return Collections.emptyList();
    }

    @Override
    public Object getMissingItemsImpl() {
        if (preview == null) {
            return Collections.emptyList();
        }

        return preview.items().stream().filter(item -> item.missing() > 0).map(item -> RSApi.getObjectFromResourceKey(item.resource(), item.missing(), autocraftingComponent)).collect(Collectors.toList());
    }

    @Override
    public boolean hasMultiplePathsImpl() {
        // Not supported for rs2
        return false;
    }

    @Override
    public Object getFinalOutputImpl() {
        if (preview == null) {
            return Collections.emptyList();
        }

        return preview.outputsOfPatternWithCycle().stream().map((resource) -> RSApi.getObjectFromResourceAmount(resource, autocraftingComponent)).collect(Collectors.toList());
    }

    public TaskStatus getCraftingTask() {
        return craftingTask;
    }

    @Override
    public boolean cancelImpl() {
        if (isJobDone() || isJobCanceled()) {
            return false;
        }
        autocraftingComponent.cancel(craftingTask.info().id());
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        // RS2 does re-create a new instance of the TaskStatus when something changes - so we actually need to re fetch that from the system
        // It's cursed, but the only way currently
        if (craftingTask != null) {
            for (TaskStatus status : autocraftingComponent.getStatuses()) {
                if (status.info().id().equals(craftingTask.info().id())) {
                    this.craftingTask = status;
                    break;
                }
            }
        }
        // The following is to get the TaskStatus from the issued task
        if (futureTask.isEmpty() || craftingTask != null) {
            return;
        }

        TaskId id = futureTask.get();
        for (TaskStatus status : autocraftingComponent.getStatuses()) {
            if (status.info().id().equals(id)) {
                this.craftingTask = status;
                // And only now we set that the crafting is started.
                setStartedCrafting();
                break;
            }
        }
    }

    @Override
    protected void maybeCraft() {
        if (startedCrafting || futureTask.isPresent() || futureCalculationResult == null || !futureCalculationResult.isDone()) {
            return;
        }

        Optional<Preview> optionalPreview;

        try {
            optionalPreview = futureCalculationResult.get();
        } catch (ExecutionException | InterruptedException ex) {
            AdvancedPeripherals.debug("Tried to get preview, but preview calculation is not done. Should be done.", ex);
            fireEvent(true, StatusConstants.UNKNOWN_ERROR);
            return;
        }

        // TODO: I currently don't exactly know when the optional can be empty after the future is done. So I need to evaluate this.
        if (optionalPreview.isEmpty()) {
            AdvancedPeripherals.debug(org.apache.logging.log4j.Level.ERROR, "preview optional is empty.");
            fireEvent(true, StatusConstants.UNKNOWN_ERROR);
            return;
        }

        Preview preview = optionalPreview.get();
        this.preview = preview;

        PreviewType previewType = preview.type();

        if (previewType == PreviewType.MISSING_RESOURCES) {
            calculationNotSuccessful = true;
            fireEvent(true, StatusConstants.MISSING_ITEMS);
            return;
        }

        if (previewType != PreviewType.SUCCESS) {
            calculationNotSuccessful = true;
            fireEvent(true, previewType.toString());
            return;
        }

        // We first create a preview which calculates the recipes, and then we check if the preview was successful
        // If it was, we again start a task which again calculates the recipes, and then we hope nothing changed from the first calculation
        futureTask = autocraftingComponent.startTask(toCraft, amount, Actor.EMPTY, false, new TimeoutableCancellationToken());
    }

    @Override
    protected void startCalculation() {
        if (startedCalculation) {
            return;
        }
        startedCalculation = true;

        if (autocraftingComponent.getPatternsByOutput(toCraft).isEmpty()) {
            fireEvent(true, StatusConstants.NOT_CRAFTABLE);
            return;
        }

        futureCalculationResult = autocraftingComponent.getPreview(toCraft, amount, new TimeoutableCancellationToken());
        fireEvent(false, StatusConstants.CALCULATION_STARTED);
    }

    @Override
    public void jobStateChanged() {
        if (this.craftingTask == null) {
            fireEvent(true, StatusConstants.UNKNOWN_ERROR);
            return;
        }

        if (isJobCanceled() && !isJobCanceled) {
            fireEvent(false, StatusConstants.JOB_CANCELED);
            setJobCanceled();
            return;
        }

        if (isJobDone() && !isJobDone) {
            fireEvent(true, StatusConstants.JOB_DONE);
            setJobDone();
        }
    }
}
