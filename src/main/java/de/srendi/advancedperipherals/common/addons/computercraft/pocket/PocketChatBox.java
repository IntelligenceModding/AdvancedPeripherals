package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePocket;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PocketChatBox extends BasePocket<ChatBoxPeripheral> {

    public PocketChatBox() {
        super(new ResourceLocation("advancedperipherals", "chatty_pocket"), "pocket.advancedperipherals.chatty_pocket", Blocks.CHAT_BOX);
    }

    @Nullable
    @Override
    public ChatBoxPeripheral getPeripheral(IPocketAccess access) {
        return new ChatBoxPeripheral("chatBox", access);
    }

}
