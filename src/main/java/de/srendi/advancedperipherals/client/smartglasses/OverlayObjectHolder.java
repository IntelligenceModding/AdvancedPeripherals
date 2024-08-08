package de.srendi.advancedperipherals.client.smartglasses;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectDecodeRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.CircleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.ItemObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RectangleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.TextObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Static holder for client side renderable objects - will change
 */
public class OverlayObjectHolder {

    public static Map<Integer, RenderableObject> objects = new HashMap<>();

    public static void addOrUpdateObject(RenderableObject object) {
        objects.put(object.getId(), object);
    }

    public static void addOrUpdateObjects(Collection<RenderableObject> objects) {
        for (RenderableObject renderableObject : objects) {
            addOrUpdateObject(renderableObject);
        }
    }

    public static void removeObject(int id) {
        objects.remove(id);
    }

    public static Collection<RenderableObject> getObjects() {
        return objects.values();
    }

    public static void clear() {
        objects.clear();
    }

    public static void registerDecodeObjects() {
        ObjectDecodeRegistry.register(RectangleObject.TYPE_ID, RectangleObject::decode);
        ObjectDecodeRegistry.register(CircleObject.TYPE_ID, CircleObject::decode);
        ObjectDecodeRegistry.register(TextObject.TYPE_ID, TextObject::decode);
        ObjectDecodeRegistry.register(ItemObject.TYPE_ID, ItemObject::decode);
    }
}
