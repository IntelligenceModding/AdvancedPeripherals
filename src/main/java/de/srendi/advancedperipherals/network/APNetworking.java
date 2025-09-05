package de.srendi.advancedperipherals.network;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.network.toclient.CraftingCompleteToastPacket;
import de.srendi.advancedperipherals.network.toclient.ToastToClientPacket;
import de.srendi.advancedperipherals.network.toclient.UsernameToCachePacket;
import de.srendi.advancedperipherals.network.toserver.RetrieveUsernamePacket;
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
        registrar.playToClient(ToastToClientPacket.TYPE, ToastToClientPacket.CODEC, ToastToClientPacket::handle);
        registrar.playToClient(CraftingCompleteToastPacket.TYPE, CraftingCompleteToastPacket.CODEC, CraftingCompleteToastPacket::handle);
        registrar.playToClient(UsernameToCachePacket.TYPE, UsernameToCachePacket.CODEC, UsernameToCachePacket::handle);

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
