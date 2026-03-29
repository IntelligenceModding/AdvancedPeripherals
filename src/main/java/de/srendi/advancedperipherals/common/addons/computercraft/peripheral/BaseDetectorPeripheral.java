package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.base.BaseDetectorEntity;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import org.jetbrains.annotations.NotNull;

public abstract class BaseDetectorPeripheral<E extends BaseDetectorEntity<?, ?, ?>> extends BasePeripheral<BlockEntityPeripheralOwner<E>> {
    protected BaseDetectorPeripheral(String type, @NotNull E blockEntity) {
        super(type, new BlockEntityPeripheralOwner<>(blockEntity));
    }

    @LuaFunction
    public final long getMaxTransferRate() {
        return owner.getBlockEntity().getMaxTransferRate();
    }

    @LuaFunction
    public final long getTransferRateLimit() {
        return owner.getBlockEntity().getTransferRateLimit();
    }

    @LuaFunction
    public final void setTransferRateLimit(long transferRate) {
        owner.getBlockEntity().setTransferRateLimit(transferRate);
    }

    @LuaFunction
    public final long getTransferRate() {
        return owner.getBlockEntity().getTransferRate();
    }

    @LuaFunction
    public final String getLastTransferedId() {
        return owner.getBlockEntity().getLastTransferredId();
    }

    @LuaFunction
    public final String getReadyTransferId() {
        return owner.getBlockEntity().getReadyTransferId();
    }
}
