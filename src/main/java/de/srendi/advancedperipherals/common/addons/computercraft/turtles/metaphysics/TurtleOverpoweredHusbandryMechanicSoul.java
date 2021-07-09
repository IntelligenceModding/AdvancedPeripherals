package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredHusbandryMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredWeakMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtleOverpoweredHusbandryMechanicSoul extends ModelTransformingTurtle<OverpoweredHusbandryMechanicSoulPeripheral> {
    public TurtleOverpoweredHusbandryMechanicSoul() {
        super("overpowered_husbandry_mechanic_soul_turtle", "turtle.advancedperipherals.overpowered_husbandry_mechanic_soul", new ItemStack(Items.OVERPOWERED_HUSBANDRY_MECHANIC_SOUL.get()));
    }

    @Override
    protected OverpoweredHusbandryMechanicSoulPeripheral createPeripheral() {
        return new OverpoweredHusbandryMechanicSoulPeripheral("overpoweredHusbandryMechanicSoul", null);
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return null;
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return null;
    }
}
