package de.srendi.advancedperipherals.network.toserver;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.network.APNetworking;
import de.srendi.advancedperipherals.network.base.IPacket;
import de.srendi.advancedperipherals.network.toclient.UsernameToCachePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Optional;
import java.util.UUID;

public class RetrieveUsernamePacket implements IPacket {

    public UUID uuid;
    public UUID requester;

    public RetrieveUsernamePacket(UUID uuid, UUID requester) {
        this.uuid = uuid;
        this.requester = requester;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        Optional<GameProfile> gameProfile = ServerLifecycleHooks.getCurrentServer().getProfileCache().get(uuid);
        ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(requester);

        // The player left the server before the packet could be handled
        if (player == null)
            return;

        if (gameProfile.isEmpty())
            return;
        APNetworking.sendTo(new UsernameToCachePacket(gameProfile.get().getId(), gameProfile.get().getName()), player);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUUID(requester);
    }

    public static RetrieveUsernamePacket decode(FriendlyByteBuf buffer) {
        return new RetrieveUsernamePacket(buffer.readUUID(), buffer.readUUID());
    }
}
