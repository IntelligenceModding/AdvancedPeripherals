package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class ChunkyPeripheral extends BasePeripheral<TurtlePeripheralOwner> {

    public static final String TYPE = "chunky";
    private static final String UUID_TAG = "uuid";
    private @Nullable ChunkPos loadedChunk;

    public ChunkyPeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(TYPE, new TurtlePeripheralOwner(turtle, side));
    }

    protected UUID getUUID() {
        CompoundTag data = owner.getDataStorage();
        if (!data.contains(UUID_TAG)) {
            data.putUUID(UUID_TAG, UUID.randomUUID());
            owner.markDataStorageDirty();
        }
        return data.getUUID(UUID_TAG);
    }

    public ChunkPos getChunkPos() {
        return getLevel().getChunkAt(getPos()).getPos();
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.ENABLE_CHUNKY_TURTLE.get();
    }

    public void updateChunkState() {
        ServerLevel level = (ServerLevel) getLevel();
        ChunkManager manager = ChunkManager.get(level);
        ChunkPos currentChunk = getChunkPos();
        if (loadedChunk == null || !loadedChunk.equals(currentChunk)) {
            setLoadedChunk(currentChunk, manager, level);
        } else {
            manager.touch(getUUID());
        }
    }

    protected void setLoadedChunk(@Nullable ChunkPos newChunk, ChunkManager manager, ServerLevel level) {

        if (loadedChunk != null) {
            manager.removeForceChunk(level, getUUID(), loadedChunk);
            level.setChunkForced(loadedChunk.x, loadedChunk.z, false);
            loadedChunk = null;
        }
        if (newChunk != null) {
            loadedChunk = newChunk;
            manager.addForceChunk(level, getUUID(), loadedChunk);
            level.setChunkForced(newChunk.x, newChunk.z, true);
        }
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        super.detach(computer);
        ServerLevel level = (ServerLevel) owner.getLevel();
        ChunkManager manager = ChunkManager.get(Objects.requireNonNull(level));
        setLoadedChunk(null, manager, level);
    }
}
