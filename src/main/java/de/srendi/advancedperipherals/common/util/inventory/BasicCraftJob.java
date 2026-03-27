package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicLong;

public abstract class BasicCraftJob {

    private static final String EVENT_SUFFIX = "_crafting";
    private static final int JOB_DONE_PURGE_TIME = 5 * 60 * 1000;

    public static final AtomicLong ID_SEQ = new AtomicLong();

    protected final long id = ID_SEQ.incrementAndGet();
    protected final IComputerAccess computer;
    protected final String eventPrefix;
    protected final long amount;
    protected final Level world;

    protected boolean startedCrafting = false;
    protected boolean startedCalculation = false;
    protected boolean calculationNotSuccessful = false;
    protected boolean errorOccurred = false;
    protected boolean isJobDone = false;
    protected long jobDoneTime = 0;
    protected boolean isJobCanceled = false;
    protected String debugMessage = null;

    public BasicCraftJob(IComputerAccess computer, String eventPrefix, Level world, long amount) {
        this.computer = computer;
        this.eventPrefix = eventPrefix;
        this.world = world;
        this.amount = amount;
    }

    @LuaFunction
    public final long getId() {
        return id;
    }

    protected abstract boolean isJobDone();

    @LuaFunction
    public final boolean isDone() {
        return isJobDone();
    }

    protected abstract boolean isJobCanceled();

    @LuaFunction
    public final boolean isCanceled() {
        return isJobCanceled();
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
    public final String getDebugMessage() {
        return debugMessage;
    }

    @LuaFunction
    public final Object getRequestedItem() {
        return getParsedRequestedItemImpl();
    }

    @LuaFunction
    public final long getElapsedTime() {
        return getElapsedTimeImpl();
    }

    @LuaFunction
    public final long getTotalItems() {
        return getTotalItemsImpl();
    }

    @LuaFunction
    public final long getItemProgress() {
        return getItemProgressImpl();
    }

    @LuaFunction
    public final Object getEmittedItems() {
        return getEmittedItemsImpl();
    }

    @LuaFunction
    public final Object getUsedItems() {
        return getUsedItemsImpl();
    }

    @LuaFunction
    public final Object getMissingItems() {
        return getMissingItemsImpl();
    }

    @LuaFunction
    public final boolean hasMultiplePaths() {
        return hasMultiplePathsImpl();
    }

    @LuaFunction
    public final Object getFinalOutput() {
        return getFinalOutputImpl();
    }

    @LuaFunction
    public final boolean cancel() {
        return cancelImpl();
    }

    protected abstract Object getParsedRequestedItemImpl();

    protected abstract long getElapsedTimeImpl();

    protected abstract long getTotalItemsImpl();

    protected abstract long getItemProgressImpl();

    protected abstract Object getEmittedItemsImpl();

    protected abstract Object getUsedItemsImpl();

    protected abstract Object getMissingItemsImpl();

    protected abstract boolean hasMultiplePathsImpl();

    protected abstract Object getFinalOutputImpl();

    protected abstract boolean cancelImpl();

    protected Level getWorld() {
        return world;
    }

    protected long getAmount() {
        return amount;
    }

    public boolean canBePurged() {
        return calculationNotSuccessful || ((isJobDone || isJobCanceled) && jobDoneTime + JOB_DONE_PURGE_TIME < System.currentTimeMillis());
    }

    protected void fireNotConnected() {
        fireEvent(true, StatusConstants.NOT_CONNECTED);
    }

    protected void setStartedCrafting() {
        this.startedCrafting = true;
        fireEvent(false, StatusConstants.CRAFTING_STARTED);
    }

    protected void setJobCanceled() {
        this.isJobCanceled = true;
        this.jobDoneTime = System.currentTimeMillis();
    }

    protected void setJobDone() {
        this.isJobDone = true;
        this.jobDoneTime = System.currentTimeMillis();
    }

    protected void fireEvent(boolean error, StatusConstants message) {
        this.computer.queueEvent(eventPrefix + EVENT_SUFFIX, error, this.id, message.toString());
        this.debugMessage = message.toString();
        this.errorOccurred = error;
    }

    protected void fireEvent(boolean error, String message) {
        this.computer.queueEvent(eventPrefix + EVENT_SUFFIX, error, this.id, message);
        this.debugMessage = message;
        this.errorOccurred = error;
    }

    public void tick() {
        startCalculation();
        maybeCraft();
    }

    protected abstract void maybeCraft();

    protected abstract void startCalculation();

    public abstract void jobStateChanged();
}
