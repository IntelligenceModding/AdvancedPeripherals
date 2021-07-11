package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class MechanicSoulPeripheral extends FuelConsumingPeripheral {

    // So, we storing here turtle and side
    // Because this peripheral are only for turtles
    private final ITurtleAccess turtle;
    private final TurtleSide side;

    protected static final String FUEL_CONSUMING_RATE_SETTING = "FUEL_CONSUMING_RATE";
    protected static final int DEFAULT_FUEL_CONSUMING_RATE = 1;

    public MechanicSoulPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
        this.turtle = turtle;
        this.side = side;
    }

    public void addRotationCycle() {
        addRotationCycle(1);
    }

    public void addRotationCycle(int count) {
        DataStorageUtil.RotationCharge.addCycles(owner, count);
        owner.triggerClientServerSync();
    }

    @Override
    protected int _getFuelConsumptionRate() {
        CompoundNBT settings = owner.getDataStorage();
        int rate = settings.getInt(FUEL_CONSUMING_RATE_SETTING);
        if (rate == 0) {
            _setFuelConsumptionRate(DEFAULT_FUEL_CONSUMING_RATE);
            return DEFAULT_FUEL_CONSUMING_RATE;
        }
        return rate;
    }

    @Override
    protected void _setFuelConsumptionRate(int rate) {
        owner.getDataStorage().putInt(FUEL_CONSUMING_RATE_SETTING, rate);
    }

    protected AxisAlignedBB getBox(BlockPos pos, int radius) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new AxisAlignedBB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );
    }
}
