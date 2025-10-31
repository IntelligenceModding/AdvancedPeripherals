package de.srendi.advancedperipherals.common.network.toserver;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.network.toclient.UsernameToCachePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class RetrieveUsernamePacket implements IAPPacket {

    public static final CustomPacketPayload.Type<RetrieveUsernamePacket> TYPE = new Type<>(AdvancedPeripherals.getRL("retrieve_username"));

    public UUID uuid;
    public UUID requester;

    public RetrieveUsernamePacket(UUID uuid, UUID requester) {
        this.uuid = uuid;
        this.requester = requester;
    }

    public RetrieveUsernamePacket(RegistryFriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
        this.requester = buffer.readUUID();
    }

    @Override
    public void handle(IPayloadContext context) {
        Optional<GameProfile> gameProfile = ServerLifecycleHooks.getCurrentServer().getProfileCache().get(uuid);
        ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(requester);

        // The player left the server before the packet could be handled
        if (player == null)
            return;

        if (gameProfile.isEmpty())
            return;
        PacketDistributor.sendToPlayer(player, new UsernameToCachePacket(gameProfile.get().getId(), gameProfile.get().getName()));
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUUID(requester);
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
