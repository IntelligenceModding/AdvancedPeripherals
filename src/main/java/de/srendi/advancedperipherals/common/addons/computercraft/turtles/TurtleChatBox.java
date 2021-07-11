package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

public class TurtleChatBox extends BaseTurtle<ChatBoxPeripheral> {

    private static final ModelResourceLocation leftModel = new ModelResourceLocation("advancedperipherals:turtle_chat_box_upgrade_left", "inventory");
    private static final ModelResourceLocation rightModel = new ModelResourceLocation("advancedperipherals:turtle_chat_box_upgrade_right", "inventory");
    private long lastConsumedMessage;

    public TurtleChatBox() {
        super("chat_box_turtle", "turtle.advancedperipherals.chat_box_turtle", new ItemStack(Blocks.CHAT_BOX.get()));
        lastConsumedMessage = Events.counter - 1;
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
    protected ChatBoxPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new ChatBoxPeripheral("chatBox", turtle, side);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        if (turtle.getWorld().isClientSide) return;

        if (turtle.getUpgrade(side) instanceof TurtleChatBox) {
            TileEntity tile = turtle.getWorld().getBlockEntity(turtle.getPosition());
            if (tile instanceof TileTurtle) {
                TileTurtle tileTurtle = (TileTurtle) tile;
                ServerComputer computer = tileTurtle.getServerComputer();
                lastConsumedMessage = Events.traverseChatMessages(lastConsumedMessage, message -> {
                    computer.queueEvent("chat", new Object[]{message.getLeft(), message.getRight()});
                });
            }
        }
    }
}