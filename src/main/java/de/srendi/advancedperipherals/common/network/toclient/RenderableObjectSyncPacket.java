package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public class RenderableObjectSyncPacket implements IAPPacket {
    private final int id;
    private final FriendlyByteBuf data;

    public RenderableObjectSyncPacket(OverlayObject object) {
        this.id = object.getId();
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        object.encodeUpdated(buf);
        this.data = buf;
    }

    public RenderableObjectSyncPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readVarInt();
        int size = buffer.readVarInt();
        this.data = new FriendlyByteBuf(Unpooled.buffer(size, size));
        buffer.readBytes(this.data, size);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        OverlayObject object = OverlayObjectHolder.getObject(this.id);
        if (object == null) {
            AdvancedPeripherals.debug(Level.ERROR, "Received update packet for unknown overlay object {}", this.id);
            return;
        }
        context.get().enqueueWork(() -> {
            this.data.readerIndex(0);
            object.decodeUpdated(this.data);
        });
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.id);
        this.data.readerIndex(0);
        buffer.writeVarInt(this.data.readableBytes());
        buffer.writeBytes(this.data);
    }
}
