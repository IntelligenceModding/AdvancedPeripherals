package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IAutomataCoreTier;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
        CompoundTag settings = owner.getDataStorage();
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

    protected AABB getBox(BlockPos pos, int radius) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return new AABB(
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

    @Nonnull
    public MethodResult fuelErrorCallback(MethodResult fuelErrorResult) {
        return fuelErrorResult;
    }

    protected SingleOperationContext forUnknownDistance() {
        return new SingleOperationContext(1, getInteractionRadius());
    }

    protected SingleOperationContext toDistance(BlockPos pos) {
        return new SingleOperationContext(1, getPos().distManhattan(pos));
    }

    protected <T> MethodResult withOperation(IPeripheralOperation<T> operation, T context, Function<T, MethodResult> function, @Nullable Function<T, Optional<MethodResult>> checkBeforeFuel) {
        Optional<MethodResult> checkResults = cooldownCheck(operation);
        if (checkResults.isPresent()) return checkResults.get();
        if (checkBeforeFuel != null) {
            checkResults = checkBeforeFuel.apply(context);
            if (checkResults.isPresent()) return checkResults.get();
        }
        checkResults = consumeFuelOp(operation, context);
        if (checkResults.isPresent()) return checkResults.map(this::fuelErrorCallback).get();
        addRotationCycle();
        MethodResult functionResult = function.apply(context);
        trackOperation(operation, context);
        return functionResult;
    }

    protected MethodResult withOperation(SingleOperation operation, Function<SingleOperationContext, MethodResult> function) {
        return withOperation(operation, forUnknownDistance(), function, null);
    }

    protected MethodResult withOperation(SingleOperation operation, Function<SingleOperationContext, MethodResult> function, @Nonnull Function<SingleOperationContext, Optional<MethodResult>> checkBeforeFuel) {
        return withOperation(operation, forUnknownDistance(), function, checkBeforeFuel);
    }
}
