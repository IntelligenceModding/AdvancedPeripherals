package de.srendi.advancedperipherals.lib.peripherals;

import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.Nullable;

public abstract class TileEntityIntegrationPeripheral<T extends TileEntity> extends IntegrationPeripheral {

    protected final T tileEntity;

    public TileEntityIntegrationPeripheral(TileEntity entity) {
        super();
        this.tileEntity = (T) entity;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return tileEntity;
    }
}
