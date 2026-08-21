package de.srendi.advancedperipherals.common.network;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.toclient.KeyboardMouseCapturePacket;
import de.srendi.advancedperipherals.common.network.toclient.NarrateToClientPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectAddPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectBulkAddPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectBulkSyncPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectClearPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectDeletePacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectSyncPacket;
import de.srendi.advancedperipherals.common.network.toclient.SaddleTurtleInfoPacket;
import de.srendi.advancedperipherals.common.network.toclient.ToastToClientPacket;
import de.srendi.advancedperipherals.common.network.toclient.UsernameToCachePacket;
import de.srendi.advancedperipherals.common.network.toserver.GlassesHotkeyPacket;
import de.srendi.advancedperipherals.common.network.toserver.KeyboardMouseClickPacket;
import de.srendi.advancedperipherals.common.network.toserver.KeyboardMouseMovePacket;
import de.srendi.advancedperipherals.common.network.toserver.KeyboardMouseScrollPacket;
import de.srendi.advancedperipherals.common.network.toserver.OverlayModuleClientInfoPacket;
import de.srendi.advancedperipherals.common.network.toserver.PlayerInteractionPacket;
import de.srendi.advancedperipherals.common.network.toserver.RetrieveUsernamePacket;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Function;

@EventBusSubscriber
public class APNetworking {
    private static final String PROTOCOL_VERSION = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
    private static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(AdvancedPeripherals.getRL("main_channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int id = 0;

    public static void init() {
        registerServerToClient(KeyboardMouseCapturePacket.class, KeyboardMouseCapturePacket::new);
        registerServerToClient(NarrateToClientPacket.class, NarrateToClientPacket::new);
        registerServerToClient(RenderableObjectAddPacket.class, RenderableObjectAddPacket::new);
        registerServerToClient(RenderableObjectBulkAddPacket.class, RenderableObjectBulkAddPacket::new);
        registerServerToClient(RenderableObjectBulkSyncPacket.class, RenderableObjectBulkSyncPacket::new);
        registerServerToClient(RenderableObjectClearPacket.class, RenderableObjectClearPacket::new);
        registerServerToClient(RenderableObjectDeletePacket.class, RenderableObjectDeletePacket::new);
        registerServerToClient(RenderableObjectSyncPacket.class, RenderableObjectSyncPacket::new);
        registerServerToClient(SaddleTurtleInfoPacket.class, SaddleTurtleInfoPacket::new);
        registerServerToClient(ToastToClientPacket.class, ToastToClientPacket::new);
        registerServerToClient(UsernameToCachePacket.class, UsernameToCachePacket::new);

        registerClientToServer(GlassesHotkeyPacket.class, GlassesHotkeyPacket::new);
        registerClientToServer(KeyboardMouseClickPacket.class, KeyboardMouseClickPacket::new);
        registerClientToServer(KeyboardMouseMovePacket.class, KeyboardMouseMovePacket::new);
        registerClientToServer(KeyboardMouseScrollPacket.class, KeyboardMouseScrollPacket::new);
        registerClientToServer(OverlayModuleClientInfoPacket.class, OverlayModuleClientInfoPacket::new);
        registerClientToServer(PlayerInteractionPacket.class, PlayerInteractionPacket::new);
        registerClientToServer(RetrieveUsernamePacket.class, RetrieveUsernamePacket::new);
        registerClientToServer(SaddleTurtleControlPacket.class, SaddleTurtleControlPacket::new);
    }

    public static <MSG extends IAPPacket> void registerServerToClient(Class<MSG> packet, Function<FriendlyByteBuf, MSG> decode) {
        NETWORK_CHANNEL.registerMessage(id++, packet, IAPPacket::write, decode, IAPPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static <MSG extends IAPPacket> void registerClientToServer(Class<MSG> packet, Function<FriendlyByteBuf, MSG> decode) {
        NETWORK_CHANNEL.registerMessage(id++, packet, IAPPacket::write, decode, IAPPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
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
    public static void sendToPlayer(ServerPlayer player, Object msg) {
        if (!(player instanceof FakePlayer)) {
            NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
        }
    }

    public static void sendPacketToAll(Object packet) {
        NETWORK_CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static void sendToAllAround(Object mes, ResourceKey<Level> dim, BlockPos pos, int radius) {
        NETWORK_CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), radius, dim)), mes);
    }

    public static void sendToAllInWorld(Object mes, ServerLevel world) {
        NETWORK_CHANNEL.send(PacketDistributor.DIMENSION.with(world::dimension), mes);
    }
}
