package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import org.jetbrains.annotations.NotNull;

public class TurtleFuelAbility extends FuelAbility<TurtlePeripheralOwner> {
    private final int maxFuelConsumptionLevel;

    public TurtleFuelAbility(@NotNull TurtlePeripheralOwner owner, int maxFuelConsumptionLevel) {
        super(owner);
        this.maxFuelConsumptionLevel = maxFuelConsumptionLevel;
    }

    @Override
    protected boolean consumeFuel(int count) {
        return owner.turtle.consumeFuel(count);
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return maxFuelConsumptionLevel;
    }

    @Override
    public boolean isFuelConsumptionDisable() {
        return !owner.getTurtle().isFuelNeeded();
    }

    @Override
    public int getFuelCount() {
        return owner.turtle.getFuelLevel();
    }

    @Override
    public int getFuelMaxCount() {
        return owner.turtle.getFuelLimit();
    }

    @Override
    public void addFuel(int count) {
        owner.turtle.addFuel(count);
    }
}
