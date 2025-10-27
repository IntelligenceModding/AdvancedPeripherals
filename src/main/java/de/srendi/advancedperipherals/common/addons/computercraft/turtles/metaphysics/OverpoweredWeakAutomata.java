package de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics.OverpoweredWeakAutomataCorePeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.turtle.ClockwiseAnimatedTurtleUpgrade;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OverpoweredWeakAutomata extends ClockwiseAnimatedTurtleUpgrade<OverpoweredWeakAutomataCorePeripheral> {

    public OverpoweredWeakAutomata(ItemStack stack) {
        super(CCRegistration.ID.OP_WEAK_AUTOMATA, stack);
    }

    @Override
    public ModelResourceLocation getLeftModel() {
        return null;
    }

    @Override
    public ModelResourceLocation getRightModel() {
        return null;
    }

    @Override
    protected OverpoweredWeakAutomataCorePeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new OverpoweredWeakAutomataCorePeripheral(turtle, side);
    }

    @Override
    public UpgradeType<? extends ITurtleUpgrade> getType() {
        return CCRegistration.OP_WEAK_TURTLE.get();
    }
}
