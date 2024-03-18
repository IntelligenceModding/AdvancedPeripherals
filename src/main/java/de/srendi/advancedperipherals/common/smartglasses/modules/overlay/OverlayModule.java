package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CopyOnWriteArraySet;


/**
 * We want to support scripts which were made for the plethora classes. So we call this item the same as the overlay item from plethora
 * We'll first add our own implementation for a rendering system and then add the API endpoints for plethora scripts
 */
public class OverlayModule implements IModule {

    public final CopyOnWriteArraySet<OverlayObject> objects = new CopyOnWriteArraySet<>();
    public final SmartGlassesAccess access;

    public OverlayModule(SmartGlassesAccess access) {
        this.access = access;
    }

    @Override
    public ResourceLocation getName() {
        return AdvancedPeripherals.getRL("glasses");
    }

    @Override
    public IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess) {
        return new OverlayGlassesFunctions(this);
    }

    @Override
    public void tick(@NotNull SmartGlassesAccess smartGlassesAccess) {
        /*
        Entity entity = smartGlassesAccess.getEntity();
        if (entity != null && entity.getLevel().getGameTime() % 20 == 0)
            AdvancedPeripherals.LOGGER.info("I'm an overlay module! And I'm alive!");
        */
    }

    public SmartGlassesAccess getAccess() {
        return access;
    }

    public CopyOnWriteArraySet<OverlayObject> getObjects() {
        return objects;
    }

    /**
     * Adds an object to the module. If the object already exists, it will return the object and stop proceeding
     *
     * @param object The object which should be added
     * @return A pair of the object and a boolean. The boolean is true if the object was added successfully and false if not.
     * The object is the object which was added or the object which already exists(When not successful).
     */
    public Pair<OverlayObject, Boolean> addObject(OverlayObject object) {
        for (OverlayObject overlayObject : objects) {
            if (overlayObject.getId().equals(object.getId()))
                return Pair.of(overlayObject, false);
        }
        objects.add(object);
        return Pair.of(object, true);
    }
}
