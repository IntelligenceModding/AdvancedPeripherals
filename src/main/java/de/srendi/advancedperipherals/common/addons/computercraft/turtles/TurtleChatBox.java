package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtleChatBox extends BaseTurtle<ChatBoxPeripheral> {

    private int sync;

    public TurtleChatBox() {
        super("chat_box_turtle", "turtle.advancedperipherals.chat_box_turtle", new ItemStack(Blocks.CHAT_BOX.get()));
    }

    @Override
    protected ChatBoxPeripheral createPeripheral() {
        return new ChatBoxPeripheral("chatBox", tileEntity);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        //Sync the tick of the peripherals and the turtle
        if (peripheral.getTick() == 0) {
            sync = 0;
        }
        sync++;
        peripheral.setTick(sync);
    }
}