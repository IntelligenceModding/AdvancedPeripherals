package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PatternGridPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtlePatternGridUpgrade extends PeripheralTurtleUpgrade<PatternGridPeripheral> {

    public TurtlePatternGridUpgrade(ItemStack stack) {
        super(CCRegistration.ID.PATTERN_TURTLE, stack);
    }

    @Override
    public ModelResourceLocation getLeftModel() {
        return new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_pattern_upgrade_left"), "inventory");
    }

    @Override
    public ModelResourceLocation getRightModel() {
        return new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_pattern_upgrade_right"), "inventory");
    }

    @Override
    protected PatternGridPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new PatternGridPeripheral(turtle, side);
    }

    @Override
    public UpgradeType<? extends ITurtleUpgrade> getType() {
        return CCRegistration.PATTERN_TURTLE.get();
    }
}
