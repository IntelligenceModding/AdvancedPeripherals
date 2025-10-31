package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.IAPPacket;
import de.srendi.advancedperipherals.common.network.toserver.OverlayModuleClientInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OverlayModuleClientRequestPacket implements IAPPacket {

    public static final Type<OverlayModuleClientRequestPacket> TYPE = new Type<>(AdvancedPeripherals.getRL("overlay_module_client_request"));

    public OverlayModuleClientRequestPacket() {
    }

    public OverlayModuleClientRequestPacket(FriendlyByteBuf buffer) {
    }

    @Override
    public void handle(IPayloadContext context) {
        Minecraft minecraft = Minecraft.getInstance();

        int sizeX = minecraft.getWindow().getWidth(), sizeY = minecraft.getWindow().getHeight();
        double guiScale = minecraft.getWindow().getGuiScale();

        PacketDistributor.sendToServer(new OverlayModuleClientInfoPacket(minecraft.player.getUUID(), sizeX, sizeY, guiScale));
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
