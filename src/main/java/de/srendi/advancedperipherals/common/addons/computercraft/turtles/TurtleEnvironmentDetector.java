package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.item.ItemStack;

public class TurtleEnvironmentDetector extends BaseTurtle<EnvironmentDetectorPeripheral> {

    public TurtleEnvironmentDetector() {
        super("environment_detector_turtle", "turtle.advancedperipherals.environment_detector_turtle", new ItemStack(Blocks.ENVIRONMENT_DETECTOR.get()));
    }

    @Override
    protected EnvironmentDetectorPeripheral createPeripheral() {
        return new EnvironmentDetectorPeripheral("environmentDetector", tileEntity);
    }
}
