package de.srendi.advancedperipherals.common.network;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.toclient.KeyboardMouseCapturePacket;
import de.srendi.advancedperipherals.common.network.toclient.OverlayModuleClientRequestPacket;
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
import de.srendi.advancedperipherals.common.network.toserver.RetrieveUsernamePacket;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class APNetworking {

    private static void init(PayloadRegistrar registrar) {
        registrar.playToClient(KeyboardMouseCapturePacket.TYPE, makeReader(KeyboardMouseCapturePacket::new), KeyboardMouseCapturePacket::handle);
        registrar.playToClient(OverlayModuleClientRequestPacket.TYPE, makeReader(OverlayModuleClientRequestPacket::new), OverlayModuleClientRequestPacket::handle);
        registrar.playToClient(RenderableObjectAddPacket.TYPE, makeReader(RenderableObjectAddPacket::new), RenderableObjectAddPacket::handle);
        registrar.playToClient(RenderableObjectBulkAddPacket.TYPE, makeReader(RenderableObjectBulkAddPacket::new), RenderableObjectBulkAddPacket::handle);
        registrar.playToClient(RenderableObjectBulkSyncPacket.TYPE, makeReader(RenderableObjectBulkSyncPacket::new), RenderableObjectBulkSyncPacket::handle);
        registrar.playToClient(RenderableObjectClearPacket.TYPE, makeReader(RenderableObjectClearPacket::new), RenderableObjectClearPacket::handle);
        registrar.playToClient(RenderableObjectDeletePacket.TYPE, makeReader(RenderableObjectDeletePacket::new), RenderableObjectDeletePacket::handle);
        registrar.playToClient(RenderableObjectSyncPacket.TYPE, makeReader(RenderableObjectSyncPacket::new), RenderableObjectSyncPacket::handle);
        registrar.playToClient(SaddleTurtleInfoPacket.TYPE, makeReader(SaddleTurtleInfoPacket::new), SaddleTurtleInfoPacket::handle);
        registrar.playToClient(ToastToClientPacket.TYPE, makeReader(ToastToClientPacket::new), ToastToClientPacket::handle);
        registrar.playToClient(UsernameToCachePacket.TYPE, makeReader(UsernameToCachePacket::new), UsernameToCachePacket::handle);

        registrar.playToServer(GlassesHotkeyPacket.TYPE, makeReader(GlassesHotkeyPacket::new), GlassesHotkeyPacket::handle);
        registrar.playToServer(KeyboardMouseClickPacket.TYPE, makeReader(KeyboardMouseClickPacket::new), KeyboardMouseClickPacket::handle);
        registrar.playToServer(KeyboardMouseMovePacket.TYPE, makeReader(KeyboardMouseMovePacket::new), KeyboardMouseMovePacket::handle);
        registrar.playToServer(KeyboardMouseScrollPacket.TYPE, makeReader(KeyboardMouseScrollPacket::new), KeyboardMouseScrollPacket::handle);
        registrar.playToServer(OverlayModuleClientInfoPacket.TYPE, makeReader(OverlayModuleClientInfoPacket::new), OverlayModuleClientInfoPacket::handle);
        registrar.playToServer(RetrieveUsernamePacket.TYPE, makeReader(RetrieveUsernamePacket::new), RetrieveUsernamePacket::handle);
        registrar.playToServer(SaddleTurtleControlPacket.TYPE, makeReader(SaddleTurtleControlPacket::new), SaddleTurtleControlPacket::handle);
    }

    private static <T extends IAPPacket> StreamCodec<RegistryFriendlyByteBuf, T> makeReader(StreamDecoder<RegistryFriendlyByteBuf, T> reader) {
        return StreamCodec.ofMember(IAPPacket::write, reader);
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final String version = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
        final PayloadRegistrar registrar = event.registrar(AdvancedPeripherals.MOD_ID).versioned(version);
        init(registrar);
    }
}
