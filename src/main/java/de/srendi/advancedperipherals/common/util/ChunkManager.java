package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class ChunkManager extends SavedData {

    private static final String DATA_NAME = AdvancedPeripherals.MOD_ID + "_ForcedChunks";
    private static final String FORCED_CHUNKS_TAG = "forcedChunks";
    private static int tickCounter = 0;
    private final Map<UUID, LoadChunkRecord> forcedChunks = new HashMap<>();
    private boolean initialized = false;

    public ChunkManager() {
        super();
    }

    public static @NotNull ChunkManager get(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ChunkManager::load, ChunkManager::new, DATA_NAME);
    }

    public static ChunkManager load(@NotNull CompoundTag data) {
        ChunkManager manager = new ChunkManager();
        CompoundTag forcedData = data.getCompound(FORCED_CHUNKS_TAG);
        AdvancedPeripherals.debug("Loading chunk manager from NBT " + data, Level.WARN);
        for (String key : forcedData.getAllKeys()) {
            manager.forcedChunks.put(UUID.fromString(key), LoadChunkRecord.deserialize(forcedData.getCompound(key)));
        }
        return manager;
    }

    @SubscribeEvent
    public static void afterServerStarted(ServerStartedEvent event) {
        ChunkManager.get(event.getServer().overworld()).init();
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
            // run cleanup per chunkLoadValidTime / 10
            final int checkIntervalInTick = APConfig.PERIPHERALS_CONFIG.chunkLoadValidTime.get() * 20 / 10;
            if (tickCounter >= checkIntervalInTick) {
                tickCounter = 0;
                ChunkManager.get(ServerLifecycleHooks.getCurrentServer().overworld()).cleanup();
            }
        }
    }

    private static boolean forceChunk(UUID owner, ServerLevel level, ChunkPos pos) {
        AdvancedPeripherals.debug("Forcing chunk " + pos, Level.WARN);
        return ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, owner, pos.x, pos.z, true, true);
    }

    private static boolean unforceChunk(UUID owner, ServerLevel level, ChunkPos pos) {
        AdvancedPeripherals.debug("Unforcing chunk " + pos, Level.WARN);
        return ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, owner, pos.x, pos.z, false, true);
    }

    public synchronized boolean addForceChunk(ServerLevel level, UUID owner, ChunkPos pos) {
        AdvancedPeripherals.debug("Trying to load forced chunk cluster " + pos, Level.WARN);
        LoadChunkRecord oldRecord = forcedChunks.get(owner);
        if (oldRecord != null) {
            ServerLevel oldLevel = getServerLevel(oldRecord.getDimensionName());
            if (oldLevel == level && pos.equals(oldRecord.getPos())) {
                return true;
            }
            unforceChunkRecord(owner, oldRecord, oldLevel);
        }
        final int chunkRadius = APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
        forcedChunks.put(owner, new LoadChunkRecord(level.dimension().location().toString(), pos, chunkRadius));
        setDirty();
        boolean result = true;
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                result &= forceChunk(owner, level, new ChunkPos(pos.x + x, pos.z + z));
            }
        }
        return result;
    }

    public synchronized void touch(UUID owner) {
        LoadChunkRecord forcedChunk = forcedChunks.get(owner);
        if (forcedChunk != null) {
            forcedChunk.touch();
        }
    }

    // This method is kept for backward compatibility
    // use removeForceChunk without the position argument instead
    // TODO: remove in next major version
    @Deprecated(forRemoval = true, since = "1.19.2-0.7.36")
    public synchronized boolean removeForceChunk(ServerLevel level, UUID owner, ChunkPos pos) {
        return removeForceChunk(level, owner);
    }

    public synchronized boolean removeForceChunk(ServerLevel level, UUID owner) {
        AdvancedPeripherals.debug("Attempting to unload forced chunk cluster " + owner, Level.WARN);
        LoadChunkRecord chunkRecord = forcedChunks.get(owner);
        if (chunkRecord == null)
            return true;
        String dimensionName = level.dimension().location().toString();
        if (!chunkRecord.getDimensionName().equals(dimensionName))
            throw new IllegalArgumentException(String.format("Incorrect dimension! Should be %s instead of %s", chunkRecord.getDimensionName(), dimensionName));
        boolean result = unforceChunkRecord(owner, chunkRecord, level);
        if (result) {
            forcedChunks.remove(owner);
            setDirty();
        }
        return result;
    }

    private synchronized boolean unforceChunkRecord(UUID owner, LoadChunkRecord chunkRecord, ServerLevel level) {
        boolean result = true;
        final ChunkPos pos = chunkRecord.getPos();
        final int chunkRadius = chunkRecord.getRadius();
        AdvancedPeripherals.debug(String.format("Trying to unload forced chunk cluster %s at %s with radius %d", owner, pos, chunkRadius), Level.WARN);
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                result &= unforceChunk(owner, level, new ChunkPos(pos.x + x, pos.z + z));
            }
        }
        return result;
    }

    public synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        AdvancedPeripherals.debug(String.format("Schedule chunk manager init, forcedChunks = %d", forcedChunks.size()), Level.WARN);
        final int chunkRadius = APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
        final Map<String, ServerLevel> levels = getServerLevels();
        forcedChunks.forEach((uuid, value) -> {
            String dimensionName = value.getDimensionName();
            ServerLevel level = levels.get(dimensionName);
            if (level == null) {
                AdvancedPeripherals.debug("Skipped not exists dimension " + dimensionName, Level.ERROR);
                return;
            }
            final ChunkPos pos = value.getPos();
            final int loadedRadius = value.getRadius();
            AdvancedPeripherals.debug(String.format("Recorded chunk in %s at %s with radius %d", dimensionName, pos, loadedRadius), Level.INFO);
            if (loadedRadius == chunkRadius) {
                return;
            }
            if (loadedRadius == -1) {
                // if it's coming from old version, just force all
                for (int x = -chunkRadius; x <= chunkRadius; x++) {
                    for (int z = -chunkRadius; z <= chunkRadius; z++) {
                        forceChunk(uuid, level, new ChunkPos(pos.x + x, pos.z + z));
                    }
                }
            } else if (loadedRadius > chunkRadius) {
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
            setDirty();
        });
    }

    public synchronized void cleanup() {
        AdvancedPeripherals.debug("Schedule chunk manager cleanup", Level.WARN);
        final Map<String, ServerLevel> levels = getServerLevels();
        final Iterator<Map.Entry<UUID, LoadChunkRecord>> iterator = forcedChunks.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<UUID, LoadChunkRecord> entry = iterator.next();
            UUID uuid = entry.getKey();
            LoadChunkRecord chunkRecord = entry.getValue();
            String dimensionName = chunkRecord.getDimensionName();
            ServerLevel level = levels.get(dimensionName);
            if (level == null) {
                continue;
            }
            if (chunkRecord.isValid()) {
                continue;
            }
            AdvancedPeripherals.debug(String.format("Purge forced chunk for %s", uuid), Level.WARN);
            unforceChunkRecord(uuid, chunkRecord, level);
            iterator.remove();
            setDirty();
        }
    }

    @Override
    public synchronized @NotNull CompoundTag save(@NotNull CompoundTag data) {
        AdvancedPeripherals.debug("Schedule chunk manager save, forcedChunks = " + forcedChunks.size(), Level.WARN);
        CompoundTag forcedChunksTag = new CompoundTag();
        forcedChunks.forEach((key, value) -> forcedChunksTag.put(key.toString(), value.serialize()));
        // !!! DO NOT forget to put forcedChunksTag into data !!!
        // It will not magically be saved 😅
        data.put(FORCED_CHUNKS_TAG, forcedChunksTag);
        return data;
    }

    private static Map<String, ServerLevel> getServerLevels() {
        Map<String, ServerLevel> levels = new HashMap<>();
        ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(level -> {
            String dimensionName = level.dimension().location().toString();
            levels.put(dimensionName, level);
        });
        return levels;
    }

    private static ServerLevel getServerLevel(String name) {
        ResourceKey<net.minecraft.world.level.Level> key = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(name));
        return ServerLifecycleHooks.getCurrentServer().getLevel(key);
    }

    private static class LoadChunkRecord {

        private static final String POS_TAG = "pos";
        private static final String DIMENSION_NAME_TAG = "dimensionName";
        private static final String RADIUS_TAG = "radius";

        private final @NotNull String dimensionName;
        private final @NotNull ChunkPos pos;
        private int radius;
        private long lastTouch;

        LoadChunkRecord(@NotNull String dimensionName, @NotNull ChunkPos pos, int radius) {
            this.dimensionName = dimensionName;
            this.pos = pos;
            this.radius = radius;
            this.lastTouch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        }

        public static LoadChunkRecord deserialize(@NotNull CompoundTag tag) {
            Set<String> keys = tag.getAllKeys();
            int radius = keys.contains(RADIUS_TAG) ? tag.getInt(RADIUS_TAG) : -1;
            return new LoadChunkRecord(tag.getString(DIMENSION_NAME_TAG), NBTUtil.chunkPosFromNBT(tag.getCompound(POS_TAG)), radius);
        }

        public @NotNull ChunkPos getPos() {
            return pos;
        }

        public @NotNull String getDimensionName() {
            return dimensionName;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public void touch() {
            lastTouch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        }

        public boolean isValid() {
            long currentEpoch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            return lastTouch + APConfig.PERIPHERALS_CONFIG.chunkLoadValidTime.get() >= currentEpoch;
        }

        public @NotNull CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putString(DIMENSION_NAME_TAG, dimensionName);
            tag.put(POS_TAG, NBTUtil.toNBT(pos));
            tag.putInt(RADIUS_TAG, radius);
            return tag;
        }
    }
}
