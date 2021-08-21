package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredHusbandryAutomataCorePeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OverpoweredHusbandryAutomata extends ModelTransformingTurtle<OverpoweredHusbandryAutomataCorePeripheral> {
    public OverpoweredHusbandryAutomata() {
        super("overpowered_husbandry_automata", "turtle.advancedperipherals.overpowered_husbandry_automata", new ItemStack(Items.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get()));
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
    protected OverpoweredHusbandryAutomataCorePeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new OverpoweredHusbandryAutomataCorePeripheral(turtle, side);
    }
}
