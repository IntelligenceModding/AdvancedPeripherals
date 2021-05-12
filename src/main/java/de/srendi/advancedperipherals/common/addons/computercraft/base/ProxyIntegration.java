package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;

public abstract class ProxyIntegration<T extends TileEntity> implements IPeripheral {

    protected T tileEntity;

    private final Class<T> targetClass = getTargetClass();

    protected abstract Class<T> getTargetClass();

    protected abstract ProxyIntegration getNewInstance();

    protected abstract String getName();

    public boolean isTileEntity(TileEntity tileEntity) {
        return targetClass.isAssignableFrom(tileEntity.getClass());
    }

    public void setTileEntity(T tileEntity) {
        this.tileEntity = tileEntity;
    }

    public T getTileEntity() {
        return tileEntity;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return this == iPeripheral;
    }

    @NotNull
    @Override
    public String getType() {
        return "peripheralProxy:" + getName();
    }
}
