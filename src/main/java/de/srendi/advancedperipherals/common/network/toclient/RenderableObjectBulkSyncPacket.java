package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.setup.APRegistries;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Level;

import java.util.Collection;
import java.util.UUID;

public class RenderableObjectBulkSyncPacket implements IAPPacket {

    public static final Type<RenderableObjectBulkSyncPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("renderable_object_bulk_sync"));

    private final UUID player;
    private final int count;
    private final RegistryFriendlyByteBuf data;

    public RenderableObjectBulkSyncPacket(UUID player, Collection<OverlayObject> objects) {
        this.player = player;
        this.count = objects.size();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), ServerLifecycleHooks.getCurrentServer().registryAccess());
        for (OverlayObject object : objects) {
            buf.writeVarInt(object.getId());
            object.encodeUpdated(buf);
        }
        this.data = buf;
    }

    public RenderableObjectBulkSyncPacket(RegistryFriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = buffer.registryAccess().registryOrThrow(APRegistries.OVERLAY_OBJECTS);
        this.player = buffer.readUUID();
        this.count = buffer.readVarInt();
        int size = buffer.readVarInt();
        this.data = new RegistryFriendlyByteBuf(Unpooled.buffer(size, size), buffer.registryAccess());
        buffer.readBytes(this.data, size);
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            this.data.readerIndex(0);
            for (int i = 0; i < this.count; i++) {
                int id = this.data.readVarInt();
                OverlayObject object = OverlayObjectHolder.getObject(id);
                if (object == null) {
                    AdvancedPeripherals.debug(Level.ERROR, "Received bulk update packet for unknown overlay object {}", id);
                    return;
                }
                object.decodeUpdated(this.data);
            }
        });
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        Registry<OverlayObjectType<?>> registry = buffer.registryAccess().registryOrThrow(APRegistries.OVERLAY_OBJECTS);
        buffer.writeUUID(this.player);
        buffer.writeVarInt(this.count);
        this.data.readerIndex(0);
        buffer.writeVarInt(this.data.readableBytes());
        buffer.writeBytes(this.data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
