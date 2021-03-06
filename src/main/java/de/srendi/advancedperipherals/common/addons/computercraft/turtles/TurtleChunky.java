package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChunkyPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TurtleChunky extends BaseTurtle<ChunkyPeripheral>{

    private List<ChunkPos> loadedChunks = new ArrayList<>();

    public TurtleChunky() {
        super("chunky_turtle", "turtle.advancedperipherals.chunky_turtle", new ItemStack(Items.CHUNK_CONTROLLER.get()));
    }

    @Override
    protected ChunkyPeripheral createPeripheral() {
        return new ChunkyPeripheral("chunky", tileEntity);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        if(!loadedChunks.contains(turtle.getWorld().getChunk(turtle.getPosition()).getPos())) {
            forceChunk(turtle.getWorld().getChunk(turtle.getPosition()).getPos(), true);
        }
    }

    public boolean forceChunk(ChunkPos chunkPos, boolean load) {
        boolean forced = ChunkManager.INSTANCE.forceChunk((ServerWorld) tileEntity.getWorld(), tileEntity.getPos(), chunkPos, load);

        if(forced) {
            loadedChunks.add(chunkPos);
        }

        return forced;
    }
}
