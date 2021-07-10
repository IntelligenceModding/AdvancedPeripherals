package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.NotNull;

public class ChatBoxTile extends PeripheralTileEntity<ChatBoxPeripheral> {

    public ChatBoxTile() {
        this(TileEntityTypes.CHAT_BOX.get());
    }

    public ChatBoxTile(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @NotNull
    @Override
    protected ChatBoxPeripheral createPeripheral() {
        return new ChatBoxPeripheral("chatBox", this);
    }
}