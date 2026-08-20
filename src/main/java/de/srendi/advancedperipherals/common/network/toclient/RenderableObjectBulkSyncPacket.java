package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.Collection;
import java.util.function.Supplier;

public class RenderableObjectBulkSyncPacket implements IAPPacket {

    private final int count;
    private final FriendlyByteBuf data;

    public RenderableObjectBulkSyncPacket(Collection<OverlayObject> objects) {
        this.count = objects.size();
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        for (OverlayObject object : objects) {
            buf.writeVarInt(object.getId());
            object.encodeUpdated(buf);
        }
        this.data = buf;
    }

    public RenderableObjectBulkSyncPacket(FriendlyByteBuf buffer) {
        this.count = buffer.readVarInt();
        int size = buffer.readVarInt();
        this.data = new FriendlyByteBuf(Unpooled.buffer(size, size));
        buffer.readBytes(this.data, size);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
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
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.count);
        this.data.readerIndex(0);
        buffer.writeVarInt(this.data.readableBytes());
        buffer.writeBytes(this.data);
    }
}
