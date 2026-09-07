package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.component.ComputerComponent;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public final class APComputerComponents {
    private APComputerComponents() {}

    public static final ComputerComponent<Supplier<Entity>> SMARTGLASSES_EQUIPPED = ComputerComponent.create(AdvancedPeripherals.MOD_ID, "smartglasses_equipped");
}
