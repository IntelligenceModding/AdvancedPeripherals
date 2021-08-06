package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChunky;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppingEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = AdvancedPeripherals.MOD_ID)
public class ChunkManager implements ForgeChunkManager.LoadingValidationCallback {

    public static final ChunkManager INSTANCE = new ChunkManager();

    private boolean init = false;

    public static void register() {
        ForgeChunkManager.setForcedChunkLoadingCallback(AdvancedPeripherals.MOD_ID, INSTANCE);
    }

    @SubscribeEvent
    public static void onServerAboutStart(FMLServerAboutToStartEvent event) {
        INSTANCE.onAboutStart();
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        INSTANCE.onServerStopping();
    }

    @Override
    public void validateTickets(ServerLevel level, ForgeChunkManager.TicketHelper ticketHelper) {
        ticketHelper.getBlockTickets().forEach((blockPos, chunks) -> {
            if (level.getBlockEntity(blockPos) != null) {
                BlockEntity tileEntity = level.getBlockEntity(blockPos);
                if (tileEntity instanceof TileTurtle) {
                    TileTurtle tileTurtle = (TileTurtle) tileEntity;
                    if (tileTurtle.getUpgrade(TurtleSide.RIGHT) instanceof TurtleChunky || tileTurtle.getUpgrade(TurtleSide.LEFT) instanceof TurtleChunky) {
                        TurtleChunky turtle = (TurtleChunky) (tileTurtle.getUpgrade(TurtleSide.RIGHT) instanceof TurtleChunky ? tileTurtle.getUpgrade(TurtleSide.RIGHT) : tileTurtle.getUpgrade(TurtleSide.LEFT));
                        for (Long chunk : chunks.getSecond()) {
                            turtle.forceChunk(tileTurtle.getAccess(), new ChunkPos(chunk), true);
                        }
                    } else {
                        AdvancedPeripherals.debug("Trying remove forced chunk " + blockPos);
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

    public boolean forceChunk(ServerLevel world, BlockPos pos, ChunkPos chunkPos, boolean add) {
        if (init) {
            if (add)
                AdvancedPeripherals.debug("Trying force chunk " + pos);
            if (!add)
                AdvancedPeripherals.debug("Trying unforce chunk " + pos);
            return ForgeChunkManager.forceChunk(world, AdvancedPeripherals.MOD_ID, pos, chunkPos.x, chunkPos.z, add, true);
        }
        return false;
    }
}
