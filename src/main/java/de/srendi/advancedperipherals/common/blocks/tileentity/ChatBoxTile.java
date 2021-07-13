package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.NotNull;

public class ChatBoxTile extends PeripheralTileEntity<ChatBoxPeripheral> implements ITickableTileEntity {

    private Long lastConsumedMessage;

    public ChatBoxTile() {
        this(TileEntityTypes.CHAT_BOX.get());
        lastConsumedMessage = Events.counter - 1;
    }

    public ChatBoxTile(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        lastConsumedMessage = Events.counter - 1;
    }

    @NotNull
    @Override
    protected ChatBoxPeripheral createPeripheral() {
        return new ChatBoxPeripheral("chatBox", this);
    }

    @Override
    public void tick() {
        lastConsumedMessage = Events.traverseChatMessages(lastConsumedMessage, message -> {
            getConnectedComputers().forEach(computer -> computer.queueEvent("chat", message.username, message.message,
                    message.uuid, message.isHidden));
        });
    }
}