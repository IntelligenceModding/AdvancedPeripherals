package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;

public abstract class ProxyIntegration implements IPeripheral {

    private Class<?> targetClass = getTargetClass();

    protected abstract Class<?> getTargetClass();

    protected abstract ProxyIntegration getNewInstance();

    protected abstract String getName();

    public boolean isTileEntity(TileEntity tileEntity) {
        if(targetClass.isAssignableFrom(tileEntity.getClass())) {
            return true;
        }
        return false;
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
