package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectDecodeRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.network.NetworkEvent;

public class RenderableObjectSyncPacket implements IAPPacket {

    private final RenderableObject object;

    public RenderableObjectSyncPacket(RenderableObject object) {
        this.object = object;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        OverlayObjectHolder.addOrUpdateObject(object);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        object.encode(buffer);
    }

    public static RenderableObjectSyncPacket decode(FriendlyByteBuf buffer) {
        int id = buffer.readInt();
        RenderableObject decodedObject = ObjectDecodeRegistry.getObject(id, buffer);
        return new RenderableObjectSyncPacket(decodedObject);
    }
}
