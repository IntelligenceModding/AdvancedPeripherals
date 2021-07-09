package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredEndMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtleOverpoweredEndMechanicSoul extends ModelTransformingTurtle<OverpoweredEndMechanicSoulPeripheral> {
    public TurtleOverpoweredEndMechanicSoul() {
        super("overpowered_end_mechanic_soul_turtle", "turtle.advancedperipherals.overpowered_end_mechanic_soul", new ItemStack(Items.OVERPOWERED_END_MECHANIC_SOUL.get()));
    }

    @Override
    protected OverpoweredEndMechanicSoulPeripheral createPeripheral() {
        return new OverpoweredEndMechanicSoulPeripheral("overpoweredEndMechanicSoul", null);
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return null; //Null, the turtle uses the chunk controller item model. See BaseTurtle.java
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return null;
    }
}
