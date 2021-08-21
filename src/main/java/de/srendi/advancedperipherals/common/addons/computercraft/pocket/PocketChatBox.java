package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePocket;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PocketChatBox extends BasePocket<ChatBoxPeripheral> {
    public static final ResourceLocation ID = new ResourceLocation(AdvancedPeripherals.MOD_ID, "chatty_pocket");

    public PocketChatBox() {
        super(ID, Blocks.CHAT_BOX);
    }

    @Nullable
    @Override
    public ChatBoxPeripheral getPeripheral(IPocketAccess access) {
        return new ChatBoxPeripheral(access);
    }

}
