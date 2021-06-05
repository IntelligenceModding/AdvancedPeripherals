package de.srendi.advancedperipherals.network.messages;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.HudOverlayHandler;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static UpdateHudCanvasMessage decode(PacketBuffer buf) {
        ByteBufInputStream streamin = new ByteBufInputStream(buf);
        CompoundNBT nbt;
        List<ARRenderAction> canvas = new ArrayList<ARRenderAction>();
        try {
            nbt = CompressedStreamTools.read(streamin, NBTSizeTracker.UNLIMITED);
            ListNBT list = nbt.getList(LIST, NBT.TAG_COMPOUND);
            list.forEach(x -> canvas.add(ARRenderAction.deserialize((CompoundNBT) x)));
        } catch (IOException e) {
            AdvancedPeripherals.LOGGER.error("Failed to decode UpdateHudCanvasMessage: {}", e.getMessage());
            e.printStackTrace();
        }
        return new UpdateHudCanvasMessage(canvas);
    }

    public static void encode(UpdateHudCanvasMessage mes, PacketBuffer buf) {
        ByteBufOutputStream stream = new ByteBufOutputStream(buf);
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        mes.getCanvas().forEach(x -> list.add(x.serializeNBT()));
        nbt.put(LIST, list);
        try {
            CompressedStreamTools.write(nbt, stream);
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
