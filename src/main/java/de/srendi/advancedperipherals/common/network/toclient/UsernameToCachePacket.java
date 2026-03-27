package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UsernameToCachePacket implements IAPPacket {

    public static final Type<UsernameToCachePacket> TYPE = new Type<>(AdvancedPeripherals.getRL("username_to_cache"));

    public UUID uuid;
    public String username;

    public UsernameToCachePacket(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UsernameToCachePacket(RegistryFriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.username = buffer.readUtf();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUtf(username);
    }

    @Override
    public void handle(IPayloadContext context) {
        ClientUUIDCache.putUsername(uuid, username);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
