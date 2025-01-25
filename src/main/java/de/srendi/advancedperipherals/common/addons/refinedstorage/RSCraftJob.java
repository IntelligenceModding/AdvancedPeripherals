package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.util.inventory.BasicCraftJob;
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

    public ICraftingTask getCraftingTask() {
        return craftingTask;
    }

    @Override
    protected void maybeCraft() {
        if (startedCrafting || calculationNotSuccessful ||  calculationResult == null) {
            return;
        }

        CalculationResultType type = calculationResult.getType();

        if (type == CalculationResultType.MISSING) {
            fireEvent(true, false, false, false, true, MISSING_ITEMS);
            calculationNotSuccessful = true;
            return;
        }

        if (!calculationResult.isOk()) {
            calculationNotSuccessful = true;
            fireEvent(true, false, false, false, true, type.toString());
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
        fireEvent(true, false, false, false, false, CALCULATION_STARTED);
    }

    private void maybeCalculateFluid() {
        if (fluidToCraft.isEmpty()) {
            return;
        }

        if (craftingManager.getPattern(fluidToCraft) == null) {
            fireEvent(false, false, false, false, false, NOT_CRAFTABLE);
            return;
        }

        calculationResult = craftingManager.create(fluidToCraft, (int) amount);
        fireEvent(true, false, false, false, false, CALCULATION_STARTED);
    }

    @Override
    public void jobStateChanged() {
        if (this.craftingTask == null) {
            fireEvent(true, true, true, false, true, UNKNOWN_ERROR);
            return;
        }

        if (isJobCanceled() && !isJobCanceled) {
            fireEvent(true, true, false, true, false, JOB_CANCELED);
            setJobCanceled();
            return;
        }

        if (isJobDone() && !isJobDone) {
            fireEvent(true, true, true, false, false, JOB_DONE);
            setJobDone();
        }
    }
}
