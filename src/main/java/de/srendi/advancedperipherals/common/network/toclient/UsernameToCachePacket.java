package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class UsernameToCachePacket implements IAPPacket {

    public UUID uuid;
    public String username;

    public UsernameToCachePacket(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UsernameToCachePacket(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.username = buffer.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUtf(username);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ClientUUIDCache.putUsername(uuid, username);
    }
}
