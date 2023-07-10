package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class TurtleChatBoxUpgrade extends PeripheralTurtleUpgrade<ChatBoxPeripheral> {

    private long lastConsumedMessage;

    public TurtleChatBoxUpgrade(ResourceLocation id, ItemStack item) {
        super(id, item);
        lastConsumedMessage = Events.getLastChatMessageID() - 1;
    }

    @Override
    public ModelResourceLocation getLeftModel() {
        return new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_chat_box_upgrade_left"), "inventory");
    }

    @Override
    public ModelResourceLocation getRightModel() {
        return new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_chat_box_upgrade_right"), "inventory");
    }

    @Override
    protected ChatBoxPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new ChatBoxPeripheral(turtle, side);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        if (turtle.getLevel().isClientSide) return;

        if (turtle.getUpgrade(side) instanceof TurtleChatBoxUpgrade) {
            BlockEntity tile = turtle.getLevel().getBlockEntity(turtle.getPosition());
            if (tile instanceof TileTurtle tileTurtle) {
                ServerComputer computer = tileTurtle.getServerComputer();
                lastConsumedMessage = Events.traverseChatMessages(lastConsumedMessage, message -> computer.queueEvent("chat", new Object[]{message.username(), message.message(), message.uuid(), message.isHidden()}));

            }
        }
    }
}
