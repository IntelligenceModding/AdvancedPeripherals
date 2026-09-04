package de.srendi.advancedperipherals.common.smartglasses;

import de.srendi.advancedperipherals.common.setup.APTranslations;
import net.minecraft.network.chat.Component;

public enum SlotType {
    PERIPHERALS(Component.translatable(APTranslations.SMART_GLASSES_PERIPHERALS)),
    MODULES(Component.translatable(APTranslations.SMART_GLASSES_MODULES));

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
