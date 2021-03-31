package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtlePlayerDetector extends BaseTurtle<PlayerDetectorPeripheral> {

    private static final ModelResourceLocation leftModel = new ModelResourceLocation("advancedperipherals:turtle_player_upgrade_left", "inventory");
    private static final ModelResourceLocation rightModel = new ModelResourceLocation("advancedperipherals:turtle_player_upgrade_right", "inventory");

    public TurtlePlayerDetector() {
        super("player_detector_turtle", "turtle.advancedperipherals.player_detector_turtle", new ItemStack(Blocks.PLAYER_DETECTOR.get()));
    }

    @Override
    protected PlayerDetectorPeripheral createPeripheral() {
        return new PlayerDetectorPeripheral("environmentDetector", tileEntity);
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
