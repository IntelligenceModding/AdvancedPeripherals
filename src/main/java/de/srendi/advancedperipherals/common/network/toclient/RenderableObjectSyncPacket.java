package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.RenderableObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class RenderableObjectSyncPacket implements IPacket {

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
        return new RenderableObjectSyncPacket(RenderableObject.decode(buffer));
    }
}
