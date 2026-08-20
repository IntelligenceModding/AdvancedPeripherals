package de.srendi.advancedperipherals.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IAPPacket {

    void write(FriendlyByteBuf buffer);

    void handle(Supplier<NetworkEvent.Context> context);
}
