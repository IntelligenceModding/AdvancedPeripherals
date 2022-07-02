package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TurtleChatBoxUpgrade extends PeripheralTurtleUpgrade<ChatBoxPeripheral> {
    public static final ResourceLocation ID = new ResourceLocation(AdvancedPeripherals.MOD_ID, "chat_box_turtle");

    private static final ModelResourceLocation leftModel = new ModelResourceLocation("advancedperipherals:turtle_chat_box_upgrade_left", "inventory");
    private static final ModelResourceLocation rightModel = new ModelResourceLocation("advancedperipherals:turtle_chat_box_upgrade_right", "inventory");

    public TurtleChatBoxUpgrade() {
        super(ID, new ItemStack(Blocks.CHAT_BOX.get()));
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
        return new ChatBoxPeripheral(turtle, side);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        if (turtle.getWorld().isClientSide) return;

        if (turtle.getUpgrade(side) instanceof TurtleChatBoxUpgrade) {
            TileEntity tile = turtle.getWorld().getBlockEntity(turtle.getPosition());
            if (tile instanceof TileTurtle) {
                TileTurtle tileTurtle = (TileTurtle) tile;
                ServerComputer computer = tileTurtle.getServerComputer();
                setLastConsumedMessage(turtle, side, Events.traverseChatMessages(getLastConsumedMessage(turtle, side), message -> {
                    computer.queueEvent("chat", new Object[]{message.username, message.message,
                            message.uuid, message.isHidden});
                }));
            }
        }
    }

    private long getLastConsumedMessage(ITurtleAccess turtle, TurtleSide side) {
        if(turtle.getUpgradeNBTData(side).contains("lastConsumedMessage"))
            return turtle.getUpgradeNBTData(side).getLong("lastConsumedMessage");
        return -1;
    }

    private void setLastConsumedMessage(ITurtleAccess turtle, TurtleSide side, long message) {
        turtle.getUpgradeNBTData(side).putLong("lastConsumedMessage", message);
        turtle.updateUpgradeNBTData(side);
    }
}