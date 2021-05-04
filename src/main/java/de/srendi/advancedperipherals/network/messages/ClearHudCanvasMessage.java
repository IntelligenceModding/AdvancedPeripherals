package de.srendi.advancedperipherals.network.messages;

import de.srendi.advancedperipherals.client.HudOverlayHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClearHudCanvasMessage {

    public ClearHudCanvasMessage() {

    }

    public static ClearHudCanvasMessage decode(PacketBuffer buf) {
        return new ClearHudCanvasMessage();
    }

    public static void encode(ClearHudCanvasMessage mes, PacketBuffer buf) {

    }

    public static void handle(ClearHudCanvasMessage mes, Supplier<NetworkEvent.Context> cont) {
        cont.get().enqueueWork(HudOverlayHandler::clearCanvas);
        cont.get().setPacketHandled(true);
    }
}
