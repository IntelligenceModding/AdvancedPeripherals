package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.network.chat.TranslatableComponent;

public class TranslationUtil {

    public static TranslatableComponent itemTooltip(String descriptionId) {
        int lastIndex = descriptionId.lastIndexOf('.');
        return new TranslatableComponent(String.format(
                "%s.tooltip.%s",
                descriptionId.substring(0, lastIndex).replaceFirst("^block", "item"),
                descriptionId.substring(lastIndex + 1)
        ));
    }

    public static String turtle(String name) {
        return String.format("turtle.%s.%s", AdvancedPeripherals.MOD_ID, name);
    }

    public static String pocket(String name) {
        return String.format("pocket.%s.%s", AdvancedPeripherals.MOD_ID, name);
    }
}
