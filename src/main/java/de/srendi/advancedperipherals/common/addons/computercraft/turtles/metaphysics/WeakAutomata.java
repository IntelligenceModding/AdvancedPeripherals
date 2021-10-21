package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.WeakAutomataCorePeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import de.srendi.advancedperipherals.lib.turtle.ClockwiseAnimatedTurtleUpgrade;
import org.jetbrains.annotations.NotNull;

public class WeakAutomata extends ClockwiseAnimatedTurtleUpgrade<WeakAutomataCorePeripheral> {
    public static final ResourceLocation ID = new ResourceLocation(AdvancedPeripherals.MOD_ID, "weak_automata");

    public WeakAutomata() {
        super(ID, new ItemStack(Items.WEAK_AUTOMATA_CORE.get()));
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
    protected WeakAutomataCorePeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new WeakAutomataCorePeripheral(turtle, side);
    }
}
