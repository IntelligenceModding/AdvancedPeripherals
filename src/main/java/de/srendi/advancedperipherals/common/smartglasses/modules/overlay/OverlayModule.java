package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.toclient.OverlayModuleClientRequestPacket;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayModule implements IModule {
    private static final ResourceLocation ID = AdvancedPeripherals.getRL("overlay");

    private final Map<Integer, OverlayObject> objects = new ConcurrentHashMap<>();
    private final Map<Integer, OverlayObject> objectsToAdd = new ConcurrentHashMap<>();
    private final Map<Integer, OverlayObject> objectsToUpdate = new ConcurrentHashMap<>();
    private final SmartGlassesSideAccess access;

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
        ServerPlayer player = this.getOwner();
        if (player != null && (this.lastPlayer == null || this.lastPlayer.get() != player)) {
            this.lastPlayer = new WeakReference<>(player);
            PacketDistributor.sendToPlayer(player, new OverlayModuleClientRequestPacket());
        }
    }

    @Override
    public void onUnequipped(SmartGlassesSideAccess smartGlassesAccess) {
        this.clear();
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
        if (autoUpdate) {
            this.bulkUpdate();
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
        if (this.autoUpdate) {
            ServerPlayer owner = this.getOwner();
            PacketDistributor.sendToPlayer(owner, new RenderableObjectAddPacket(owner.getUUID(), object));
            this.objects.put(id, object);
            return;
        }
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
        if (this.autoUpdate) {
            PacketDistributor.sendToPlayer(this.getOwner(), new RenderableObjectSyncPacket(object));
            return;
        }
        if (!this.objectsToAdd.containsKey(object.getId())) {
            this.objectsToUpdate.put(object.getId(), object);
        }
    }

    public int bulkUpdate() {
        ServerPlayer owner = this.getOwner();
        int maxPackets = 15000;
        int count = 0;
        List<OverlayObject> packedObjects = new ArrayList<>();
        {
            // In some cases, if the user creates a lot of objects above 15k, the packet payload can be too big.
            // We split up the packets for every 15k objects to prevent the payload limit from mc
            int remaning = this.objectsToAdd.size();
            count += remaning;
            while (remaning > 0) {
                packedObjects.clear();
                for (OverlayObject object : this.objectsToAdd.values()) {
                    packedObjects.add(object);
                    remaning--;
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
            int remaning = this.objectsToUpdate.size();
            count += remaning;
            while (remaning > 0) {
                packedObjects.clear();
                for (OverlayObject object : this.objectsToUpdate.values()) {
                    packedObjects.add(object);
                    remaning--;
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
