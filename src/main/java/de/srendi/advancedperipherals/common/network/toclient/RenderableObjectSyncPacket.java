package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectDecodeRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RenderableObjectSyncPacket implements IAPPacket {

    public static final CustomPacketPayload.Type<RenderableObjectSyncPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("renderableobjectsync"));

    private final RenderableObject object;

    public RenderableObjectSyncPacket(RenderableObject object) {
        this.object = object;
    }

    public RenderableObjectSyncPacket(RegistryFriendlyByteBuf buffer) {
        this.object = ObjectDecodeRegistry.getObject(buffer.readInt(), buffer);
    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayObjectHolder.addOrUpdateObject(object);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        object.encode(buffer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
