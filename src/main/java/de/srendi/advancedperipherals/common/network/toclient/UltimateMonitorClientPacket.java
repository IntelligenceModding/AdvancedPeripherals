/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.monitor.UltimateMonitorEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.terminal.UltimateTerminalState;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;

public class UltimateMonitorClientPacket implements IPacket
{
    private final BlockPos pos;
    private final UltimateTerminalState state;

    public UltimateMonitorClientPacket( BlockPos pos, UltimateTerminalState state )
    {
        this.pos = pos;
        this.state = state;
    }

    @Override
    public void encode( @Nonnull FriendlyByteBuf buf )
    {
        buf.writeBlockPos( pos );
        state.write( buf );
    }

    @Override
    public void handle( NetworkEvent.Context context )
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if( player == null || player.level == null ) return;

        BlockEntity te = player.level.getBlockEntity( pos );
        if( !(te instanceof UltimateMonitorEntity monitor) ) return;

        monitor.read( state );
    }

    public static UltimateMonitorClientPacket decode(FriendlyByteBuf buffer) {
        return new UltimateMonitorClientPacket(buffer.readBlockPos(), new UltimateTerminalState(buffer));
    }
}
