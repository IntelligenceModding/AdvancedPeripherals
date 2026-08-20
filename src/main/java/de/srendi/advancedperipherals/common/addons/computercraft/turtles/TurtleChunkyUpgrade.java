package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChunkyPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtleChunkyUpgrade extends PeripheralTurtleUpgrade<ChunkyPeripheral> {
    public TurtleChunkyUpgrade(ItemStack stack) {
        super(CCRegistration.ID.Turtle.CHUNKY, stack);
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
}
