package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicLong;

public abstract class BasicCraftJob {

    protected static final String CALCULATION_STARTED = "CALCULATION_STARTED";
    protected static final String CRAFTING_STARTED = "CRAFTING_STARTED";
    protected static final String JOB_CANCELED = "JOB_CANCELED";
    protected static final String JOB_DONE = "JOB_DONE";
    protected static final String NOT_CRAFTABLE = "NOT_CRAFTABLE";
    protected static final String MISSING_ITEMS = "MISSING_ITEMS";
    protected static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
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
    protected String debugMessage = "";

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
    public final boolean hasDebugMessage() {
        return !debugMessage.isEmpty();
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

    @LuaFunction(value = "getProgress")
    public final long getProgressLua() {
        return getProgress();
    }

    public abstract Object getParsedRequestedItem();

    public abstract long getElapsedTime();

    public abstract long getTotalItems();

    public abstract long getProgress();

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
        fireEvent(false, false, true, false, false, "not connected");
    }

    public void setStartedCrafting() {
        this.startedCrafting = true;
        fireEvent(true, true, false, false, false, CRAFTING_STARTED);
    }

    public void setJobCanceled() {
        this.isJobCanceled = true;
        this.jobDoneTime = System.currentTimeMillis();
    }

    public void setJobDone() {
        this.isJobDone = true;
        this.jobDoneTime = System.currentTimeMillis();
    }

    protected void fireEvent(boolean calculationStarted, boolean craftingStarted, boolean isDone, boolean wasCanceled, boolean error, String message) {
        this.computer.queueEvent(eventName + EVENT, calculationStarted, craftingStarted, isDone, wasCanceled, error, this.id, message);
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
