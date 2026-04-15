package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Level;

import java.util.UUID;

public class RenderableObjectSyncPacket implements IAPPacket {

    public static final CustomPacketPayload.Type<RenderableObjectSyncPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("renderable_object_sync"));

    private final UUID player;
    private final int id;
    private final RegistryFriendlyByteBuf data;

    public RenderableObjectSyncPacket(UUID player, OverlayObject object) {
        this.player = player;
        this.id = object.getId();
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), ServerLifecycleHooks.getCurrentServer().registryAccess());
        object.encodeUpdated(buf);
        this.data = buf;
    }

    public RenderableObjectSyncPacket(RegistryFriendlyByteBuf buffer) {
        this.player = buffer.readUUID();
        this.id = buffer.readVarInt();
        int size = buffer.readVarInt();
        this.data = new RegistryFriendlyByteBuf(Unpooled.buffer(size, size), buffer.registryAccess());
        buffer.readBytes(this.data, size);
    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayObject object = OverlayObjectHolder.getObject(this.id);
        if (object == null) {
            AdvancedPeripherals.debug(Level.ERROR, "Received update packet for unknown overlay object {}", this.id);
            return;
        }
        context.enqueueWork(() -> {
            this.data.readerIndex(0);
            object.decodeUpdated(this.data);
        });
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(this.player);
        buffer.writeVarInt(this.id);
        this.data.readerIndex(0);
        buffer.writeVarInt(this.data.readableBytes());
        buffer.writeBytes(this.data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
