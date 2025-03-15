package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.base.BaseDetectorEntity;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

public abstract class BaseDetectorPeripheral<E extends BaseDetectorEntity> extends BasePeripheral<BlockEntityPeripheralOwner<E>> {
    protected BaseDetectorPeripheral(String type, E tileEntity) {
        super(type, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @LuaFunction
    public final long getMaxTransferRate() {
        return owner.tileEntity.getMaxTransferRate();
    }

    @LuaFunction
    public final long getTransferRateLimit() {
        return owner.tileEntity.getTransferRateLimit();
    }

    @LuaFunction
    public final void setTransferRateLimit(long transferRate) {
        owner.tileEntity.setTransferRateLimit(transferRate);
    }

    @LuaFunction
    public final long getTransferRate() {
        return owner.tileEntity.getTransferRate();
    }

    @LuaFunction
    public final String getLastTransferedId() {
        return owner.tileEntity.getLastTransferedId();
    }

    @LuaFunction
    public final String getReadyTransferId() {
        return owner.tileEntity.getReadyTransferId();
    }
}
