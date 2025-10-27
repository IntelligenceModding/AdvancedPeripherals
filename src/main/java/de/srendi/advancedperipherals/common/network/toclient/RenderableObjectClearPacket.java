package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RenderableObjectClearPacket implements IAPPacket {

    public static final CustomPacketPayload.Type<RenderableObjectClearPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("renderableobjectclear"));

    public RenderableObjectClearPacket() {

    }

    public RenderableObjectClearPacket(RegistryFriendlyByteBuf buffer) {

    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayObjectHolder.clear();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
