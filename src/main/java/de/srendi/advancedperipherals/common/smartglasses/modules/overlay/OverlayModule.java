package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectBulkSyncPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectClearPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectDeletePacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectSyncPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * We want to support scripts which were made for the plethora classes. So we call this item the same as the overlay item from plethora
 * We'll first add our own implementation for a rendering system and then add the API endpoints for plethora scripts
 */
public class OverlayModule implements IModule {

    public final ConcurrentHashMap<Integer, RenderableObject> objects = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, RenderableObject> objectsToUpdate = new ConcurrentHashMap<>();
    public final SmartGlassesAccess access;

    public boolean autoUpdate = true;
    private int idCounter = 0;

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

    public Map<Integer, RenderableObject> getObjects() {
        return objects;
    }

    /**
     * Adds an object to the module. If the object already exists, it will return the object and stop proceeding
     *
     * @param object The object which should be added
     * @return A pair of the object and a boolean. The boolean is true if the object was added successfully and false if not.
     * The object is the object which was added or the object which already exists(When not successful).
     */
    public RenderableObject addObject(RenderableObject object) {
        int id = idCounter++;
        object.setId(id);
        if (autoUpdate) {
            APNetworking.sendTo(new RenderableObjectSyncPacket(object), (ServerPlayer) access.getEntity());
            objects.put(id, object);
        } else {
            objectsToUpdate.put(id, object);
        }
        return object;
    }

    /**
     * Removes an object from the module if it exists and updates the client.
     *
     * @param id the object id
     * @return true if the object existed and was removed, false if the object was not in the collection
     */
    public boolean removeObject(int id) {
        RenderableObject removed = objects.remove(id);

        if (removed != null)
            APNetworking.sendTo(new RenderableObjectDeletePacket(id), (ServerPlayer) access.getEntity());

        return removed != null;
    }

    /**
     * Removes all objects from the module
     *
     * @return the amount of objects cleared
     */
    public int clear() {
        int size = objects.size();
        objects.clear();
        idCounter = 0;
        objectsToUpdate.clear();
        APNetworking.sendTo(new RenderableObjectClearPacket(), (ServerPlayer) access.getEntity());
        return size;
    }

    /**
     * Just sends a sync package to the client, this method should only be called from the setter lua functions from our objects
     *
     * @param object the object to sync to the player
     */
    public void update(RenderableObject object) {
        if (autoUpdate) {
            APNetworking.sendTo(new RenderableObjectSyncPacket(object), (ServerPlayer) access.getEntity());
            return;
        }

        objectsToUpdate.put(object.getId(), object);
    }


    public int bulkUpdate() {
        int size = objectsToUpdate.size();
        int packetCount = (int) Math.ceil((double) size / 15000);

        // In some cases, if the user creates a lot of objects above 15k, the packet payload can be too big.
        // We split up the packets for every 15k objects to prevent the payload limit from mc
        for (int i = 0; i < packetCount; i++) {
            List<RenderableObject> packetObjects = new ArrayList<>();
            int count = 0;

            for (RenderableObject object : objectsToUpdate.values()) {
                packetObjects.add(object);
                objects.put(object.getId(), object);
                objectsToUpdate.remove(object.getId());
                count++;

                if (count >= 15000) {
                    break; // Ensure we don't exceed the packet size limit
                }
            }

            APNetworking.sendTo(new RenderableObjectBulkSyncPacket(packetObjects),
                    (ServerPlayer) access.getEntity()
            );
        }

        return size;
    }
}
