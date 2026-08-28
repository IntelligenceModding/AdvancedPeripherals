package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectBulkAddPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectBulkSyncPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectClearPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectDeletePacket;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayModule implements IModule {
    public static final ResourceLocation ID = AdvancedPeripherals.getRL("overlay");

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

    @Nullable
    private ServerPlayer getOwner() {
        return this.access.getEntity() instanceof ServerPlayer player ? player : null;
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public String getLuaAlias() {
        return "overlay";
    }

    @Override
    public IModuleFunctions getFunctions(SmartGlassesSideAccess access) {
        return new OverlayGlassesFunctions(this, access);
    }

    @Override
    public void serverTick(SmartGlassesSideAccess access) {
        SmartGlassesComputer computer = access.getComputer();
        if (!computer.isOn()) {
            if (this.equipped) {
                this.equipped = false;
                this.clear();
            }
            return;
        }
        if (!computer.isEquipped()) {
            return;
        }
        if (!(computer.getEntity() instanceof ServerPlayer)) {
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
    public void onUnequipped(SmartGlassesSideAccess access) {
        this.equipped = false;
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
        //     if (owner == null) {
        //         return;
        //     }
        //     APNetworking.sendToPlayer(owner, new RenderableObjectAddPacket(owner.getUUID(), object));
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
        ServerPlayer owner = this.getOwner();
        if (owner != null) {
            APNetworking.sendToPlayer(owner, new RenderableObjectDeletePacket(id));
        }
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
        ServerPlayer owner = this.getOwner();
        if (owner != null) {
            APNetworking.sendToPlayer(owner, new RenderableObjectClearPacket());
        }
        return size;
    }

    /**
     * Just sends a sync package to the client, this method should only be called from the setter lua functions from our objects
     *
     * @param object the object to sync to the player
     */
    public void update(OverlayObject object) {
        // if (this.autoUpdate) {
        //     ServerPlayer owner = this.getOwner();
        //     if (owner != null) {
        //         APNetworking.sendToPlayer(owner, new RenderableObjectSyncPacket(object));
        //     }
        //     return;
        // }
        if (!this.objectsToAdd.containsKey(object.getId())) {
            this.objectsToUpdate.put(object.getId(), object);
        }
    }

    private void sendAllObjects() {
        ServerPlayer owner = this.getOwner();
        if (owner == null) {
            return;
        }
        int maxPackets = 10000;
        List<OverlayObject> packedObjects = new ArrayList<>();
        Iterator<OverlayObject> iter = this.objects.values().iterator();
        while (iter.hasNext()) {
            packedObjects.clear();
            do {
                packedObjects.add(iter.next());
            } while (iter.hasNext() && packedObjects.size() < maxPackets);
            APNetworking.sendToPlayer(owner, new RenderableObjectBulkAddPacket(owner.getUUID(), List.copyOf(packedObjects)));
        }
    }

    public int bulkUpdate() {
        int count = 0;

        ServerPlayer owner = this.getOwner();
        if (owner == null) {
            {
                int remaining = this.objectsToAdd.size();
                for (int id : this.objectsToAdd.keySet()) {
                    if (remaining <= 0) {
                        break;
                    }
                    OverlayObject object = this.objectsToAdd.remove(id);
                    if (object == null) {
                        continue;
                    }
                    this.objects.put(id, object);
                    remaining--;
                    count++;
                }
            }
            {
                int remaining = this.objectsToUpdate.size();
                for (int id : this.objectsToUpdate.keySet()) {
                    if (remaining <= 0) {
                        break;
                    }
                    OverlayObject object = this.objectsToUpdate.remove(id);
                    if (object == null) {
                        continue;
                    }
                    remaining--;
                    count++;
                }
            }
            return count;
        }

        int maxPackets = 10000;
        List<OverlayObject> packedObjects = new ArrayList<>();
        {
            // In some cases, if the user creates a lot of objects, the packet payload can be too big.
            // We split up the packets for every 10k objects to prevent the payload limit from mc
            int remaining = this.objectsToAdd.size();
            while (remaining > 0) {
                packedObjects.clear();
                for (int id : this.objectsToAdd.keySet()) {
                    if (remaining <= 0 || packedObjects.size() >= maxPackets) {
                        break;
                    }
                    OverlayObject object = this.objectsToAdd.remove(id);
                    if (object == null) {
                        continue;
                    }
                    this.objects.put(id, object);
                    packedObjects.add(object);
                    remaining--;
                    count++;
                }
                APNetworking.sendToPlayer(owner, new RenderableObjectBulkAddPacket(owner.getUUID(), List.copyOf(packedObjects)));
            }
        }
        {
            int remaining = this.objectsToUpdate.size();
            while (remaining > 0) {
                packedObjects.clear();
                for (int id : this.objectsToUpdate.keySet()) {
                    if (remaining <= 0 || packedObjects.size() >= maxPackets) {
                        break;
                    }
                    OverlayObject object = this.objectsToUpdate.remove(id);
                    if (object == null) {
                        continue;
                    }
                    packedObjects.add(object);
                    remaining--;
                    count++;
                }
                APNetworking.sendToPlayer(owner, new RenderableObjectBulkSyncPacket(List.copyOf(packedObjects)));
            }
        }

        return count;
    }
}
