package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.HusbandryMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredEndMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TurtleHusbandryMechanicSoul extends ModelTransformingTurtle<HusbandryMechanicSoulPeripheral> {

    public TurtleHusbandryMechanicSoul() {
        super("husbandry_mechanic_soul_turtle", "turtle.advancedperipherals.husbandry_mechanic_soul", new ItemStack(Items.HUSBANDRY_MECHANIC_SOUL.get()));
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
    protected HusbandryMechanicSoulPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new HusbandryMechanicSoulPeripheral("husbandryMechanicSoul", turtle, side);
    }
}
