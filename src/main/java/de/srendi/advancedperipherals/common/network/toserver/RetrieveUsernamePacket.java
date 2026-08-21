package de.srendi.advancedperipherals.common.network.toserver;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.network.toclient.UsernameToCachePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class RetrieveUsernamePacket implements IAPPacket {

    public UUID uuid;

    public RetrieveUsernamePacket(UUID uuid) {
        this.uuid = uuid;
    }

    public RetrieveUsernamePacket(FriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        GameProfile gameProfile = player.serverLevel().getServer().getProfileCache().get(uuid).orElse(null);
        if (gameProfile == null) {
            return;
        }
        APNetworking.sendToPlayer(player, new UsernameToCachePacket(gameProfile.getId(), gameProfile.getName()));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
    }
}
