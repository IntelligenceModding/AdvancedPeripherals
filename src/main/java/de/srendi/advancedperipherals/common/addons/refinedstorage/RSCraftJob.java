package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.util.BasicCraftJob;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class RSCraftJob extends BasicCraftJob {

    private final ICraftingManager craftingManager;
    private final ItemStack itemToCraft;
    private final FluidStack fluidToCraft;

    private ICalculationResult calculationResult;
    private ICraftingTask craftingTask;

    public RSCraftJob(IComputerAccess computer, Level world, long amount, ItemStack itemToCraft, ICraftingManager calculationResult) {
        super(computer, "rs", world, amount);
        this.craftingManager = calculationResult;
        this.itemToCraft = itemToCraft;
        this.fluidToCraft = FluidStack.EMPTY;
    }

    public RSCraftJob(IComputerAccess computer, Level world, long amount, FluidStack fluidToCraft, ICraftingManager calculationResult) {
        super(computer, "rs", world, amount);
        this.craftingManager = calculationResult;
        this.fluidToCraft = fluidToCraft;
        this.itemToCraft = ItemStack.EMPTY;
    }

    @Override
    protected boolean isJobDone() {
        return craftingTask != null && craftingTask.getCompletionPercentage() == 100;
    }

    @Override
    protected boolean isJobCanceled() {
        return craftingTask != null && craftingTask.getCompletionPercentage() != 100 && craftingManager.getTasks().stream().noneMatch(task -> task.getId() == this.craftingTask.getId());
    }

    @Override
    public Object getParsedRequestedItem() {
        return LuaConverter.itemStackToObject(itemToCraft, (int) amount);
    }

    @Override
    public long getElapsedTime() {
        if (craftingTask == null) {
            return -1;
        }
        return System.nanoTime() - craftingTask.getStartTime() * 1_000;
    }

    @Override
    public long getTotalItems() {
        if (craftingTask == null || craftingTask.getRequested() == null) {
            return -1;
        }
        ICraftingRequestInfo requestInfo = craftingTask.getRequested();

        return requestInfo.getFluid() != null ? requestInfo.getFluid().getAmount() : requestInfo.getItem().getCount();
    }

    @Override
    public long getItemProgress() {
        return 0;
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

    @Override
    public boolean cancel() {
        if (isJobDone() || isJobCanceled()) {
            return false;
        }
        craftingManager.cancel(craftingTask.getId());
        return true;
    }

    public ICraftingTask getCraftingTask() {
        return craftingTask;
    }

    @Override
    protected void maybeCraft() {
        if (startedCrafting || calculationNotSuccessful || calculationResult == null) {
            return;
        }

        CalculationResultType type = calculationResult.getType();

        if (type == CalculationResultType.MISSING) {
            fireEvent(true, StatusConstants.MISSING_ITEMS);
            calculationNotSuccessful = true;
            return;
        }

        if (!calculationResult.isOk()) {
            calculationNotSuccessful = true;
            fireEvent(true, type.toString());
            return;
        }

        craftingManager.start(calculationResult.getTask());
        this.craftingTask = calculationResult.getTask();
        setStartedCrafting();
    }

    @Override
    protected void startCalculation() {
        if (startedCalculation) {
            return;
        }
        startedCalculation = true;
        maybeCalculateFluid();
        maybeCalculateItem();
    }

    private void maybeCalculateItem() {
        if (itemToCraft.isEmpty()) {
            return;
        }

        calculationResult = craftingManager.create(itemToCraft, (int) amount);
        fireEvent(false, StatusConstants.CALCULATION_STARTED);
    }

    private void maybeCalculateFluid() {
        if (fluidToCraft.isEmpty()) {
            return;
        }

        if (craftingManager.getPattern(fluidToCraft) == null) {
            fireEvent(true, StatusConstants.NOT_CRAFTABLE);
            return;
        }

        calculationResult = craftingManager.create(fluidToCraft, (int) amount);
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
