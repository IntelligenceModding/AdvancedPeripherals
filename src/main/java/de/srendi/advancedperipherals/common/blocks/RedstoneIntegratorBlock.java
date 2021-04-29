package de.srendi.advancedperipherals.common.blocks;

import dan200.computercraft.shared.util.RedstoneUtil;
import de.srendi.advancedperipherals.common.blocks.base.BaseTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.RedstoneIntegratorTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.DaylightDetectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

public class RedstoneIntegratorBlock extends BaseTileEntityBlock {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return TileEntityTypes.REDSTONE_INTEGRATOR.get().create();
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        return state.with(FACING, direction.rotate(state.get(FACING)));
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        RedstoneIntegratorTileEntity tileEntity = (RedstoneIntegratorTileEntity) blockAccess.getTileEntity(pos);
        return tileEntity.power[side.getIndex()];
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        RedstoneIntegratorTileEntity tileEntity = (RedstoneIntegratorTileEntity) blockAccess.getTileEntity(pos);
        return tileEntity.power[side.getIndex()];
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.with(FACING, mirror.mirror(state.get(FACING)));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Direction facing = player.getHorizontalFacing().getOpposite();
        return getDefaultState().with(FACING, facing);
    }

}
