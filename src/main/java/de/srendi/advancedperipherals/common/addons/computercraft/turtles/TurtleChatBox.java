package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TurtleChatBox extends BaseTurtle<ChatBoxPeripheral> {

    private int tick;
    private static final ModelResourceLocation model = new ModelResourceLocation("computercraft:turtle_advanced", "inventory");

    @Override
    protected ChatBoxPeripheral createPeripheral() {
        return new ChatBoxPeripheral("chatBox", tileEntity);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        //Sync the tick of the peripherals and the turtle
        if (peripheral.getTick() == 0) {
            tick = 0;
        }
        tick++;
        peripheral.setTick(tick);
    }

}