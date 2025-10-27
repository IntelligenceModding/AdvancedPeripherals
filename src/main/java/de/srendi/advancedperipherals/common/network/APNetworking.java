package de.srendi.advancedperipherals.common.network;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.toclient.OverlayModuleClientRequestPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectBulkSyncPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectClearPacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectDeletePacket;
import de.srendi.advancedperipherals.common.network.toclient.RenderableObjectSyncPacket;
import de.srendi.advancedperipherals.common.network.toclient.SaddleTurtleInfoPacket;
import de.srendi.advancedperipherals.common.network.toclient.ToastToClientPacket;
import de.srendi.advancedperipherals.common.network.toclient.UsernameToCachePacket;
import de.srendi.advancedperipherals.common.network.toserver.GlassesHotkeyPacket;
import de.srendi.advancedperipherals.common.network.toserver.OverlayModuleClientInfoPacket;
import de.srendi.advancedperipherals.common.network.toserver.RetrieveUsernamePacket;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class APNetworking {

    private static final String PROTOCOL_VERSION = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
    
    public static void init(PayloadRegistrar registrar) {
        registrar.playToClient(SaddleTurtleInfoPacket.class, SaddleTurtleInfoPacket::decode);
        registrar.playToClient(ToastToClientPacket.class, ToastToClientPacket::decode);
        registrar.playToClient(RenderableObjectSyncPacket.class, RenderableObjectSyncPacket::decode);
        registrar.playToClient(RenderableObjectDeletePacket.class, RenderableObjectDeletePacket::decode);
        registrar.playToClient(RenderableObjectClearPacket.class, RenderableObjectClearPacket::decode);
        registrar.playToClient(RenderableObjectBulkSyncPacket.class, RenderableObjectBulkSyncPacket::decode);
        registrar.playToClient(OverlayModuleClientRequestPacket.TYPE, OverlayModuleClientRequestPacket.CODEC, OverlayModuleClientRequestPacket::handle);
        registrar.playToClient(ToastToClientPacket.TYPE, ToastToClientPacket.CODEC, ToastToClientPacket::handle);
        registrar.playToClient(UsernameToCachePacket.TYPE, UsernameToCachePacket.CODEC, UsernameToCachePacket::handle);

        registrar.playToServer(GlassesHotkeyPacket.class, GlassesHotkeyPacket::decode);
        registrar.playToServer(SaddleTurtleControlPacket.class, SaddleTurtleControlPacket::decode);
        registrar.playToServer(OverlayModuleClientInfoPacket.class, OverlayModuleClientInfoPacket::decode);
        registrar.playToServer(RetrieveUsernamePacket.TYPE, RetrieveUsernamePacket.CODEC, RetrieveUsernamePacket::handle);
    }
    
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(AdvancedPeripherals.MOD_ID)
                .versioned(PROTOCOL_VERSION);
        init(registrar);
    }
    

    public static void sendTo(ServerPlayer player, CustomPacketPayload message) {
        if (!(player instanceof FakePlayer)) {
            PacketDistributor.sendToPlayer(player, message);
        }
    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }
}
