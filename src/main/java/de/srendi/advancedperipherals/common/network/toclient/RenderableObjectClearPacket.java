package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class RenderableObjectClearPacket implements IPacket {

    public RenderableObjectClearPacket() {

    }

    @Override
    public void handle(NetworkEvent.Context context) {
        OverlayObjectHolder.clear();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {

    }

    public static RenderableObjectClearPacket decode(FriendlyByteBuf buffer) {
        return new RenderableObjectClearPacket();
    }
}
