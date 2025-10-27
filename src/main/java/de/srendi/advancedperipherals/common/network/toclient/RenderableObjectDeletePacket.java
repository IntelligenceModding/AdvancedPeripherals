package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RenderableObjectDeletePacket implements IAPPacket {

    public static final CustomPacketPayload.Type<RenderableObjectDeletePacket> TYPE = new Type<>(AdvancedPeripherals.getRL("renderableobjectdelete"));

    private final int object;

    public RenderableObjectDeletePacket(int object) {
        this.object = object;
    }

    public RenderableObjectDeletePacket(RegistryFriendlyByteBuf buffer) {
        this.object = buffer.readInt();
    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayObjectHolder.removeObject(object);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(object);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
