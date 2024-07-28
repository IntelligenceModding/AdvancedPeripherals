/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.monitor;

import dan200.computercraft.ComputerCraft;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toclient.UltimateMonitorClientPacket;
import de.srendi.advancedperipherals.common.addons.computercraft.terminal.UltimateTerminalState;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Queue;

@Mod.EventBusSubscriber( modid = AdvancedPeripherals.MOD_ID )
public final class UltimateMonitorWatcher
{
    private static final Queue<UltimateMonitorEntity> watching = new ArrayDeque<>();

    private UltimateMonitorWatcher()
    {
    }

    static void enqueue( UltimateMonitorEntity monitor )
    {
        if( monitor.enqueued ) return;

        monitor.enqueued = true;
        monitor.cached = null;
        watching.add( monitor );
    }

    @SubscribeEvent
    public static void onWatch( ChunkWatchEvent.Watch event )
    {
        // Find all origin monitors who are not already on the queue and send the
        // monitor data to the player.
        for( BlockEntity te : event.getChunk().getBlockEntities().values() )
        {
            if( !(te instanceof UltimateMonitorEntity monitor) ) continue;

            UltimateServerMonitor serverMonitor = getMonitor( monitor );
            if( serverMonitor == null || monitor.enqueued ) continue;

            UltimateTerminalState state = getState( monitor, serverMonitor );
            APNetworking.sendToClient( new UltimateMonitorClientPacket( monitor.getBlockPos(), state ), event.getPlayer() );
        }
    }

    @SubscribeEvent
    public static void onTick( TickEvent.ServerTickEvent event )
    {
        // Find all enqueued monitors and send their contents to all nearby players.

        if( event.phase != TickEvent.Phase.END ) return;

        long limit = ComputerCraft.monitorBandwidth;
        boolean obeyLimit = limit > 0;

        UltimateMonitorEntity tile;
        while( (!obeyLimit || limit > 0) && (tile = watching.poll()) != null )
        {
            tile.enqueued = false;
            UltimateServerMonitor monitor = getMonitor( tile );
            if( monitor == null ) continue;

            BlockPos pos = tile.getBlockPos();
            Level world = tile.getLevel();
            if( !(world instanceof ServerLevel) ) continue;

            UltimateTerminalState state = getState( tile, monitor );
            APNetworking.sendToAllTracking( new UltimateMonitorClientPacket( pos, state ), world, pos );

            limit -= state.size();
        }
    }

    private static UltimateServerMonitor getMonitor( UltimateMonitorEntity monitor )
    {
        return !monitor.isRemoved() && monitor.getXIndex() == 0 && monitor.getYIndex() == 0 ? monitor.getCachedServerMonitor() : null;
    }

    private static UltimateTerminalState getState( UltimateMonitorEntity tile, UltimateServerMonitor monitor ) {
        UltimateTerminalState state = tile.cached;
        if (state == null) {
            state = tile.cached = new UltimateTerminalState(monitor.getTerminal());
        }
        return state;
    }
}
