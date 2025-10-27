package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.CompassPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtleCompassUpgrade extends PeripheralTurtleUpgrade<CompassPeripheral> {

    public TurtleCompassUpgrade(ItemStack stack) {
        super(CCRegistration.ID.COMPASS_TURTLE, stack);
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
    protected CompassPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new CompassPeripheral(turtle, side);
    }

    @Override
    public UpgradeType<? extends ITurtleUpgrade> getType() {
        return CCRegistration.COMPASS_TURTLE.get();
    }
}
