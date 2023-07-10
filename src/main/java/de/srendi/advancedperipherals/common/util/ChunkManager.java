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
import net.minecraftforge.event.server.ServerStoppingEvent;
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
    public static void beforeServerStopped(ServerStoppingEvent event) {
        ChunkManager.get(event.getServer().overworld()).stop();
    }

    @SubscribeEvent
    public static void afterServerStarted(ServerStartedEvent event) {
        ChunkManager.get(event.getServer().overworld()).init();
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
            if (tickCounter % (APConfig.PERIPHERALS_CONFIG.chunkLoadValidTime.get() / 2) == 0) {
                ChunkManager.get(ServerLifecycleHooks.getCurrentServer().overworld()).cleanup();
            }
        }
    }

    public synchronized boolean addForceChunk(ServerLevel level, UUID owner, ChunkPos pos) {
        AdvancedPeripherals.debug("Trying to load forced chunk cluster" + pos, Level.WARN);
        if (forcedChunks.containsKey(owner)) return true;
        forcedChunks.put(owner, new LoadChunkRecord(level.dimension().location().toString(), pos));
        setDirty();
        int chunkRadius = APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
        if (chunkRadius == 0) {
            return ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, owner, pos.x, pos.z, true, true);
        }
        boolean result = true;
        for (int x = chunkRadius * -1; x < chunkRadius; x++) {
            for (int z = chunkRadius * -1; z < chunkRadius; z++) {
                result &= ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, owner, pos.x + x, pos.z + z, true, true);
            }
        }
        return result;
    }

    public synchronized void touch(UUID owner) {
        if (forcedChunks.containsKey(owner)) forcedChunks.get(owner).touch();
    }

    public synchronized boolean removeForceChunk(ServerLevel level, UUID owner, ChunkPos pos) {
        AdvancedPeripherals.debug("Trying to unload forced chunk cluster" + pos, Level.WARN);
        if (!forcedChunks.containsKey(owner))
            return true;
        LoadChunkRecord chunkRecord = forcedChunks.get(owner);
        String dimensionName = level.dimension().location().toString();
        if (!chunkRecord.getDimensionName().equals(dimensionName))
            throw new IllegalArgumentException(String.format("Incorrect dimension! Should be %s instead of %s", chunkRecord.getDimensionName(), dimensionName));
        boolean result = true;
        int chunkRadius = APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
        if (chunkRadius == 0) {
            result = ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, owner, pos.x, pos.z, false, true);
        } else {
            for (int x = chunkRadius * -1; x < chunkRadius; x++) {
                for (int z = chunkRadius * -1; z < chunkRadius; z++) {
                    result &= ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, owner, pos.x + x, pos.z + z, true, true);
                }
            }
        }
        if (result) {
            forcedChunks.remove(owner);
            setDirty();
        }
        return result;
    }

    public synchronized void init() {
        if (!initialized) {
            AdvancedPeripherals.debug("Schedule chunk manager init", Level.WARN);
            int chunkRadius = APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
            ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(level -> {
                String dimensionName = level.dimension().location().toString();
                forcedChunks.entrySet().stream().filter(entry -> entry.getValue().getDimensionName().equals(dimensionName)).forEach(entry -> {
                    if (chunkRadius == 0) {
                        ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, entry.getKey(), entry.getValue().getPos().x, entry.getValue().getPos().z, true, true);
                    }
                    for (int x = chunkRadius * -1; x < chunkRadius; x++) {
                        for (int z = chunkRadius * -1; z < chunkRadius; z++) {
                            ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, entry.getKey(), entry.getValue().getPos().x + x, entry.getValue().getPos().z + z, true, true);
                        }
                    }
                });
            });
            initialized = true;
        }
    }

    public synchronized void stop() {
        if (initialized) {
            AdvancedPeripherals.debug("Schedule chunk manager stop", Level.WARN);
            int chunkRadius = APConfig.PERIPHERALS_CONFIG.chunkyTurtleRadius.get();
            ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(level -> {
                String dimensionName = level.dimension().location().toString();
                forcedChunks.entrySet().stream().filter(entry -> entry.getValue().getDimensionName().equals(dimensionName)).forEach(entry -> {
                    if (chunkRadius == 0) {
                        ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, entry.getKey(), entry.getValue().getPos().x, entry.getValue().getPos().z, false, true);
                    }
                    for (int x = chunkRadius * -1; x < chunkRadius; x++) {
                        for (int z = chunkRadius * -1; z < chunkRadius; z++) {
                            ForgeChunkManager.forceChunk(level, AdvancedPeripherals.MOD_ID, entry.getKey(), entry.getValue().getPos().x + x, entry.getValue().getPos().z + z, false, true);
                        }
                    }
                });
            });
            initialized = false;
        }
    }

    public synchronized void cleanup() {
        AdvancedPeripherals.debug("Schedule chunk manager cleanup", Level.WARN);
        ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(level -> {
            String dimensionName = level.dimension().location().toString();
            List<UUID> purgeList = new ArrayList<>();
            forcedChunks.entrySet().stream().filter(entry -> entry.getValue().getDimensionName().equals(dimensionName) && !entry.getValue().isValid()).forEach(entry -> purgeList.add(entry.getKey()));
            purgeList.forEach(uuid -> {
                AdvancedPeripherals.debug(String.format("Purge forced chunk for %s", uuid), Level.WARN);
                removeForceChunk(level, uuid, forcedChunks.get(uuid).getPos());
            });
        });
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag data) {
        CompoundTag forcedChunksTag = new CompoundTag();
        forcedChunks.forEach((key, value) -> forcedChunksTag.put(key.toString(), value.serialize()));
        return data;
    }

    private static class LoadChunkRecord {

        private static final String POS_TAG = "pos";
        private static final String DIMENSION_NAME_TAG = "dimensionName";

        private final @NotNull String dimensionName;
        private final @NotNull ChunkPos pos;
        private long lastTouch;

        LoadChunkRecord(@NotNull String dimensionName, @NotNull ChunkPos pos) {
            this.dimensionName = dimensionName;
            this.pos = pos;
            this.lastTouch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        }

        public static LoadChunkRecord deserialize(@NotNull CompoundTag tag) {
            return new LoadChunkRecord(tag.getString(DIMENSION_NAME_TAG), NBTUtil.chunkPosFromNBT(tag.getCompound(POS_TAG)));
        }

        public @NotNull ChunkPos getPos() {
            return pos;
        }

        public @NotNull String getDimensionName() {
            return dimensionName;
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
            return tag;
        }
    }
}
