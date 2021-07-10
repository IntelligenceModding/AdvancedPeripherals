package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredHusbandryMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredWeakMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.WeakMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TurtleOverpoweredHusbandryMechanicSoul extends ModelTransformingTurtle<OverpoweredHusbandryMechanicSoulPeripheral> {
    public TurtleOverpoweredHusbandryMechanicSoul() {
        super("overpowered_husbandry_mechanic_soul_turtle", "turtle.advancedperipherals.overpowered_husbandry_mechanic_soul", new ItemStack(Items.OVERPOWERED_HUSBANDRY_MECHANIC_SOUL.get()));
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
    protected OverpoweredHusbandryMechanicSoulPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new OverpoweredHusbandryMechanicSoulPeripheral("overpoweredHusbandryMechanicSoul", turtle, side);
    }
}
