package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class MechanicSoulPeripheral extends FuelConsumingPeripheral {

    public MechanicSoulPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    public void addRotationCycle() {
        addRotationCycle(1);
    }

    public void addRotationCycle(int count) {
        DataStorageUtil.RotationCharge.addCycles(owner, count);
        owner.triggerClientServerSync();
    }

    protected AxisAlignedBB getBox(BlockPos pos, int radius) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new AxisAlignedBB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );
    }
}
