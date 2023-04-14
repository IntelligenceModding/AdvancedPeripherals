package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import org.jetbrains.annotations.NotNull;

/**
 * This is a copy of the FuelAbility class, but with the fuel consumption disabled.
 * This is used for the Pocket Computer.
 */
public class InfinitePocketFuelAbility extends FuelAbility<PocketPeripheralOwner> {

    public InfinitePocketFuelAbility(@NotNull PocketPeripheralOwner owner) {
        super(owner);
    }

    @Override
    protected boolean consumeFuel(int count) {
        return true;
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFuelConsumptionDisable() {
        return true;
    }

    @Override
    public int getFuelCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getFuelMaxCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void addFuel(int count) {
        // Not needed for infinite fuel
    }
}

