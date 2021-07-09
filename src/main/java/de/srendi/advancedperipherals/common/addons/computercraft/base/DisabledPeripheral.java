package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class DisabledPeripheral extends BasePeripheral {
    public final static DisabledPeripheral INSTANCE = new DisabledPeripheral("disabledPeripheral", (ITurtleAccess) null);

    public DisabledPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public DisabledPeripheral(String type, TileEntity tileEntity) {
        super(type, tileEntity);
    }

    public DisabledPeripheral(String type, Entity entity) {
        super(type, entity);
    }

    public DisabledPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
