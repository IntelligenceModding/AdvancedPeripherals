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

    public static final String PERIPHERAL_TYPE = "chunky";
    private static final String UUID_TAG = "uuid";
    private @Nullable ChunkPos loadedCentralChunk = null;

    public ChunkyPeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(PERIPHERAL_TYPE, new TurtlePeripheralOwner(turtle, side));
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
        return APConfig.PERIPHERALS_CONFIG.enableChunkyTurtle.get();
    }

    public void updateChunkState() {
        // TODO: should find someway to update after turtle moved or while moving, but not every tick
        ServerLevel level = (ServerLevel) getLevel();
        ChunkManager manager = ChunkManager.get(level);
        ChunkPos currentChunk = getChunkPos();
        setLoadedChunk(currentChunk, manager, level);
        manager.touch(getUUID());
    }

    protected void setLoadedChunk(@Nullable ChunkPos newChunk, ChunkManager manager, ServerLevel level) {
        if (loadedCentralChunk != null) {
            if (loadedCentralChunk.equals(newChunk)) {
                return;
            }
            manager.removeForceChunk(level, getUUID());
            // Should not be used
            // level.setChunkForced(loadedChunk.x, loadedChunk.z, false);
            loadedCentralChunk = null;
        }
        if (newChunk != null) {
            loadedCentralChunk = newChunk;
            manager.addForceChunk(level, getUUID(), loadedCentralChunk);
            // Should not be used
            // level.setChunkForced(newChunk.x, newChunk.z, true);
        }
    }

    @Override
    public void attach(IComputerAccess computer) {
        super.attach(computer);
        ServerLevel level = (ServerLevel) owner.getLevel();
        ChunkManager manager = ChunkManager.get(Objects.requireNonNull(level));
        ChunkPos currentChunk = getChunkPos();
        setLoadedChunk(currentChunk, manager, level);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        super.detach(computer);
        // Should not remove the loaded chunk when detaching,
        // because CC:T will detach all peripherals before server stopped.
        // So the chunk record will never be saved if we removed the chunk record when detaching.
        // The records will be automatically removed by the ChunkManager if they have not been touched a while ago.

        // ServerLevel level = (ServerLevel) owner.getLevel();
        // ChunkManager manager = ChunkManager.get(Objects.requireNonNull(level));
        // setLoadedChunk(null, manager, level);
    }
}
