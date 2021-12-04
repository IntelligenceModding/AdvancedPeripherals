package de.srendi.advancedperipherals.network.messages;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.HudOverlayHandler;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UpdateHudCanvasMessage {
    private static final String LIST = "list";
    private List<ARRenderAction> canvas;

    public UpdateHudCanvasMessage(List<ARRenderAction> canvas) {
        this.canvas = canvas;
    }

    public static UpdateHudCanvasMessage decode(FriendlyByteBuf buf) {
        ByteBufInputStream streamin = new ByteBufInputStream(buf);
        CompoundTag nbt;
        List<ARRenderAction> canvas = new ArrayList<ARRenderAction>();
        try {
            nbt = NbtIo.read(streamin, NbtAccounter.UNLIMITED);
            ListTag list = nbt.getList(LIST, Tag.TAG_COMPOUND);
            list.forEach(x -> canvas.add(ARRenderAction.deserialize((CompoundTag) x)));
        } catch (IOException e) {
            AdvancedPeripherals.LOGGER.error("Failed to decode UpdateHudCanvasMessage: {}", e.getMessage());
            e.printStackTrace();
        }
        return new UpdateHudCanvasMessage(canvas);
    }

    public static void encode(UpdateHudCanvasMessage mes, FriendlyByteBuf buf) {
        ByteBufOutputStream stream = new ByteBufOutputStream(buf);
        CompoundTag nbt = new CompoundTag();
        ListTag list = new ListTag();
        mes.getCanvas().forEach(x -> list.add(x.serializeNBT()));
        nbt.put(LIST, list);
        try {
            NbtIo.write(nbt, stream);
        } catch (IOException e) {
            AdvancedPeripherals.LOGGER.error("Failed to encode UpdateHudCanvasMessage: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void handle(UpdateHudCanvasMessage mes, Supplier<NetworkEvent.Context> cont) {
        cont.get().enqueueWork(() -> {
            HudOverlayHandler.updateCanvas(mes.getCanvas());
        });
        cont.get().setPacketHandled(true);
    }

    public List<ARRenderAction> getCanvas() {
        return canvas;
    }
}
