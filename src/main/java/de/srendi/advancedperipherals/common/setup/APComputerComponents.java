package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.component.ComputerComponent;
import de.srendi.advancedperipherals.AdvancedPeripherals;

public final class APComputerComponents {
	private APComputerComponents() {}

	public static final ComputerComponent<Boolean> SMARTGLASSES = ComputerComponent.create(AdvancedPeripherals.MOD_ID, "smartglasses");
}
