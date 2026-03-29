package de.srendi.advancedperipherals.common.network.toserver;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.network.toclient.UsernameToCachePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RetrieveUsernamePacket implements IAPPacket {

    public static final Type<RetrieveUsernamePacket> TYPE = new Type<>(AdvancedPeripherals.getRL("retrieve_username"));

    public UUID uuid;

    public RetrieveUsernamePacket(UUID uuid) {
        this.uuid = uuid;
    }

    public RetrieveUsernamePacket(RegistryFriendlyByteBuf buffer) {
        this.uuid = buffer.readUUID();
    }

    @Override
    public void handle(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        GameProfile gameProfile = player.serverLevel().getServer().getProfileCache().get(uuid).orElse(null);
        if (gameProfile == null) {
            return;
        }
        PacketDistributor.sendToPlayer(player, new UsernameToCachePacket(gameProfile.getId(), gameProfile.getName()));
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
