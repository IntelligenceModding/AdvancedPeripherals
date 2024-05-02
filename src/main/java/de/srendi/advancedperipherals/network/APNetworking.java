package de.srendi.advancedperipherals.network;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.network.base.IPacket;
import de.srendi.advancedperipherals.network.toclient.ToastToClientPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Function;

public class APNetworking {
    private static final String PROTOCOL_VERSION = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
    private static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(AdvancedPeripherals.MOD_ID, "main_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int id = 0;

    public static void init() {
        registerServerToClient(ToastToClientPacket.class, ToastToClientPacket::decode);
    }

    public static <MSG extends IPacket> void registerServerToClient(Class<MSG> packet, Function<FriendlyByteBuf, MSG> decode) {
        NETWORK_CHANNEL.registerMessage(id++, packet, IPacket::encode, decode, IPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static <MSG extends IPacket> void registerClientToServer(Class<MSG> packet, Function<FriendlyByteBuf, MSG> decode) {
        NETWORK_CHANNEL.registerMessage(id++, packet, IPacket::encode, decode, IPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    /**
     * Sends a packet to the server.<p>
     * Must be called Client side.
     */
    public static void sendToServer(Object msg) {
        NETWORK_CHANNEL.sendToServer(msg);
    }

    /**
     * Send a packet to a specific player.<p>
     * Must be called Server side.
     */
    public static void sendTo(Object msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer)) {
            NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
        }
    }

    public static void sendPacketToAll(Object packet) {
        NETWORK_CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static ClientboundBlockEntityDataPacket createTEUpdatePacket(BlockEntity tile) {
        return ClientboundBlockEntityDataPacket.create(tile);
    }

    public static void sendToAllAround(Object mes, ResourceKey<Level> dim, BlockPos pos, int radius) {
        NETWORK_CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), radius, dim)), mes);
    }

    public static void sendToAllInWorld(Object mes, ServerLevel world) {
        NETWORK_CHANNEL.send(PacketDistributor.DIMENSION.with(world::dimension), mes);
    }
}
