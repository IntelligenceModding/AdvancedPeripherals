package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class RenderableObjectClearPacket implements IAPPacket {
    public RenderableObjectClearPacket() {
    }

    public RenderableObjectClearPacket(FriendlyByteBuf buffer) {
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        OverlayObjectHolder.clear();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
    }
}
