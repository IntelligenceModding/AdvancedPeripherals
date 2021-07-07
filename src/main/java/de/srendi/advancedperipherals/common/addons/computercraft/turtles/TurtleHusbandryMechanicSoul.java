package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic.EndMechanicalSoulPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic.HusbandryMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic.WeakMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtleHusbandryMechanicSoul extends BaseTurtle<HusbandryMechanicSoulPeripheral> {

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
