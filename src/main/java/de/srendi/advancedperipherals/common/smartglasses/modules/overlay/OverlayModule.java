package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectAddPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectBulkAddPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectBulkSyncPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectClearPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectDeletePacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectSyncPacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayModule implements IModule {
    private static final ResourceLocation ID = AdvancedPeripherals.getRL("overlay");

    private final ConcurrentHashMap<Integer, OverlayObject> objects = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, OverlayObject> objectsToAdd = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, OverlayObject> objectsToUpdate = new ConcurrentHashMap<>();
    private final SmartGlassesSideAccess access;

    private boolean equipped = false;
    private boolean autoUpdate = true;
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
        if (!access.getComputer().isEquipped()) {
            return;
        }
        if (!this.equipped) {
            this.equipped = true;
            this.sendAllObjects();
        }
        if (this.autoUpdate) {
            this.bulkUpdate();
        }
    }

    @Override
    public void onUnequipped(SmartGlassesSideAccess smartGlassesAccess) {
        this.equipped = false;
        PacketDistributor.sendToPlayer(this.getOwner(), new RenderableObjectClearPacket());
    }

    public void setScreenSizes(int screenWidth, int screenHeight, double guiScale) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.guiScale = guiScale;
        this.access.getComputer().queueEvent(CCEvents.OVERLAY_RESIZE);
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

    public Map<Integer, OverlayObject> getObjects() {
        return this.objects;
    }

    public boolean getAutoUpdate() {
        return this.autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        if (this.autoUpdate == autoUpdate) {
            return;
        }
        this.autoUpdate = autoUpdate;
    }

    /**
     * Adds an object to the module.
     *
     * @param object The object which should be added
     */
    public void addObject(OverlayObject object) {
        int id = ++idCounter;
        object.setId(id);
        // if (this.autoUpdate) {
        //     ServerPlayer owner = this.getOwner();
        //     PacketDistributor.sendToPlayer(owner, new RenderableObjectAddPacket(owner.getUUID(), object));
        //     this.objects.put(id, object);
        //     return;
        // }
        this.objectsToAdd.put(id, object);
    }

    /**
     * Removes an object from the module if it exists and updates the client.
     *
     * @param id the object id
     * @return true if the object existed and was removed, false if the object was not in the collection
     */
    public boolean removeObject(int id) {
        boolean removed = this.objects.remove(id) != null;
        boolean removedAdding = this.objectsToAdd.remove(id) != null;
        this.objectsToUpdate.remove(id);
        if (!removed) {
            return removedAdding;
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
        int size = this.objects.size();
        this.objects.clear();
        this.idCounter = 0;
        this.objectsToAdd.clear();
        this.objectsToUpdate.clear();
        PacketDistributor.sendToPlayer(this.getOwner(), new RenderableObjectClearPacket());
        return size;
    }

    /**
     * Just sends a sync package to the client, this method should only be called from the setter lua functions from our objects
     *
     * @param object the object to sync to the player
     */
    public void update(OverlayObject object) {
        // if (this.autoUpdate) {
        //     PacketDistributor.sendToPlayer(this.getOwner(), new RenderableObjectSyncPacket(object));
        //     return;
        // }
        if (!this.objectsToAdd.containsKey(object.getId())) {
            this.objectsToUpdate.put(object.getId(), object);
        }
    }

    private void sendAllObjects() {
        ServerPlayer owner = this.getOwner();
        int maxPackets = 10000;
        List<OverlayObject> packedObjects = new ArrayList<>();
        Iterator<OverlayObject> iter = this.objects.values().iterator();
        while (iter.hasNext()) {
            packedObjects.clear();
            do {
                packedObjects.add(iter.next());
            } while (iter.hasNext() && packedObjects.size() < maxPackets);
            PacketDistributor.sendToPlayer(owner, new RenderableObjectBulkAddPacket(owner.getUUID(), List.copyOf(packedObjects)));
        }
    }

    public int bulkUpdate() {
        ServerPlayer owner = this.getOwner();
        int maxPackets = 10000;
        int count = 0;
        List<OverlayObject> packedObjects = new ArrayList<>();
        {
            // In some cases, if the user creates a lot of objects, the packet payload can be too big.
            // We split up the packets for every 10k objects to prevent the payload limit from mc
            int remaining = this.objectsToAdd.size();
            count += remaining;
            while (remaining > 0) {
                packedObjects.clear();
                for (OverlayObject object : this.objectsToAdd.values()) {
                    packedObjects.add(object);
                    remaining--;
                    this.objects.put(object.getId(), object);
                    this.objectsToAdd.remove(object.getId());
                    if (packedObjects.size() >= maxPackets) {
                        break; // Ensure we don't exceed the packet size limit
                    }
                }
                PacketDistributor.sendToPlayer(owner, new RenderableObjectBulkAddPacket(owner.getUUID(), List.copyOf(packedObjects)));
            }
        }
        {
            int remaining = this.objectsToUpdate.size();
            count += remaining;
            while (remaining > 0) {
                packedObjects.clear();
                for (OverlayObject object : this.objectsToUpdate.values()) {
                    packedObjects.add(object);
                    remaining--;
                    this.objectsToUpdate.remove(object.getId());
                    if (packedObjects.size() >= maxPackets) {
                        break;
                    }
                }
                PacketDistributor.sendToPlayer(owner, new RenderableObjectBulkSyncPacket(List.copyOf(packedObjects)));
            }
        }

        return count;
    }
}
