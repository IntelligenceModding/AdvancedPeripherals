package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BasicCraftJob {

    private static final String EVENT_SUFFIX = "_crafting";
    private static final int JOB_DONE_PURGE_TIME = 5 * 60 * 1000;

    public static final AtomicInteger ID_SEQ = new AtomicInteger();

    protected final int id;
    protected volatile UUID uuid;
    @Nullable
    protected final IComputerAccess computer;
    protected final String eventPrefix;

    protected boolean startedCalculation = false;
    protected boolean startedCrafting = false;
    protected boolean calculationNotSuccessful = false;
    protected boolean errorOccurred = false;
    protected boolean isJobDone = false;
    protected long jobDoneTime = 0;
    protected boolean isJobCanceled = false;
    protected String debugMessage = null;

    public BasicCraftJob(int id, UUID uuid, IComputerAccess computer, String eventPrefix) {
        this.id = id;
        this.uuid = uuid;
        this.computer = computer;
        this.eventPrefix = eventPrefix;
    }

    public BasicCraftJob(IComputerAccess computer, String eventPrefix) {
        this(ID_SEQ.incrementAndGet(), null, computer, eventPrefix);
    }

    @LuaFunction
    public final int getId() {
        return id;
    }

    @Nullable
    public final UUID getUUID() {
        return this.uuid;
    }

    @LuaFunction("getUUID")
    public final String getUUIDLua() {
        return uuid == null ? null : uuid.toString();
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

    protected abstract Object getParsedRequestedItemImpl();

    @LuaFunction
    public final Object getRequestedItem() {
        return getParsedRequestedItemImpl();
    }

    protected abstract long getElapsedTimeImpl();

    @LuaFunction
    public final double getElapsedTime() {
        return getElapsedTimeImpl();
    }

    protected abstract double getItemProgressImpl();

    @LuaFunction
    public final double getItemProgress() {
        return getItemProgressImpl();
    }

    protected abstract Object getEmittedItemsImpl();

    @LuaFunction
    public final Object getEmittedItems() {
        return getEmittedItemsImpl();
    }

    protected abstract Object getUsedItemsImpl();

    @LuaFunction
    public final Object getUsedItems() {
        return getUsedItemsImpl();
    }

    protected abstract Object getMissingItemsImpl();

    @LuaFunction
    public final Object getMissingItems() {
        return getMissingItemsImpl();
    }

    protected abstract boolean hasMultiplePathsImpl();

    @LuaFunction
    public final boolean hasMultiplePaths() {
        return hasMultiplePathsImpl();
    }

    protected abstract Object getFinalOutputImpl();

    @LuaFunction
    public final Object getFinalOutput() {
        return getFinalOutputImpl();
    }

    protected abstract boolean cancelImpl();

    @LuaFunction
    public final boolean cancel() {
        return cancelImpl();
    }

    public boolean canBePurged() {
        return calculationNotSuccessful || isJobDone || isJobCanceled;
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
        if (this.computer == null) {
            return;
        }
        this.computer.queueEvent(eventPrefix + EVENT_SUFFIX, error, this.id, message.toString());
        this.debugMessage = message.toString();
        this.errorOccurred = error;
    }

    protected void fireEvent(boolean error, String message) {
        if (this.computer == null) {
            return;
        }
        this.computer.queueEvent(eventPrefix + EVENT_SUFFIX, error, this.id, message);
        this.debugMessage = message;
        this.errorOccurred = error;
    }

    public void tick() {
        if (!startedCrafting) {
            if (!startedCalculation) {
                startCalculation();
            }
            maybeCraft();
        }
    }

    protected abstract void startCalculation();

    protected abstract void maybeCraft();

    public abstract void jobStateChanged();
}
