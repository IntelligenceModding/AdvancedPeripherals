package de.srendi.advancedperipherals.lib.peripherals.owner;

import dan200.computercraft.ComputerCraft;
import org.jetbrains.annotations.NotNull;

public class TurtleFuelAbility extends FuelAbility<TurtlePeripheralOwner> {
    private final int maxFuelConsumptionLevel;

    public TurtleFuelAbility(@NotNull TurtlePeripheralOwner owner, int maxFuelConsumptionLevel) {
        super(owner);
        this.maxFuelConsumptionLevel = maxFuelConsumptionLevel;
    }

    @Override
    protected boolean _consumeFuel(int count) {
        return owner.turtle.consumeFuel(count);
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return maxFuelConsumptionLevel;
    }

    @Override
    public boolean isFuelConsumptionDisable() {
        return !ComputerCraft.turtlesNeedFuel;
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
