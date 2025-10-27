package de.srendi.advancedperipherals.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EmptyCodec<V extends IAPPacket> implements StreamCodec<RegistryFriendlyByteBuf, V> {

    private final Supplier<V> packetSupplier;

    public EmptyCodec(Supplier<V> packetSupplier) {
        this.packetSupplier = packetSupplier;
    }


    @NotNull
    @Override
    public V decode(@NotNull RegistryFriendlyByteBuf buffer) {
        return packetSupplier.get();
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer, @NotNull V value) {

    }
}
