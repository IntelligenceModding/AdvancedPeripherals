package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.autocrafting.preview.Preview;
import com.refinedmods.refinedstorage.api.autocrafting.preview.PreviewType;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.BasicCraftJob;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RSCraftJob extends BasicCraftJob {

    private final AutocraftingNetworkComponent autocraftingComponent;
    private final ResourceKey toCraft;

    private TaskStatus craftingTask;
    private Future<Optional<Preview>> futureTask;

    public RSCraftJob(IComputerAccess computer, Level world, long amount, ResourceKey toCraft, AutocraftingNetworkComponent calculationResult) {
        super(computer, "rs", world, amount);
        this.autocraftingComponent = calculationResult;
        this.toCraft = toCraft;
    }

    @Override
    protected boolean isJobDone() {
        return craftingTask != null && craftingTask.percentageCompleted() == 100;
    }

    @Override
    protected boolean isJobCanceled() {
        return craftingTask != null && craftingTask.percentageCompleted() != 100 && autocraftingComponent.getStatuses().stream().noneMatch(task -> task.info().id() == craftingTask.info().id());
    }

    @Override
    public Object getParsedRequestedItem() {
        return RefinedStorageApi.getObjectFromResourceKey(toCraft, amount);
    }

    @Override
    public long getElapsedTime() {
        if (craftingTask == null) {
            return -1;
        }
        return System.currentTimeMillis() - craftingTask.info().startTime();
    }

    @Override
    public long getTotalItems() {
        if (craftingTask == null) {
            return -1;
        }

        return craftingTask.items().size();
    }

    @Override
    public long getItemProgress() {
        return craftingTask == null ? 0 : craftingTask.items().stream().mapToLong(TaskStatus.Item::stored).sum();
    }

    @Override
    public Object getEmittedItems() {
        return null;
    }

    @Override
    public Object getUsedItems() {
        return null;
    }

    @Override
    public Object getMissingItems() {
        return null;
    }

    @Override
    public boolean hasMultiplePaths() {
        return false;
    }

    @Override
    public Object getFinalOutput() {
        return null;
    }

    public TaskStatus getCraftingTask() {
        return craftingTask;
    }

    @Override
    public boolean cancel() {
        if (isJobDone() || isJobCanceled()) {
            return false;
        }
        autocraftingComponent.cancel(craftingTask.info().id());
        return true;
    }

    @Override
    protected void maybeCraft() {
        if (startedCrafting || futureTask == null || !futureTask.isDone()) {
            return;
        }

        Optional<Preview> optionalPreview;

        try {
            optionalPreview = futureTask.get();
        } catch (ExecutionException | InterruptedException ex) {
            AdvancedPeripherals.debug("Tried to get preview, but preview calculation is not done. Should be done.", org.apache.logging.log4j.Level.ERROR);
            ex.printStackTrace();
            fireEvent(true, StatusConstants.UNKNOWN_ERROR);
            return;
        }

        // TODO: I currently don't exactly know when the optional can be empty after the future is done. So I need to evaluate this.
        if (optionalPreview.isEmpty()) {
            AdvancedPeripherals.debug("preview optional is empty.", org.apache.logging.log4j.Level.ERROR);
            fireEvent(true, StatusConstants.UNKNOWN_ERROR);
            return;
        }

        Preview preview = optionalPreview.orElse(null);
        PreviewType previewType = preview.type();

        if (previewType == PreviewType.MISSING_RESOURCES) {
            fireEvent(true, StatusConstants.MISSING_ITEMS);
            calculationNotSuccessful = true;
            return;
        }

        if (previewType != PreviewType.SUCCESS) {
            calculationNotSuccessful = true;
            fireEvent(true, previewType.toString());
            return;
        }

        // How RS2 handles crafting is a bit cursed. We first create a preview which calculates the recipes, and then we check if the preview was successful
        // If it was, we again start a task which again calculates the recipes, and then we hope nothing changed from the first calculation
        autocraftingComponent.startTask(toCraft, amount, Actor.EMPTY, false);
        setStartedCrafting();
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

        futureTask = autocraftingComponent.getPreview(toCraft, amount);
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
