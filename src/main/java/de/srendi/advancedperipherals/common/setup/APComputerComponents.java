package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.component.ComputerComponent;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;

import java.util.function.Supplier;

public final class APComputerComponents {
    private APComputerComponents() {}

    public static final ComputerComponent<Supplier<SmartGlassesComputer>> SMARTGLASSES_EQUIPPED = ComputerComponent.create(AdvancedPeripherals.MOD_ID, "smartglasses_equipped");
}
