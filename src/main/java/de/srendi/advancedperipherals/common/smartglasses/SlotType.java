package de.srendi.advancedperipherals.common.smartglasses;

import net.minecraft.network.chat.Component;

public enum SlotType {
    PERIPHERALS(Component.literal("Peripherals")),
    MODULES(Component.literal("Modules"));

    private Component name;

    SlotType(Component name) {
        this.name = name;
    }

    public Component getName() {
        return name;
    }
}