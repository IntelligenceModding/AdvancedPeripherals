package de.srendi.advancedperipherals.lib.peripherals.owner;

@SuppressWarnings("InstantiationOfUtilityClass")
public class PeripheralOwnerAbility<T> {
    public static final PeripheralOwnerAbility<FuelAbility<?>> FUEL = new PeripheralOwnerAbility<>();
    public static final PeripheralOwnerAbility<OperationAbility> OPERATION = new PeripheralOwnerAbility<>();

    public PeripheralOwnerAbility() {
    }
}
