package de.srendi.advancedperipherals.network.messages;

import de.srendi.advancedperipherals.common.blocks.blockentities.ARControllerEntity;
import de.srendi.advancedperipherals.network.MNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestHudCanvasMessage {

    private final BlockPos blockPos;
    private final String dimensionKey;

    public RequestHudCanvasMessage(BlockPos blockPos, String dimensionKey) {
        this.blockPos = blockPos;
        this.dimensionKey = dimensionKey;
    }

    public static RequestHudCanvasMessage decode(FriendlyByteBuf buf) {
        BlockPos blockPos = buf.readBlockPos();
        String dimensionKey = buf.readUtf(Short.MAX_VALUE);
        return new RequestHudCanvasMessage(blockPos, dimensionKey);
    }

    public static void encode(RequestHudCanvasMessage mes, FriendlyByteBuf buf) {
        buf.writeBlockPos(mes.getBlockPos());
        buf.writeUtf(mes.getDimensionKey(), Short.MAX_VALUE);
    }

    public static void handle(RequestHudCanvasMessage mes, Supplier<NetworkEvent.Context> cont) {
        cont.get().enqueueWork(() -> {
            Iterable<ServerLevel> worlds = cont.get().getSender().getServer().getAllLevels();
            for (ServerLevel world : worlds) {
                if (world.dimension().toString().equals(mes.getDimensionKey())) {
                    BlockEntity te = world.getBlockEntity(mes.getBlockPos());
                    if (!(te instanceof ARControllerEntity controller))
                        return;
                    MNetwork.sendTo(new UpdateHudCanvasMessage(controller.getCanvas()), cont.get().getSender());
                    break;
                }
            }
        });
        cont.get().setPacketHandled(true);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public String getDimensionKey() {
        return dimensionKey;
    }
}
