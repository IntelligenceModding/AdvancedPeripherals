package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UsernameToCachePacket implements IAPPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, UsernameToCachePacket> CODEC = StreamCodec.of((buffer, value) -> value.write(buffer), UsernameToCachePacket::decode);

    public static final Type<UsernameToCachePacket> TYPE = new Type<>(AdvancedPeripherals.getRL("usernametocache"));

    public UUID uuid;
    public String username;

    public UsernameToCachePacket(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public static UsernameToCachePacket decode(FriendlyByteBuf buffer) {
        return new UsernameToCachePacket(buffer.readUUID(), buffer.readUtf());
    }

    @Override
    public void handle(IPayloadContext context) {
        ClientUUIDCache.putUsername(uuid, username);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUtf(username);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
