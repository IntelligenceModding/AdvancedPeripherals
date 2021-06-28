package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.Nullable;

public abstract class Integration<T extends TileEntity> implements IPeripheral {

    private final Class<T> targetClass = getTargetClass();

    protected T tileEntity;

    protected abstract Class<T> getTargetClass();

    public abstract Integration<?> getNewInstance();

    public boolean isTileEntity(TileEntity tileEntity) {
        return targetClass.isAssignableFrom(tileEntity.getClass());
    }

    public T getTileEntity() {
        return tileEntity;
    }

    public void setTileEntity(TileEntity tileEntity) {
        this.tileEntity = (T) tileEntity;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return this == iPeripheral;
    }

}
