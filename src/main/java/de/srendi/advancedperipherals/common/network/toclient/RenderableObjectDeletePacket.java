package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.network.NetworkEvent;

public class RenderableObjectDeletePacket implements IAPPacket {

    private final int object;

    public RenderableObjectDeletePacket(int object) {
        this.object = object;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        OverlayObjectHolder.removeObject(object);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(object);
    }

    public static RenderableObjectDeletePacket decode(FriendlyByteBuf buffer) {
        return new RenderableObjectDeletePacket(buffer.readInt());
    }
}
