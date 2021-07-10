package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ModelTransformingTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredEndMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredHusbandryMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TurtleOverpoweredEndMechanicSoul extends ModelTransformingTurtle<OverpoweredEndMechanicSoulPeripheral> {
    public TurtleOverpoweredEndMechanicSoul() {
        super("overpowered_end_mechanic_soul_turtle", "turtle.advancedperipherals.overpowered_end_mechanic_soul", new ItemStack(Items.OVERPOWERED_END_MECHANIC_SOUL.get()));
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
    protected OverpoweredEndMechanicSoulPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new OverpoweredEndMechanicSoulPeripheral("overpoweredEndMechanicSoul", turtle, side);
    }
}
