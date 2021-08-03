package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChunkyPeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TurtleChunky extends BaseTurtle<ChunkyPeripheral> {

    protected static final Set<ChunkPos> loadedChunks = new HashSet<>();
    protected ChunkyPeripheral randomPeripheral;

    private int tick;

    public TurtleChunky() {
        super("chunky_turtle", "turtle.advancedperipherals.chunky_turtle", new ItemStack(Items.CHUNK_CONTROLLER.get()));
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return null; //Null, the turtle uses the chunk controller item model. See BaseTurtle.java
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return null;
    }

    @Override
    protected ChunkyPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        ChunkyPeripheral newPeripheral = new ChunkyPeripheral("chunky", turtle, side);
        if (randomPeripheral == null)
            randomPeripheral = newPeripheral;
        return newPeripheral;
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        tick++;
        if (tick % 10 == 0) {
            //Add a chunk to the Chunk Manager every 10 ticks, if it's not already forced.
            //The turtle can move, so we need to do that.
            if (AdvancedPeripheralsConfig.enableChunkyTurtle) {
                if (!turtle.getWorld().isClientSide && !loadedChunks.contains(turtle.getWorld().getChunk(turtle.getPosition()).getPos()))
                    forceChunk(turtle, turtle.getWorld().getChunk(turtle.getPosition()).getPos(), true);
            }
        }
    }

    public boolean forceChunk(@NotNull ITurtleAccess turtle, ChunkPos chunkPos, boolean load) {
        boolean forced = ChunkManager.INSTANCE.forceChunk((ServerLevel) turtle.getWorld(), turtle.getPosition(), chunkPos, load);
        loadedChunks.add(chunkPos);
        return forced;
    }
}
