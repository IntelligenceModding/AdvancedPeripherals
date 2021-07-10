package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class DisabledPeripheral extends BasePeripheral {
    public final static DisabledPeripheral INSTANCE = new DisabledPeripheral("disabledPeripheral", null, TurtleSide.LEFT);

    public DisabledPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public DisabledPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
