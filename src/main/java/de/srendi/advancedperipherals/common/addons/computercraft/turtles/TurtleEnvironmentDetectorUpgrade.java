package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtleEnvironmentDetectorUpgrade extends PeripheralTurtleUpgrade<EnvironmentDetectorPeripheral> {

    private static final ModelResourceLocation leftModel = new ModelResourceLocation("advancedperipherals:turtle_environment_upgrade_left", "inventory");
    private static final ModelResourceLocation rightModel = new ModelResourceLocation("advancedperipherals:turtle_environment_upgrade_right", "inventory");

    public TurtleEnvironmentDetectorUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, stack);
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return leftModel;
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return rightModel;
    }

    @Override
    protected EnvironmentDetectorPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new EnvironmentDetectorPeripheral(turtle, side);
    }
}
