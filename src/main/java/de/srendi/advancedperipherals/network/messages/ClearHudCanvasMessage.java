package de.srendi.advancedperipherals.network.messages;

import de.srendi.advancedperipherals.client.HudOverlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class ClearHudCanvasMessage {

    public ClearHudCanvasMessage() {

    }

    public static ClearHudCanvasMessage decode(FriendlyByteBuf buf) {
        return new ClearHudCanvasMessage();
    }

    public static void encode(ClearHudCanvasMessage mes, FriendlyByteBuf buf) {

    }

    public static void handle(ClearHudCanvasMessage mes, Supplier<NetworkEvent.Context> cont) {
        cont.get().enqueueWork(HudOverlayHandler::clearCanvas);
        cont.get().setPacketHandled(true);
    }
}
