package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.HusbandryMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtleHusbandryMechanicSoul extends ModelTransformingTurtle<HusbandryMechanicSoulPeripheral> {

    public TurtleHusbandryMechanicSoul() {
        super("husbandry_mechanic_soul_turtle", "turtle.advancedperipherals.husbandry_mechanic_soul", new ItemStack(Items.HUSBANDRY_MECHANIC_SOUL.get()));
    }

    @Override
    protected HusbandryMechanicSoulPeripheral createPeripheral() {
        return new HusbandryMechanicSoulPeripheral("husbandryMechanicSoul", null);
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
