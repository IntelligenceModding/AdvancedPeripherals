package de.srendi.advancedperipherals.common.smartglasses;

import net.minecraft.network.chat.Component;

public enum SlotType {
    PERIPHERALS(Component.translatable("text.advancedperipherals.smart_glasses.peripherals")),
    MODULES(Component.translatable("text.advancedperipherals.smart_glasses.modules"));

    private final Component name;

    SlotType(Component name) {
        this.name = name;
    }

    public Component getName() {
        return name;
    }

    public static SlotType defaultType() {
        return PERIPHERALS;
    }
}
