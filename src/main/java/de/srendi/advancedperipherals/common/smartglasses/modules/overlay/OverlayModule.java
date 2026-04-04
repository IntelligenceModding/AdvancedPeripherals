package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.toclient.OverlayModuleClientRequestPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectBulkSyncPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectClearPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectDeletePacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectSyncPacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayModule implements IModule {
    private static final ResourceLocation ID = AdvancedPeripherals.getRL("overlay");

    public final ConcurrentHashMap<Integer, RenderableObject> objects = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, RenderableObject> objectsToUpdate = new ConcurrentHashMap<>();
    private final SmartGlassesSideAccess access;

    public boolean autoUpdate = true;
    private int idCounter = 0;

    private WeakReference<ServerPlayer> lastPlayer = null;

    private int screenWidth = 0;
    private int screenHeight = 0;
    private double guiScale = 1;

    public OverlayModule(SmartGlassesSideAccess access) {
        this.access = access;
    }

    private ServerPlayer getOwner() {
        return this.access.getEntity() instanceof ServerPlayer player ? player : null;
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public IModuleFunctions getFunctions(SmartGlassesSideAccess access) {
        return new OverlayGlassesFunctions(this, access);
    }

    @Override
    public void serverTick(SmartGlassesSideAccess access) {
        ServerPlayer player = this.getOwner();
        if (player != null && (this.lastPlayer == null || this.lastPlayer.get() != player)) {
            this.lastPlayer = new WeakReference<>(player);
            PacketDistributor.sendToPlayer(player, new OverlayModuleClientRequestPacket());
        }
    }

    public void setScreenSizes(int screenWidth, int screenHeight, double guiScale) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.guiScale = guiScale;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public double getGuiScale() {
        return guiScale;
    }

    public SmartGlassesSideAccess getAccess() {
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
            ServerPlayer owner = this.getOwner();
            PacketDistributor.sendToPlayer(owner, new RenderableObjectSyncPacket(owner.getUUID(), object));
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
        RenderableObject removedUpdating = objectsToUpdate.remove(id);
        if (removed == null) {
            return removedUpdating != null;
        }
        PacketDistributor.sendToPlayer(this.getOwner(), new RenderableObjectDeletePacket(id));
        return true;
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
        PacketDistributor.sendToPlayer(this.getOwner(), new RenderableObjectClearPacket());
        return size;
    }

    /**
     * Just sends a sync package to the client, this method should only be called from the setter lua functions from our objects
     *
     * @param object the object to sync to the player
     */
    public void update(RenderableObject object) {
        if (autoUpdate) {
            ServerPlayer owner = this.getOwner();
            PacketDistributor.sendToPlayer(owner, new RenderableObjectSyncPacket(owner.getUUID(), object));
            return;
        }

        objectsToUpdate.put(object.getId(), object);
    }

    public int bulkUpdate() {
        ServerPlayer owner = this.getOwner();
        int maxPackets = 15000;
        int size = objectsToUpdate.size();
        int packetCount = (size + maxPackets - 1) / maxPackets;

        List<RenderableObject> packedObjects = new ArrayList<>();
        // In some cases, if the user creates a lot of objects above 15k, the packet payload can be too big.
        // We split up the packets for every 15k objects to prevent the payload limit from mc
        for (int i = 0; i < packetCount; i++) {
            int count = 0;
            packedObjects.clear();
            for (RenderableObject object : objectsToUpdate.values()) {
                packedObjects.add(object);
                objects.put(object.getId(), object);
                objectsToUpdate.remove(object.getId());
                count++;

                if (count >= maxPackets) {
                    break; // Ensure we don't exceed the packet size limit
                }
            }

            PacketDistributor.sendToPlayer(owner, new RenderableObjectBulkSyncPacket(owner.getUUID(), List.copyOf(packedObjects)));
        }

        return size;
    }
}
