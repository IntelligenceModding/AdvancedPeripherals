package de.srendi.advancedperipherals.api.peripherals.owner;

@SuppressWarnings("InstantiationOfUtilityClass")
public class PeripheralOwnerAbility<T> {
    public static final PeripheralOwnerAbility<FuelAbility> FUEL = new PeripheralOwnerAbility<>();
    public static final PeripheralOwnerAbility<OperationAbility> OPERATION = new PeripheralOwnerAbility<>();

    private PeripheralOwnerAbility(){}
}
