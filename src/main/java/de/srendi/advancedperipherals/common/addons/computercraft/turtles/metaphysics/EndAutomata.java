package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.EndAutomataCorePeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EndAutomata extends ModelTransformingTurtle<EndAutomataCorePeripheral> {

    public EndAutomata() {
        super("end_automata", "turtle.advancedperipherals.end_automata", new ItemStack(Items.END_AUTOMATA_CORE.get()));
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return null;
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return null;
    }

    @Override
    protected EndAutomataCorePeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new EndAutomataCorePeripheral("endAutomata", turtle, side);
    }
}
