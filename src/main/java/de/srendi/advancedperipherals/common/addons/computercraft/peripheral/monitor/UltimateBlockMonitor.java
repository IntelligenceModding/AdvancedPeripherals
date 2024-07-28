/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.monitor;

import dan200.computercraft.shared.common.BlockGeneric;
import dan200.computercraft.shared.peripheral.monitor.MonitorEdgeState;
import de.srendi.advancedperipherals.common.blocks.base.IHarvestableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UltimateBlockMonitor extends BlockGeneric implements IHarvestableBlock {
    public static final DirectionProperty ORIENTATION = DirectionProperty.create( "orientation",
        Direction.UP, Direction.DOWN, Direction.NORTH );

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final EnumProperty<MonitorEdgeState> STATE = EnumProperty.create( "state", MonitorEdgeState.class );

    public UltimateBlockMonitor( Properties settings, RegistryObject<? extends BlockEntityType<? extends UltimateMonitorEntity>> type )
    {
        super( settings, type );
        // TODO: Test underwater - do we need isSolid at all?
        registerDefaultState( getStateDefinition().any()
            .setValue( ORIENTATION, Direction.NORTH )
            .setValue( FACING, Direction.NORTH )
            .setValue( STATE, MonitorEdgeState.NONE ) );
    }

    @Override
    public TagKey<Block> getHarvestTag() {
        return Tags.Blocks.NEEDS_GOLD_TOOL;
    }

    @Override
    protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder )
    {
        builder.add( ORIENTATION, FACING, STATE );
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState mirror( BlockState state, Mirror mirrorIn )
    {
        return state.rotate( mirrorIn.getRotation( state.getValue( FACING ) ) );
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState rotate( BlockState state, Rotation rot )
    {
        return state.setValue( FACING, rot.rotate( state.getValue( FACING ) ) );
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement( BlockPlaceContext context )
    {
        float pitch = context.getPlayer() == null ? 0 : context.getPlayer().getXRot();
        Direction orientation;
        if( pitch > 66.5f )
        {
            // If the player is looking down, place it facing upwards
            orientation = Direction.UP;
        }
        else if( pitch < -66.5f )
        {
            // If they're looking up, place it down.
            orientation = Direction.DOWN;
        }
        else
        {
            orientation = Direction.NORTH;
        }

        return defaultBlockState()
            .setValue( FACING, context.getHorizontalDirection().getOpposite() )
            .setValue( ORIENTATION, orientation );
    }

    @Override
    public void setPlacedBy( @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState blockState, @Nullable LivingEntity livingEntity, @Nonnull ItemStack itemStack )
    {
        super.setPlacedBy( world, pos, blockState, livingEntity, itemStack );

        BlockEntity entity = world.getBlockEntity( pos );
        if( entity instanceof UltimateMonitorEntity monitor && !world.isClientSide )
        {
            // Defer the block update if we're being placed by another TE. See #691
            if( livingEntity == null || livingEntity instanceof FakePlayer )
            {
                monitor.updateNeighborsDeferred();
                return;
            }

            monitor.expand();
        }
    }
}
