package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ObjectDecodeRegistry {

    private static final Map<Integer, Function<FriendlyByteBuf, RenderableObject>> objectRegistry = new HashMap<>();

    public static RenderableObject getObject(int id, FriendlyByteBuf buf) {
        if (objectRegistry.containsKey(id)) {
            return objectRegistry.get(id).apply(buf);
        }
        return null;
    }

    public static void register(int id, Function<FriendlyByteBuf, RenderableObject> function) {
        objectRegistry.put(id, function);
    }

}
