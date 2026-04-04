package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.network.toserver.OverlayModuleClientInfoPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OverlayModuleClientRequestPacket implements IAPPacket {

    public static final Type<OverlayModuleClientRequestPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("overlay_module_client_request"));

    public OverlayModuleClientRequestPacket() {
    }

    public OverlayModuleClientRequestPacket(RegistryFriendlyByteBuf buffer) {
    }

    @Override
    public void handle(IPayloadContext context) {
        OverlayModuleClientInfoPacket.sendCurrentInformation();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
