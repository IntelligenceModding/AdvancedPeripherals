package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.screens.KeyboardScreen;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class KeyboardMouseCapturePacket implements IAPPacket {

    public static final Type<KeyboardMouseCapturePacket> TYPE = new Type<>(AdvancedPeripherals.getRL("keyboard_mouse_capture"));

    private final boolean enable;

    public KeyboardMouseCapturePacket(boolean enable) {
        this.enable = enable;
    }

    public KeyboardMouseCapturePacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    @Override
    public void handle(IPayloadContext context) {
        if (!(Minecraft.getInstance().screen instanceof KeyboardScreen screen)) {
            return;
        }
        screen.setCaptureMouse(this.enable);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.enable);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
