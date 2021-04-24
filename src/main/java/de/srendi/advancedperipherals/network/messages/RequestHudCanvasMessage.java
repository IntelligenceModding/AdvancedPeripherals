package de.srendi.advancedperipherals.network.messages;

import de.srendi.advancedperipherals.common.blocks.tileentity.ARControllerTileEntity;
import de.srendi.advancedperipherals.network.MNetwork;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestHudCanvasMessage {

    private BlockPos blockPos;
    private String dimensionKey;

    public RequestHudCanvasMessage(BlockPos blockPos, String dimensionKey) {
        this.blockPos = blockPos;
        this.dimensionKey = dimensionKey;
    }

    public static RequestHudCanvasMessage decode(PacketBuffer buf) {
        BlockPos blockPos = buf.readBlockPos();
        String dimensionKey = buf.readString(Short.MAX_VALUE);
        return new RequestHudCanvasMessage(blockPos, dimensionKey);
    }

    public static void encode(RequestHudCanvasMessage mes, PacketBuffer buf) {
        buf.writeBlockPos(mes.getBlockPos());
        buf.writeString(mes.getDimensionKey(), Short.MAX_VALUE);
    }

    public static void handle(RequestHudCanvasMessage mes, Supplier<NetworkEvent.Context> cont) {
        cont.get().enqueueWork(() -> {
            Iterable<ServerWorld> worlds = cont.get().getSender().getServer().getWorlds();
            for (ServerWorld world : worlds) {
                if (world.getDimensionKey().toString().equals(mes.getDimensionKey())) {
                    TileEntity te = world.getTileEntity(mes.getBlockPos());
                    if (!(te instanceof ARControllerTileEntity))
                        return;
                    ARControllerTileEntity controller = (ARControllerTileEntity) te;
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
