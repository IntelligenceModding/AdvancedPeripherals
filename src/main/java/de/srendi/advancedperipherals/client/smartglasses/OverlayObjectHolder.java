package de.srendi.advancedperipherals.client.smartglasses;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static holder for client side renderable objects - will change
 */
public class OverlayObjectHolder {

    public static Map<Integer, OverlayObject> objects = new ConcurrentHashMap<>();

    public static OverlayObject getObject(int id) {
        return objects.get(id);
    }

    public static void putObject(OverlayObject object) {
        objects.put(object.getId(), object);
    }

    public static void putObjects(Collection<OverlayObject> objects) {
        for (OverlayObject overlayObject : objects) {
            putObject(overlayObject);
        }
    }

    public static void removeObject(int id) {
        OverlayObject obj = objects.remove(id);
        if (obj instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                throw new RuntimeException("Exception when releasing overlay object " + obj.toString(), e);
            }
        }
    }

    public static Collection<OverlayObject> getObjects() {
        return objects.values();
    }

    public static void clear() {
        List<Map.Entry<Integer, OverlayObject>> entries = List.copyOf(objects.entrySet());
        objects.entrySet().removeAll(entries);
        for (Map.Entry<Integer, OverlayObject> entry : entries) {
            OverlayObject obj = entry.getValue();
            if (obj instanceof AutoCloseable closeable) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    throw new RuntimeException("Exception when releasing overlay object " + obj.toString(), e);
                }
            }
        }
    }
}
