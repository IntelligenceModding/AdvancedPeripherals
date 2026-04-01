package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public class ChunkManager extends SavedData {

    private static final String DATA_NAME = AdvancedPeripherals.MOD_ID + "_ForcedChunks";
    private static final String FORCED_CHUNKS_TAG = "forcedChunks";
    private static final SavedData.Factory<ChunkManager> FACTORY = new SavedData.Factory<>(ChunkManager::new, ChunkManager::load, null);
    private static final TicketController CONTROLLER = new TicketController(AdvancedPeripherals.getRL("chunkcontroller"), null);

    private static long tickCounter = 0;

    private final MinecraftServer server;
    private final Map<UUID, LoadChunkRecord> forcedChunks = new HashMap<>();
    private boolean initialized = false;

    public ChunkManager() {
        this.server = ServerLifecycleHooks.getCurrentServer();
    }

    public static @NotNull ChunkManager get(@NotNull MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static ChunkManager load(@NotNull CompoundTag data, HolderLookup.Provider provider) {
        ChunkManager manager = new ChunkManager();
        CompoundTag forcedData = data.getCompound(FORCED_CHUNKS_TAG);
        AdvancedPeripherals.debug("Loading chunk manager from NBT " + data, org.apache.logging.log4j.Level.WARN);
        for (String key : forcedData.getAllKeys()) {
            manager.forcedChunks.put(UUID.fromString(key), LoadChunkRecord.deserialize(forcedData.getCompound(key)));
        }
        return manager;
    }

    public static int getMaxLoadRadius() {
        return APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
    }

    // Registered using the mod bus in the main class
    public static void registerTicketController(RegisterTicketControllersEvent event) {
        event.register(CONTROLLER);
    }

    @SubscribeEvent
    public static void afterServerStarted(ServerStartedEvent event) {
        get(event.getServer()).init();
    }

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        if (!event.hasTime()) {
            return;
        }
        tickCounter++;
        if (tickCounter % (APConfig.PERIPHERALS_CONFIG.chunkLoadValidTime.get() * 20 / 10) == 0) {
            get(event.getServer()).cleanup();
        }
    }

    private static boolean forceChunk(UUID owner, ServerLevel level, ChunkPos pos) {
        AdvancedPeripherals.debug("Forcing chunk " + pos, org.apache.logging.log4j.Level.WARN);
        return CONTROLLER.forceChunk(level, owner, pos.x, pos.z, true, true);
    }

    private static boolean unforceChunk(UUID owner, ServerLevel level, ChunkPos pos) {
        AdvancedPeripherals.debug("Unforcing chunk " + pos, org.apache.logging.log4j.Level.WARN);
        return CONTROLLER.forceChunk(level, owner, pos.x, pos.z, false, true);
    }

    public int getForcedChunksCount() {
        return this.forcedChunks.size();
    }

    public boolean addForceChunk(ServerLevel level, UUID owner, ChunkPos pos) {
        AdvancedPeripherals.debug("Trying to load forced chunk cluster " + pos, org.apache.logging.log4j.Level.WARN);
        LoadChunkRecord oldRecord = this.forcedChunks.get(owner);
        if (oldRecord != null) {
            ServerLevel oldLevel = this.server.getLevel(oldRecord.getDimension());
            if (oldLevel == level && pos.equals(oldRecord.getPos())) {
                return true;
            }
            unforceChunkRecord(owner, oldRecord, oldLevel);
        }
        final int chunkRadius = getMaxLoadRadius();
        this.forcedChunks.put(owner, new LoadChunkRecord(level.dimension(), pos, chunkRadius));
        this.setDirty();
        boolean result = true;
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                result &= forceChunk(owner, level, new ChunkPos(pos.x + x, pos.z + z));
            }
        }
        return result;
    }

    public void touch(UUID owner) {
        LoadChunkRecord forcedChunk = this.forcedChunks.get(owner);
        if (forcedChunk != null) {
            forcedChunk.touch();
        }
    }

    public boolean removeForceChunk(ServerLevel level, UUID owner) {
        AdvancedPeripherals.debug("Attempting to unload forced chunk cluster " + owner, org.apache.logging.log4j.Level.WARN);
        LoadChunkRecord chunkRecord = this.forcedChunks.get(owner);
        if (chunkRecord == null) {
            return true;
        }
        if (chunkRecord.getDimension() != level.dimension()) {
            throw new IllegalArgumentException(String.format("Incorrect dimension! Should be %s instead of %s", chunkRecord.getDimension(), level.dimension()));
        }
        boolean result = unforceChunkRecord(owner, chunkRecord, level);
        if (result) {
            this.forcedChunks.remove(owner);
            this.setDirty();
        }
        return result;
    }

    private boolean unforceChunkRecord(UUID owner, LoadChunkRecord chunkRecord, ServerLevel level) {
        boolean result = true;
        final ChunkPos pos = chunkRecord.getPos();
        final int chunkRadius = chunkRecord.getRadius();
        AdvancedPeripherals.debug(String.format("Trying to unload forced chunk cluster %s at %s with radius %d", owner, pos, chunkRadius), org.apache.logging.log4j.Level.WARN);
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                result &= unforceChunk(owner, level, new ChunkPos(pos.x + x, pos.z + z));
            }
        }
        return result;
    }

    public void init() {
        if (this.initialized) {
            return;
        }
        this.initialized = true;

        AdvancedPeripherals.debug(String.format("Schedule chunk manager init, forcedChunks = %d", this.forcedChunks.size()), org.apache.logging.log4j.Level.WARN);
        final int chunkRadius = getMaxLoadRadius();
        this.forcedChunks.forEach((uuid, value) -> {
            ResourceKey<Level> dimension = value.getDimension();
            ServerLevel level = this.server.getLevel(dimension);
            if (level == null) {
                AdvancedPeripherals.debug("Skipped not exists dimension " + dimension, org.apache.logging.log4j.Level.ERROR);
                return;
            }
            final ChunkPos pos = value.getPos();
            final int loadedRadius = value.getRadius();
            AdvancedPeripherals.debug(String.format("Recorded chunk in %s at %s with radius %d", dimension, pos, loadedRadius), org.apache.logging.log4j.Level.INFO);
            if (loadedRadius == chunkRadius) {
                return;
            }
            if (loadedRadius > chunkRadius) {
                // clean overflowed load radius
                for (int x = -loadedRadius; x <= loadedRadius; x++) {
                    for (int z = -loadedRadius; z <= loadedRadius; z++) {
                        if (Math.abs(x) > chunkRadius || Math.abs(z) > chunkRadius) {
                            unforceChunk(uuid, level, new ChunkPos(pos.x + x, pos.z + z));
                        }
                    }
                }
            } else if (loadedRadius < chunkRadius) {
                // otherwise, only do the changed part to reduce startup time (in case we have a lot chunky turtle)
                for (int x = -chunkRadius; x <= chunkRadius; x++) {
                    for (int z = -chunkRadius; z <= chunkRadius; z++) {
                        if (Math.abs(x) > loadedRadius || Math.abs(z) > loadedRadius) {
                            forceChunk(uuid, level, new ChunkPos(pos.x + x, pos.z + z));
                        }
                    }
                }
            }
            value.setRadius(chunkRadius);
            this.setDirty();
        });
    }

    public void cleanup() {
        AdvancedPeripherals.debug("Schedule chunk manager cleanup", org.apache.logging.log4j.Level.WARN);
        final Iterator<Map.Entry<UUID, LoadChunkRecord>> iterator = this.forcedChunks.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<UUID, LoadChunkRecord> entry = iterator.next();
            UUID uuid = entry.getKey();
            LoadChunkRecord chunkRecord = entry.getValue();
            ResourceKey<Level> dimension = chunkRecord.getDimension();
            ServerLevel level = this.server.getLevel(dimension);
            if (level == null) {
                continue;
            }
            if (chunkRecord.isValid()) {
                continue;
            }
            AdvancedPeripherals.debug(String.format("Purge forced chunk for %s", uuid), org.apache.logging.log4j.Level.WARN);
            unforceChunkRecord(uuid, chunkRecord, level);
            iterator.remove();
            this.setDirty();
        }
    }

    @Override
    @NotNull
    public CompoundTag save(@NotNull CompoundTag data, @NotNull HolderLookup.Provider registries) {
        AdvancedPeripherals.debug("Schedule chunk manager save, forcedChunks = " + this.forcedChunks.size(), org.apache.logging.log4j.Level.WARN);
        CompoundTag forcedChunksTag = new CompoundTag();
        this.forcedChunks.forEach((key, value) -> forcedChunksTag.put(key.toString(), value.serialize()));
        // !!! DO NOT forget to put forcedChunksTag into data !!!
        // It will not magically be saved 😅
        data.put(FORCED_CHUNKS_TAG, forcedChunksTag);
        return data;
    }

    private static class LoadChunkRecord {

        private static final String POS_TAG = "pos";
        private static final String DIMENSION_NAME_TAG = "dimensionName";
        private static final String RADIUS_TAG = "radius";

        @NotNull
        private final ResourceKey<Level> dimension;
        @NotNull
        private final ChunkPos pos;
        private int radius;
        private long lastTouch;

        LoadChunkRecord(@NotNull ResourceKey<Level> dimension, @NotNull ChunkPos pos, int radius) {
            this.dimension = dimension;
            this.pos = pos;
            this.radius = radius;
            this.lastTouch = tickCounter;
        }

        public static LoadChunkRecord deserialize(@NotNull CompoundTag tag) {
            return new LoadChunkRecord(
                ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString(DIMENSION_NAME_TAG))),
                NBTUtil.chunkPosFromNBT(tag.getCompound(POS_TAG)),
                tag.getInt(RADIUS_TAG)
            );
        }

        @NotNull
        public ChunkPos getPos() {
            return this.pos;
        }

        @NotNull
        public ResourceKey<Level> getDimension() {
            return this.dimension;
        }

        public int getRadius() {
            return this.radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public void touch() {
            this.lastTouch = tickCounter;
        }

        public boolean isValid() {
            return this.lastTouch + APConfig.PERIPHERALS_CONFIG.chunkLoadValidTime.get() * 20 >= tickCounter;
        }

        @NotNull
        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putString(DIMENSION_NAME_TAG, this.dimension.location().toString());
            tag.put(POS_TAG, NBTUtil.toNBT(this.pos));
            tag.putInt(RADIUS_TAG, this.radius);
            return tag;
        }
    }
}
