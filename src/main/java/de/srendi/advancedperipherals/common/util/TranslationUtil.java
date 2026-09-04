package de.srendi.advancedperipherals.common.util;

import net.minecraft.resources.ResourceLocation;

public class TranslationUtil {
    public static String turtle(ResourceLocation id) {
        return "turtle." + id.getNamespace() + "." + id.getPath();
    }

    public static String pocket(ResourceLocation id) {
        return "pocket." + id.getNamespace() + "." + id.getPath();
    }

    public static String tooltip(String descriptionId) {
        return descriptionId + ".tooltip";
    }
}
