package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.item.ItemStack;

public class TurtlePlayerDetector extends BaseTurtle<PlayerDetectorPeripheral> {

    public TurtlePlayerDetector() {
        super("player_detector_turtle", "turtle.advancedperipherals.player_detector_turtle", new ItemStack(Blocks.PLAYER_DETECTOR.get()));
    }

    @Override
    protected PlayerDetectorPeripheral createPeripheral() {
        return new PlayerDetectorPeripheral("environmentDetector", tileEntity);
    }
}
