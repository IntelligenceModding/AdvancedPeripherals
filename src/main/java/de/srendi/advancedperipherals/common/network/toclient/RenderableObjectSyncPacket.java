package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectFactoryRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class RenderableObjectSyncPacket implements IAPPacket {

    public static final CustomPacketPayload.Type<RenderableObjectSyncPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("renderable_object_sync"));

    private final UUID player;
    private final RenderableObject object;

    public RenderableObjectSyncPacket(UUID player, RenderableObject object) {
        this.player = player;
        this.object = object;
    }

    public RenderableObjectSyncPacket(RegistryFriendlyByteBuf buffer) {
        this.player = buffer.readUUID();
        int typeId = buffer.readVarInt();
        this.object = ObjectFactoryRegistry.buildObject(typeId, this.player);
        this.object.decode(buffer);
    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayObjectHolder.addOrUpdateObject(object);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(this.player);
        buffer.writeVarInt(object.getTypeId());
        object.encode(buffer);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
