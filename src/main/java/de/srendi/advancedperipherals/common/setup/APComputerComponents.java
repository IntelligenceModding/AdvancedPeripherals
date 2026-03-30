package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.component.ComputerComponent;
import de.srendi.advancedperipherals.AdvancedPeripherals;

import java.util.function.BooleanSupplier;

public final class APComputerComponents {
    private APComputerComponents() {}

    public static final ComputerComponent<BooleanSupplier> SMARTGLASSES_EQUIPPED = ComputerComponent.create(AdvancedPeripherals.MOD_ID, "smartglasses_equipped");
}
