package de.srendi.advancedperipherals.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IAPPacket {

    void write(FriendlyByteBuf buffer);

    void handle(NetworkEvent.Context context);

    default void handle1(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        this.handle(ctx);
        ctx.setPacketHandled(true);
    }
}
