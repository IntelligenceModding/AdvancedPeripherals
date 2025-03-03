package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicLong;

public abstract class BasicCraftJob {

    protected static final String EVENT = "_crafting";
    private static final int JOB_DONE_PURGE_TIME = 5 * 60 * 1000;

    public static final AtomicLong ID_SEQ = new AtomicLong();

    protected final long id = ID_SEQ.incrementAndGet();
    protected final IComputerAccess computer;
    protected final String eventName;
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

    public BasicCraftJob(IComputerAccess computer, String eventName, Level world, long amount) {
        this.computer = computer;
        this.eventName = eventName;
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

    @LuaFunction(value = "getRequestedItem")
    public final Object getRequestedItemLua() {
        return getParsedRequestedItem();
    }

    @LuaFunction(value = "getElapsedTime")
    public final long getElapsedTimeLua() {
        return getElapsedTime();
    }

    @LuaFunction(value = "getTotalItems")
    public final long getTotalItemsLua() {
        return getTotalItems();
    }

    @LuaFunction(value = "getItemProgress")
    public final long getItemProgressLua() {
        return getItemProgress();
    }

    @LuaFunction(value = "getEmittedItems")
    public final Object getEmittedItemsLua() {
        return getEmittedItems();
    }

    @LuaFunction(value = "getUsedItems")
    public final Object getUsedItemsLua() {
        return getUsedItems();
    }

    @LuaFunction(value = "getMissingItems")
    public final Object getMissingItemsLua() {
        return getMissingItems();
    }

    @LuaFunction(value = "hasMultiplePaths")
    public final boolean hasMultiplePathsLua() {
        return hasMultiplePaths();
    }

    @LuaFunction(value = "getFinalOutput")
    public final Object getFinalOutputLua() {
        return getFinalOutput();
    }

    @LuaFunction(value = "cancel")
    public final boolean cancelLua() {
        return cancel();
    }

    public abstract Object getParsedRequestedItem();

    public abstract long getElapsedTime();

    public abstract long getTotalItems();

    public abstract long getItemProgress();

    public abstract Object getEmittedItems();

    public abstract Object getUsedItems();

    public abstract Object getMissingItems();

    public abstract boolean hasMultiplePaths();

    public abstract Object getFinalOutput();

    public abstract boolean cancel();

    public Level getWorld() {
        return world;
    }

    public long getAmount() {
        return amount;
    }

    public boolean canBePurged() {
        return calculationNotSuccessful || ((isJobDone || isJobCanceled) && jobDoneTime + JOB_DONE_PURGE_TIME < System.currentTimeMillis());
    }

    protected void fireNotConnected() {
        fireEvent(true, StatusConstants.NOT_CONNECTED);
    }

    public void setStartedCrafting() {
        this.startedCrafting = true;
        fireEvent(false, StatusConstants.CRAFTING_STARTED);
    }

    public void setJobCanceled() {
        this.isJobCanceled = true;
        this.jobDoneTime = System.currentTimeMillis();
    }

    public void setJobDone() {
        this.isJobDone = true;
        this.jobDoneTime = System.currentTimeMillis();
    }

    protected void fireEvent(boolean error, StatusConstants message) {
        this.computer.queueEvent(eventName + EVENT, error, this.id, message.toString());
        this.debugMessage = message.toString();
        this.errorOccurred = error;
    }

    protected void fireEvent(boolean error, String message) {
        this.computer.queueEvent(eventName + EVENT, error, this.id, message);
        this.debugMessage = message;
        this.errorOccurred = error;
    }

    public final void tick() {
        startCalculation();
        maybeCraft();
    }

    protected abstract void maybeCraft();

    protected abstract void startCalculation();

    public abstract void jobStateChanged();
}
