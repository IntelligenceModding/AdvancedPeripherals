package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.nbt.CompoundTag;
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
            tickCounter += 2;
            if (tickCounter >= APConfig.PERIPHERALS_CONFIG.chunkLoadValidTime.get()) {
                tickCounter = 0;
                ChunkManager.get(ServerLifecycleHooks.getCurrentServer().overworld()).cleanup();
            }
        }
    }

    private static boolean forceChunk(ServerLevel level, UUID owner, ChunkPos pos) {
        return ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, owner, pos.x, pos.z, true, true);
    }

    private static boolean unforceChunk(ServerLevel level, UUID owner, ChunkPos pos) {
        return ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, owner, pos.x, pos.z, false, true);
    }

    public synchronized boolean addForceChunk(ServerLevel level, UUID owner, ChunkPos pos) {
        AdvancedPeripherals.debug("Trying to load forced chunk cluster" + pos, Level.WARN);
        if (forcedChunks.containsKey(owner))
            return true;
        final int chunkRadius = APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
        forcedChunks.put(owner, new LoadChunkRecord(level.dimension().location().toString(), pos, chunkRadius));
        setDirty();
        boolean result = true;
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                result &= forceChunk(level, owner, new ChunkPos(pos.x + x, pos.z + z));
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
    // TODO: remove in next major version
    public synchronized boolean removeForceChunk(ServerLevel level, UUID owner, ChunkPos pos) {
        return removeForceChunk(level, owner);
    }

    public synchronized boolean removeForceChunk(ServerLevel level, UUID owner) {
        AdvancedPeripherals.debug("Attempting to unload forced chunk cluster" + owner, Level.WARN);
        LoadChunkRecord chunkRecord = forcedChunks.get(owner);
        if (chunkRecord == null)
            return true;
        String dimensionName = level.dimension().location().toString();
        if (!chunkRecord.getDimensionName().equals(dimensionName))
            throw new IllegalArgumentException(String.format("Incorrect dimension! Should be %s instead of %s", chunkRecord.getDimensionName(), dimensionName));
        boolean result = true;
        final ChunkPos pos = chunkRecord.getPos();
        final int chunkRadius = chunkRecord.getRadius();
        AdvancedPeripherals.debug("Trying to unload forced chunk cluster" + owner + " at " + pos + " with radius " + chunkRadius, Level.WARN);
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                result &= unforceChunk(level, owner, new ChunkPos(pos.x + x, pos.z + z));
            }
        }
        if (result) {
            forcedChunks.remove(owner);
            setDirty();
        }
        return result;
    }

    public synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        AdvancedPeripherals.debug("Schedule chunk manager init", Level.WARN); 
        final int chunkRadius = APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
        final Map<String, ServerLevel> levels = getServerLevels();
        forcedChunks.forEach((uuid, value) -> {
            String dimensionName = value.getDimensionName();
            ServerLevel level = levels.get(dimensionName);
            if (level == null) {
                return;
            }
            final ChunkPos pos = value.getPos();
            final int loadedRadius = value.getRadius();
            if (loadedRadius > chunkRadius) {
                // clean overflowed load radius
                for (int x = chunkRadius + 1; x <= loadedRadius; x++) {
                    for (int z = chunkRadius + 1; z <= loadedRadius; z++) {
                        unforceChunk(level, uuid, new ChunkPos(pos.x + x, pos.z + z));
                        unforceChunk(level, uuid, new ChunkPos(pos.x + x, pos.z - z));
                        unforceChunk(level, uuid, new ChunkPos(pos.x - x, pos.z + z));
                        unforceChunk(level, uuid, new ChunkPos(pos.x - x, pos.z - z));
                    }
                }
                value.setRadius(chunkRadius);
            }
            for (int x = -chunkRadius; x <= chunkRadius; x++) {
                for (int z = -chunkRadius; z <= chunkRadius; z++) {
                    forceChunk(level, uuid, new ChunkPos(pos.x + x, pos.z + z));
                }
            }
        });
    }

    public synchronized void cleanup() {
        AdvancedPeripherals.debug("Schedule chunk manager cleanup", Level.WARN);
        final Map<String, ServerLevel> levels = getServerLevels();
        forcedChunks.forEach((uuid, value) -> {
            String dimensionName = value.getDimensionName();
            ServerLevel level = levels.get(dimensionName);
            if (level == null) {
                return;
            }
            if (value.isValid()) {
                return;
            }
            AdvancedPeripherals.debug(String.format("Purge forced chunk for %s", uuid), Level.WARN);
            removeForceChunk(level, uuid);
        });
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag data) {
        CompoundTag forcedChunksTag = new CompoundTag();
        forcedChunks.forEach((key, value) -> forcedChunksTag.put(key.toString(), value.serialize()));
        return data;
    }

    private static final Map<String, ServerLevel> getServerLevels() {
        Map<String, ServerLevel> levels = new HashMap<>();
        ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(level -> {
            String dimensionName = level.dimension().location().toString();
            levels.put(dimensionName, level);
        });
        return levels;
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
            int radius = keys.contains(RADIUS_TAG) ? tag.getInt(RADIUS_TAG) : APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
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
