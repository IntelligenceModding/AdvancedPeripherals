package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public abstract class AutomataCorePeripheral extends FuelConsumingPeripheral {

    protected static final String FUEL_CONSUMING_RATE_SETTING = "FUEL_CONSUMING_RATE";
    protected static final int DEFAULT_FUEL_CONSUMING_RATE = 1;
    // So, we storing here turtle and side
    // Because this peripheral are only for turtles
    protected final ITurtleAccess turtle;
    protected final TurtleSide side;

    public AutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
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

    @Override
    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> data = super.getPeripheralConfiguration();
        data.put("interactionRadius", getInteractionRadius());
        return data;
    }

    public abstract IAutomataCoreTier getTier();

    public final int getInteractionRadius() {
        return getTier().getInteractionRadius();
    }

    @Override
    protected final int getMaxFuelConsumptionRate() {
        return getTier().getMaxFuelConsumptionRate();
    }
}
