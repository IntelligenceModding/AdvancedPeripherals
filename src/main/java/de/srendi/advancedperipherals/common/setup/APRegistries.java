package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

public final class APRegistries {
    public static final ResourceKey<Registry<OverlayObjectType<?>>> OVERLAY_OBJECTS = ResourceKey.createRegistryKey(AdvancedPeripherals.getRL("overlay_objects"));

    private APRegistries() {}

    public static final Registry<OverlayObjectType<?>> getOverlayObjectsRegistry() {
        return (Registry<OverlayObjectType<?>>) BuiltInRegistries.REGISTRY.getOrThrow((ResourceKey<Registry<?>>) (ResourceKey<?>) OVERLAY_OBJECTS);
    }
}
