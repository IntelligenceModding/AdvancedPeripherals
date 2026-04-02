package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.UUID;
import java.util.function.Function;

public class ObjectFactoryRegistry {
    private static final Int2ObjectMap<Function<UUID, RenderableObject>> objectRegistry = new Int2ObjectOpenHashMap<>();

    public static RenderableObject buildObject(int id, UUID player) {
        Function<UUID, RenderableObject> factory = objectRegistry.get(id);
        if (factory == null) {
            return null;
        }
        return factory.apply(player);
    }

    public static void register(int id, Function<UUID, RenderableObject> factory) {
        objectRegistry.put(id, factory);
    }
}
