package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChunky;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = AdvancedPeripherals.MOD_ID)
public class ChunkManager implements ForgeChunkManager.LoadingValidationCallback {

    public static final ChunkManager INSTANCE = new ChunkManager();

    private boolean init = true;

    public static void register() {
        ForgeChunkManager.setForcedChunkLoadingCallback(AdvancedPeripherals.MOD_ID, INSTANCE);
    }

    @Override
    public void validateTickets(ServerWorld world, ForgeChunkManager.TicketHelper ticketHelper) {
        ticketHelper.getBlockTickets().forEach((blockPos, chunks) -> {
            if(world.getTileEntity(blockPos) != null) {
                TileEntity tileEntity = world.getTileEntity(blockPos);
                if (tileEntity instanceof TileTurtle) {
                    TileTurtle tileTurtle = (TileTurtle) tileEntity;
                    if (tileTurtle.getUpgrade(TurtleSide.RIGHT) instanceof TurtleChunky || tileTurtle.getUpgrade(TurtleSide.LEFT) instanceof TurtleChunky) {
                        TurtleChunky turtle =  (TurtleChunky) (tileTurtle.getUpgrade(TurtleSide.RIGHT) instanceof TurtleChunky ? tileTurtle.getUpgrade(TurtleSide.RIGHT) : tileTurtle.getUpgrade(TurtleSide.LEFT));
                        for (Long chunk : chunks.getSecond()) {
                            turtle.forceChunk(new ChunkPos(chunk), true);
                        }
                    } else {
                        AdvancedPeripherals.Debug("Trying remove forced chunk " + blockPos);
                        ticketHelper.removeAllTickets(blockPos);
                    }
                }
            }
        });
    }

    public void onAboutStart() {
        init = true;
    }

    public void onServerStopping() {
        init = false;
    }

    @SubscribeEvent
    public static void onServerAboutStart(FMLServerAboutToStartEvent event) {
        INSTANCE.onAboutStart();
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        INSTANCE.onServerStopping();
    }

    public boolean forceChunk(ServerWorld world, BlockPos pos, ChunkPos chunkPos, boolean add) {
        if(init) {
            if(add)
                //AdvancedPeripherals.Debug("Trying force chunk " + pos);
            if(!add)
                AdvancedPeripherals.Debug("Trying unforce chunk " + pos);
            return ForgeChunkManager.forceChunk(world, AdvancedPeripherals.MOD_ID, pos, chunkPos.x, chunkPos.z, add, true);
        }
        return false;
    }
}
