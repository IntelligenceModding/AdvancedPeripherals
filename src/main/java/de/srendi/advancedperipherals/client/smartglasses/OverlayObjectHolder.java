package de.srendi.advancedperipherals.client.smartglasses;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectDecodeRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.Circle;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.Panel;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.Text;

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
        ObjectDecodeRegistry.register(Panel.TYPE_ID, Panel::decode);
        ObjectDecodeRegistry.register(Circle.TYPE_ID, Circle::decode);
        ObjectDecodeRegistry.register(Text.TYPE_ID, Text::decode);
    }
}
