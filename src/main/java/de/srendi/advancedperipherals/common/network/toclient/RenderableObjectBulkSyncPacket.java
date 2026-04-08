package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.APRegistries;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import net.minecraft.core.Registry;
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
    private final Collection<OverlayObject> objects;

    public RenderableObjectBulkSyncPacket(UUID player, Collection<OverlayObject> objects) {
        this.player = player;
        this.objects = objects;
    }

    public RenderableObjectBulkSyncPacket(RegistryFriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = buffer.registryAccess().registryOrThrow(APRegistries.OVERLAY_OBJECTS);
        this.player = buffer.readUUID();
        int size = buffer.readVarInt();
        List<OverlayObject> objects = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int typeId = buffer.readVarInt();
            OverlayObject object = registry.byIdOrThrow(typeId).createClient(this.player);
            object.decode(buffer);
            objects.add(object);
        }
        this.objects = objects;
    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayObjectHolder.addOrUpdateObjects(this.objects);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = buffer.registryAccess().registryOrThrow(APRegistries.OVERLAY_OBJECTS);
        buffer.writeUUID(this.player);
        buffer.writeVarInt(this.objects.size());
        for (OverlayObject object : this.objects) {
            buffer.writeVarInt(registry.getIdOrThrow(object.getType()));
            object.encode(buffer);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
