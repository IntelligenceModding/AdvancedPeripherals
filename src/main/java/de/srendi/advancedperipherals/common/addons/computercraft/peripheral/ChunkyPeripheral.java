package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.configuration.GeneralConfig;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TurtlePeripheralOwner;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
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
        CompoundNBT data = owner.getDataStorage();
        if (!data.contains(UUID_TAG)) {
            data.putUUID(UUID_TAG, UUID.randomUUID());
            owner.markDataStorageDirty();
        }
        return data.getUUID(UUID_TAG);
    }

    public ChunkPos getChunkPos() {
        return getWorld().getChunkAt(getPos()).getPos();
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.ENABLE_CHUNKY_TURTLE.get();
    }

    public void updateChunkState() {
        ServerWorld world = (ServerWorld) getWorld();
        ChunkManager manager = ChunkManager.get(world);
        ChunkPos currentChunk = getChunkPos();
        if (loadedChunk == null || !loadedChunk.equals(currentChunk)) {
            setLoadedChunk(currentChunk, manager, world);
        } else {
            manager.touch(getUUID());
        }
    }

    protected void setLoadedChunk(@Nullable ChunkPos newChunk, ChunkManager manager, ServerWorld world) {

        if (loadedChunk != null) {
            manager.removeForceChunk(world, getUUID(), loadedChunk);
            loadedChunk = null;
        }
        if (newChunk != null) {
            loadedChunk = newChunk;
            manager.addForceChunk(world, getUUID(), loadedChunk);
        }
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        super.detach(computer);
        ServerWorld world = (ServerWorld) owner.getWorld();
        ChunkManager manager = ChunkManager.get(Objects.requireNonNull(world));
        setLoadedChunk(null, manager, world);
    }
}
