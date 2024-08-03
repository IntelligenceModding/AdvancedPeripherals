package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class RenderableObjectDeletePacket implements IPacket {

    private final String object;

    public RenderableObjectDeletePacket(String object) {
        this.object = object;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        OverlayObjectHolder.removeObject(object);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(object);
    }

    public static RenderableObjectDeletePacket decode(FriendlyByteBuf buffer) {
        return new RenderableObjectDeletePacket(buffer.readUtf());
    }
}
