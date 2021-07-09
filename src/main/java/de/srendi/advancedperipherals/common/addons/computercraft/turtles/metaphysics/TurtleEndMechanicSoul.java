package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.EndMechanicalSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtleEndMechanicSoul extends ModelTransformingTurtle<EndMechanicalSoulPeripheral> {

    public TurtleEndMechanicSoul() {
        super("end_mechanic_soul_turtle", "turtle.advancedperipherals.end_mechanic_soul", new ItemStack(Items.END_MECHANIC_SOUL.get()));
    }

    @Override
    protected EndMechanicalSoulPeripheral createPeripheral() {
        return new EndMechanicalSoulPeripheral("endMechanicSoul", null);
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
