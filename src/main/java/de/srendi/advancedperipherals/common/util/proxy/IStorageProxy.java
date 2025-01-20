package de.srendi.advancedperipherals.common.util.proxy;

import org.jetbrains.annotations.Nullable;

public interface IStorageProxy {
    long getMaxTransferRate();

    long getTransferRate();

    void setTransferRate(long rate);

    long getAndResetTransfered();

    @Nullable
    String getLastTransferedId();

    @Nullable
    String getReadyTransferId();
}
