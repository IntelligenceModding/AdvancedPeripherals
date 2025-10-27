package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.util.ToastUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

public class ToastToClientPacket implements IAPPacket {

    public static final
    StreamCodec<RegistryFriendlyByteBuf, ToastToClientPacket> CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, packet -> packet.title,
            ComponentSerialization.STREAM_CODEC, packet -> packet.component,
            ToastToClientPacket::new);

    public static final Type<ToastToClientPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("toasttoclient"));

    private final Component title;
    private final Component component;

    public ToastToClientPacket(Component title, Component component) {
        this.title = title;
        this.component = component;
    }

    @Override
    public void handle(@NotNull IPayloadContext context) {
        // Should in the theory not happen, but safe is safe.
        if (!FMLEnvironment.dist.isClient()) {
            AdvancedPeripherals.debug("Tried to display toasts on the server, aborting.");
            return;
        }
        ToastUtil.displayToast(title, component);
    }

    @NotNull
    @Override
    public Type<ToastToClientPacket> type() {
        return TYPE;
    }
}
