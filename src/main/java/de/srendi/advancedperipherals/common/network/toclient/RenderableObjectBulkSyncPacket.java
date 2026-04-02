package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectFactoryRegistry;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class RenderableObjectBulkSyncPacket implements IAPPacket {

    public static final Type<RenderableObjectBulkSyncPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("renderable_object_bulk_sync"));

    private final UUID player;
    private final Collection<RenderableObject> objects;

    public RenderableObjectBulkSyncPacket(UUID player, Collection<RenderableObject> objects) {
        this.player = player;
        this.objects = objects;
    }

    public RenderableObjectBulkSyncPacket(RegistryFriendlyByteBuf buffer) {
        this.player = buffer.readUUID();
        int size = buffer.readVarInt();
        List<RenderableObject> objects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int typeId = buffer.readVarInt();
            RenderableObject object = ObjectFactoryRegistry.buildObject(typeId, this.player);
            object.decode(buffer);
            objects.add(object);
        }
        this.objects = objects;
    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayObjectHolder.addOrUpdateObjects(objects);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(this.player);
        buffer.writeVarInt(objects.size());
        for (RenderableObject object : objects) {
            buffer.writeVarInt(object.getTypeId());
            object.encode(buffer);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
