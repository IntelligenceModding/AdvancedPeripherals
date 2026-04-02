package de.srendi.advancedperipherals.client.smartglasses;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectFactoryRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BoxObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.SphereObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TorusObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.ItemObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.LineObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RectangleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.TextObject;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static holder for client side renderable objects - will change
 */
public class OverlayObjectHolder {

    public static Map<Integer, RenderableObject> objects = new ConcurrentHashMap<>();

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
        ObjectFactoryRegistry.register(RectangleObject.TYPE_ID, RectangleObject::new);
        ObjectFactoryRegistry.register(CircleObject.TYPE_ID, CircleObject::new);
        ObjectFactoryRegistry.register(TextObject.TYPE_ID, TextObject::new);
        ObjectFactoryRegistry.register(ItemObject.TYPE_ID, ItemObject::new);
        ObjectFactoryRegistry.register(LineObject.TYPE_ID, LineObject::new);

        ObjectFactoryRegistry.register(BoxObject.TYPE_ID, BoxObject::new);
        ObjectFactoryRegistry.register(BlockObject.TYPE_ID, BlockObject::new);
        ObjectFactoryRegistry.register(SphereObject.TYPE_ID, SphereObject::new);
        ObjectFactoryRegistry.register(TorusObject.TYPE_ID, TorusObject::new);
    }
}
