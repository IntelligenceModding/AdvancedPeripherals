package de.srendi.advancedperipherals.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public interface IAPPacket extends CustomPacketPayload {

    static <MSG extends IAPPacket> void handlePacket(@NotNull MSG payload, @NotNull IPayloadContext context) {
        payload.handle(context);
    }

    void handle(IPayloadContext context);
}
