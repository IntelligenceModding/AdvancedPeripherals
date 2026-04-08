package de.srendi.advancedperipherals.client.smartglasses;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static holder for client side renderable objects - will change
 */
public class OverlayObjectHolder {

    public static Map<Integer, OverlayObject> objects = new ConcurrentHashMap<>();

    public static void addOrUpdateObject(OverlayObject object) {
        objects.put(object.getId(), object);
    }

    public static void addOrUpdateObjects(Collection<OverlayObject> objects) {
        for (OverlayObject overlayObject : objects) {
            addOrUpdateObject(overlayObject);
        }
    }

    public static void removeObject(int id) {
        objects.remove(id);
    }

    public static Collection<OverlayObject> getObjects() {
        return objects.values();
    }

    public static void clear() {
        objects.clear();
    }
}
