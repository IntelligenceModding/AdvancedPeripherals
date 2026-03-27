package de.srendi.advancedperipherals.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IAPPacket extends CustomPacketPayload {

    void write(RegistryFriendlyByteBuf buffer);

    void handle(IPayloadContext context);
}
