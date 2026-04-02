package de.srendi.advancedperipherals.common.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RegistryUtil {
    @Nullable
    public static <T> T getRegistryEntry(String name, Registry<T> forgeRegistry) {
        ResourceLocation location = ResourceLocation.tryParse(name);

        if (location == null || !forgeRegistry.containsKey(location)) {
            return null;
        }
        return forgeRegistry.get(location);
    }

}
