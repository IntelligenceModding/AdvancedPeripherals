package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChunkyPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TurtleChunky extends BaseTurtle<ChunkyPeripheral> {

    protected static final Set<ChunkPos> loadedChunks = new HashSet<>();

    private int tick;

    public TurtleChunky() {
        super("chunky_turtle", "turtle.advancedperipherals.chunky_turtle", new ItemStack(Items.CHUNK_CONTROLLER.get()));
    }

    @Override
    protected ChunkyPeripheral createPeripheral() {
        return new ChunkyPeripheral("chunky", tileEntity);
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
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        tick++;
        if (tick >= 10) {
            //Add a chunk to the Chunk Manager every 10 ticks, if it's not already forced.
            //The turtle can move, so we need to do that.
            if (peripheral.isEnabled()) {
                if (!turtle.getWorld().isRemote && !loadedChunks.contains(turtle.getWorld().getChunk(turtle.getPosition()).getPos())) {
                    forceChunk(turtle.getWorld().getChunk(turtle.getPosition()).getPos(), true);
                }
                tick = 0;
            }
        }

    }

    public boolean forceChunk(ChunkPos chunkPos, boolean load) {
        boolean forced = ChunkManager.INSTANCE.forceChunk((ServerWorld) tileEntity.getWorld(), tileEntity.getPos(), chunkPos, load);
        loadedChunks.add(chunkPos);
        return forced;
    }
}
