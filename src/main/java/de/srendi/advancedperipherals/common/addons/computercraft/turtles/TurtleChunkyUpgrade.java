package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChunkyPeripheral;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtleChunkyUpgrade extends PeripheralTurtleUpgrade<ChunkyPeripheral> {
    private int updateTick = 0;

    public TurtleChunkyUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, stack);
    }

    @Override
    public ModelResourceLocation getLeftModel() {
        return null; // Null, the turtle uses the chunk controller item model. See BaseTurtle.java
    }

    @Override
    public ModelResourceLocation getRightModel() {
        return null;
    }

    @Override
    protected ChunkyPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new ChunkyPeripheral(turtle, side);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        // Add a chunk to the Chunk Manager every 10 ticks, if it's not already forced.
        // The turtle can move, so we need to do that.
        super.update(turtle, side);
        if (APConfig.PERIPHERALS_CONFIG.enableChunkyTurtle.get()) {
            // TODO: turtle will stop work when crossing chunks if update every 10 ticks
            // updateTick++;
            // if (updateTick < 10) {
            //     return;
            // }
            // updateTick = 0;
            IPeripheral peripheral = turtle.getPeripheral(side);
            if (peripheral instanceof ChunkyPeripheral chunkyPeripheral) {
                chunkyPeripheral.updateChunkState();
            }
        }
    }
}
