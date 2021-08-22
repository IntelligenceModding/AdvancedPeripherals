package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChunkyPeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtleChunky extends BaseTurtle<ChunkyPeripheral> {
    public static final ResourceLocation ID = new ResourceLocation(AdvancedPeripherals.MOD_ID, "chunky_turtle");

    public TurtleChunky() {
        super(ID, new ItemStack(Items.CHUNK_CONTROLLER.get()));
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
        return new ChunkyPeripheral(turtle, side);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        //Add a chunk to the Chunk Manager every 10 ticks, if it's not already forced.
        //The turtle can move, so we need to do that.
        super.update(turtle, side);
        if (AdvancedPeripheralsConfig.enableChunkyTurtle) {
            IPeripheral peripheral = turtle.getPeripheral(side);
            if (peripheral instanceof ChunkyPeripheral chunkyPeripheral) {
                chunkyPeripheral.updateChunkState();
            }
        }
    }
}
