package de.srendi.advancedperipherals.common.util.proxy;

public abstract class AbstractStorageProxy implements IStorageProxy {
    private final long maxTransferRate;
    private volatile long transferRate;
    private long transfered = 0;

    protected AbstractStorageProxy(long maxTransferRate) {
        this.maxTransferRate = maxTransferRate;
        this.transferRate = maxTransferRate;
    }

    @Override
    public long getMaxTransferRate() {
        return this.maxTransferRate;
    }

    @Override
    public long getTransferRate() {
        return this.transferRate;
    }

    @Override
    public void setTransferRate(long rate) {
        this.transferRate = Math.min(rate, this.maxTransferRate);
    }

    protected void resetStatus() {
        this.transfered = 0;
    }

    @Override
    public long getAndResetTransfered() {
        long transfered = this.transfered;
        this.resetStatus();
        return transfered;
    }

    protected void onTransfered(long amount) {
        this.transfered += amount;
    }
}
