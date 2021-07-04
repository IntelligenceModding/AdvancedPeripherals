package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanical.EndMechanicalSoulPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanical.WeakMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtleEndMechanicSoul extends BaseTurtle<WeakMechanicSoulPeripheral> {

    public TurtleEndMechanicSoul() {
        super("end_mechanic_soul_turtle", "turtle.advancedperipherals.end_mechanic_soul", new ItemStack(Items.END_MECHANIC_SOUL.get()));
    }

    @Override
    protected WeakMechanicSoulPeripheral createPeripheral() {
        return new EndMechanicalSoulPeripheral("end_mechanic_soul", null);
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
