package de.srendi.advancedperipherals.network.toclient;

import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.network.base.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class UsernameToCachePacket implements IPacket {

    public UUID uuid;
    public String username;

    public UsernameToCachePacket(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ClientUUIDCache.putUsername(uuid, username);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUtf(username);
    }

    public static UsernameToCachePacket decode(FriendlyByteBuf buffer) {
        return new UsernameToCachePacket(buffer.readUUID(), buffer.readUtf());
    }
}
