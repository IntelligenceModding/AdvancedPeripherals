package de.srendi.advancedperipherals.client.smartglasses;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectDecodeRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.Circle;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.Panel;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Static holder for client side renderable objects - will change
 */
public class OverlayObjectHolder {

    public static List<RenderableObject> objects = new ArrayList<>();

    public static void addOrUpdateObject(RenderableObject object) {
        removeObject(object.getId());
        objects.add(object);
    }

    public static void addOrUpdateObjects(RenderableObject[] object) {
        for (RenderableObject renderableObject : object) {
            addOrUpdateObject(renderableObject);
        }
    }

    public static List<RenderableObject> getObjects() {
        return objects;
    }

    public static void removeObject(String id) {
        objects.removeIf(object -> object.getId().equals(id));
    }

    public static void clear() {
        objects.clear();
    }

    public static void registerDecodeObjects() {
        ObjectDecodeRegistry.register(Panel.ID, Panel::decode);
        ObjectDecodeRegistry.register(Circle.ID, Circle::decode);
        ObjectDecodeRegistry.register(Text.ID, Text::decode);
    }
}
