package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.screens.KeyboardScreen;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class KeyboardMouseCapturePacket implements IPacket {

    private final boolean enable;

    public KeyboardMouseCapturePacket(boolean enable) {
        this.enable = enable;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        if (!(Minecraft.getInstance().screen instanceof KeyboardScreen screen)) {
            return;
        }
        screen.setCaptureMouse(this.enable);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.enable);
    }

    public static KeyboardMouseCapturePacket decode(FriendlyByteBuf buffer) {
        return new KeyboardMouseCapturePacket(buffer.readBoolean());
    }
}
