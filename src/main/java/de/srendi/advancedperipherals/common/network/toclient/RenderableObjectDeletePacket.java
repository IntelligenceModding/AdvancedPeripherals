package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class RenderableObjectDeletePacket implements IAPPacket {
    private final int object;

    public RenderableObjectDeletePacket(int object) {
        this.object = object;
    }

    public RenderableObjectDeletePacket(FriendlyByteBuf buffer) {
        this.object = buffer.readInt();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        OverlayObjectHolder.removeObject(object);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(object);
    }
}
