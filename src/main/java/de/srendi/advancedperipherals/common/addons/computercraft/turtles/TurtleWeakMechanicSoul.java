package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic.WeakMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtleWeakMechanicSoul extends ModelTransformingTurtle<WeakMechanicSoulPeripheral> {

    public TurtleWeakMechanicSoul() {
        super("weak_mechanic_soul_turtle", "turtle.advancedperipherals.weak_mechanic_soul", new ItemStack(Items.WEAK_MECHANIC_SOUL.get()));
    }

    @Override
    protected WeakMechanicSoulPeripheral createPeripheral() {
        return new WeakMechanicSoulPeripheral("weakMechanicSoul", null);
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
