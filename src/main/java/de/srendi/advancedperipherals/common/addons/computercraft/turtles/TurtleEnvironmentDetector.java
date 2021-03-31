package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtleEnvironmentDetector extends BaseTurtle<EnvironmentDetectorPeripheral> {

    private static final ModelResourceLocation leftModel = new ModelResourceLocation("advancedperipherals:turtle_environment_upgrade_left", "inventory");
    private static final ModelResourceLocation rightModel = new ModelResourceLocation("advancedperipherals:turtle_environment_upgrade_right", "inventory");

    public TurtleEnvironmentDetector() {
        super("environment_detector_turtle", "turtle.advancedperipherals.environment_detector_turtle", new ItemStack(Blocks.ENVIRONMENT_DETECTOR.get()));
    }

    @Override
    protected EnvironmentDetectorPeripheral createPeripheral() {
        return new EnvironmentDetectorPeripheral("environmentDetector", tileEntity);
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return leftModel;
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return rightModel;
    }
}
