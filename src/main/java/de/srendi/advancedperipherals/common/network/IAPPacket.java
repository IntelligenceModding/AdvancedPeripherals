package de.srendi.advancedperipherals.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public interface IAPPacket extends CustomPacketPayload {

    void write(@NotNull RegistryFriendlyByteBuf buffer);

    void handle(@NotNull IPayloadContext context);
}
