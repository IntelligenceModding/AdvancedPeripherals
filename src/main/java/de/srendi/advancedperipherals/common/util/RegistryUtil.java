package de.srendi.advancedperipherals.common.util;

import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class RegistryUtil {

    public static <T> T getRegistryEntry(String name, Registry<T> forgeRegistry) {
        ResourceLocation location;
        try {
            location = ResourceLocation.parse(name);
        } catch (ResourceLocationException ex) {
            location = null;
        }

        T value;
        if (location != null && forgeRegistry.containsKey(location) && (value = forgeRegistry.get(location)) != null) {
            return value;
        } else {
            return null;
        }
    }

}
