package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TurtlePlayerDetector extends BaseTurtle<PlayerDetectorPeripheral> {

    private static final ModelResourceLocation leftModel = new ModelResourceLocation("advancedperipherals:turtle_player_upgrade_left", "inventory");
    private static final ModelResourceLocation rightModel = new ModelResourceLocation("advancedperipherals:turtle_player_upgrade_right", "inventory");

    public TurtlePlayerDetector() {
        super("player_detector_turtle", "turtle.advancedperipherals.player_detector_turtle", new ItemStack(Blocks.PLAYER_DETECTOR.get()));
    }

    @Override
    protected PlayerDetectorPeripheral createPeripheral() {
        return new PlayerDetectorPeripheral("environmentDetector", (TileEntity) null);
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
