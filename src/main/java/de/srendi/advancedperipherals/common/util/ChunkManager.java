package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class ChunkManager extends WorldSavedData {

    private static final String DATA_NAME = AdvancedPeripherals.MOD_ID + "_ForcedChunks";
    private static final String FORCED_CHUNKS_TAG = "forcedChunks";
    private static int tickCounter = 0;
    private final Map<UUID, LoadChunkRecord> forcedChunks = new HashMap<>();
    private boolean initialized = false;

    public ChunkManager() {
        super(DATA_NAME);
    }

    public static @NotNull ChunkManager get(@NotNull ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(ChunkManager::new, DATA_NAME);
    }

    @SubscribeEvent
    public static void beforeServerStopped(FMLServerStoppingEvent event) {
        ChunkManager.get(event.getServer().overworld()).stop();
    }

    @SubscribeEvent
    public static void afterServerStarted(FMLServerStartedEvent event) {
        ChunkManager.get(event.getServer().overworld()).init();
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
            if (tickCounter % (APConfig.PERIPHERALS_CONFIG.CHUNK_LOAD_VALID_TIME.get() / 2) == 0) {
                ChunkManager.get(ServerLifecycleHooks.getCurrentServer().overworld()).cleanup();
            }
        }
    }

    public synchronized boolean addForceChunk(ServerWorld world, UUID owner, ChunkPos pos) {
        AdvancedPeripherals.debug("Trying to load forced chunk " + pos, Level.WARN);
        if (forcedChunks.containsKey(owner))
            throw new IllegalArgumentException(String.format("You need to cleanup force loaded chunk for %s first", owner));
        forcedChunks.put(owner, new LoadChunkRecord(world.dimension().location().toString(), pos));
        setDirty();
        return ForgeChunkManager.forceChunk(world, AdvancedPeripherals.MOD_ID, owner, pos.x, pos.z, true, true);
    }

    public synchronized void touch(UUID owner) {
        if (forcedChunks.containsKey(owner))
            forcedChunks.get(owner).touch();
    }

    public synchronized boolean removeForceChunk(ServerWorld world, UUID owner, ChunkPos pos) {
        AdvancedPeripherals.debug("Trying to unload forced chunk " + pos, Level.WARN);
        if (!forcedChunks.containsKey(owner))
            return true;
        LoadChunkRecord record = forcedChunks.get(owner);
        String dimensionName = world.dimension().location().toString();
        if (!record.getDimensionName().equals(dimensionName))
            throw new IllegalArgumentException(String.format("Incorrect dimension! Should be %s instead of %s", record.getDimensionName(), dimensionName));
        boolean result = ForgeChunkManager.forceChunk(world, AdvancedPeripherals.MOD_ID, owner, pos.x, pos.z, false, true);
        if (result) {
            forcedChunks.remove(owner);
            setDirty();
        }
        return result;
    }

    public synchronized void init() {
        if (!initialized) {
            AdvancedPeripherals.debug("Schedule chunk manager init", Level.WARN);
            ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(world -> {
                String dimensionName = world.dimension().location().toString();
                forcedChunks.entrySet().stream().filter(entry -> entry.getValue().getDimensionName().equals(dimensionName))
                        .forEach(entry -> ForgeChunkManager.forceChunk(world, AdvancedPeripherals.MOD_ID, entry.getKey(), entry.getValue().getPos().x, entry.getValue().getPos().z, true, true));
            });
            initialized = true;
        }
    }

    public synchronized void stop() {
        if (initialized) {
            AdvancedPeripherals.debug("Schedule chunk manager stop", Level.WARN);
            ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(world -> {
                String dimensionName = world.dimension().location().toString();
                forcedChunks.entrySet().stream().filter(entry -> entry.getValue().getDimensionName().equals(dimensionName))
                        .forEach(entry -> ForgeChunkManager.forceChunk(world, AdvancedPeripherals.MOD_ID, entry.getKey(), entry.getValue().getPos().x, entry.getValue().getPos().z, false, true));
            });
            initialized = false;
        }
    }

    public synchronized void cleanup() {
        AdvancedPeripherals.debug("Schedule chunk manager cleanup", Level.WARN);
        ServerLifecycleHooks.getCurrentServer().getAllLevels().forEach(world -> {
            String dimensionName = world.dimension().location().toString();
            List<UUID> purgeList = new ArrayList<>();
            forcedChunks.entrySet().stream().filter(entry -> entry.getValue().getDimensionName().equals(dimensionName) && !entry.getValue().isValid())
                    .forEach(entry -> purgeList.add(entry.getKey()));
            purgeList.forEach(uuid -> {
                AdvancedPeripherals.debug(String.format("Purge forced chunk for %s", uuid), Level.WARN);
                removeForceChunk(world, uuid, forcedChunks.get(uuid).getPos());
            });
        });
    }

    @Override
    public void load(@NotNull CompoundNBT data) {
        CompoundNBT forcedData = data.getCompound(FORCED_CHUNKS_TAG);
        for (String key : forcedData.getAllKeys()) {
            forcedChunks.put(UUID.fromString(key), LoadChunkRecord.deserialize(forcedData.getCompound(key)));
        }
    }

    @Override
    public @NotNull CompoundNBT save(@NotNull CompoundNBT data) {
        CompoundNBT forcedChunksTag = new CompoundNBT();
        forcedChunks.forEach((key, value) -> forcedChunksTag.put(key.toString(), value.serialize()));
        return data;
    }

    private static class LoadChunkRecord {

        private static final String POS_TAG = "pos";
        private static final String DIMENSION_NAME_TAG = "dimensionName";

        private final @NotNull String dimensionName;
        private final @NotNull ChunkPos pos;
        private long lastTouch;

        public LoadChunkRecord(@NotNull String dimensionName, @NotNull ChunkPos pos) {
            this.dimensionName = dimensionName;
            this.pos = pos;
            this.lastTouch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        }

        public static LoadChunkRecord deserialize(@NotNull CompoundNBT tag) {
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
            return lastTouch + APConfig.PERIPHERALS_CONFIG.CHUNK_LOAD_VALID_TIME.get() >= currentEpoch;
        }

        public @NotNull CompoundNBT serialize() {
            CompoundNBT tag = new CompoundNBT();
            tag.putString(DIMENSION_NAME_TAG, dimensionName);
            tag.put(POS_TAG, NBTUtil.toNBT(pos));
            return tag;
        }
    }
}
