package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.screens.KeyboardScreen;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KeyboardMouseCapturePacket implements IAPPacket {

    private final boolean enable;

    public KeyboardMouseCapturePacket(boolean enable) {
        this.enable = enable;
    }

    public KeyboardMouseCapturePacket(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        if (!(Minecraft.getInstance().screen instanceof KeyboardScreen screen)) {
            return;
        }
        screen.setCaptureMouse(this.enable);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.enable);
    }
}
