package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.BaseTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.RedstoneIntegratorTile;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class RedstoneIntegratorBlock extends BaseTileEntityBlock {

    public RedstoneIntegratorBlock() {
        super(false, Properties.of(Material.METAL).isRedstoneConductor(Blocks::never));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TileEntityTypes.REDSTONE_INTEGRATOR.get().create(pos, state);
    }

    @Override
    public boolean isSignalSource(BlockState p_60571_) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        RedstoneIntegratorTile tileEntity = (RedstoneIntegratorTile) blockAccess.getBlockEntity(pos);
        return tileEntity.power[side.getOpposite().get3DDataValue()];
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return getDirectSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
        return state.setValue(APTileEntityBlock.FACING, direction.rotate(state.getValue(APTileEntityBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(APTileEntityBlock.FACING, mirror.mirror(state.getValue(APTileEntityBlock.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(APTileEntityBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        Direction facing = player.getDirection().getOpposite();
        return defaultBlockState().setValue(APTileEntityBlock.FACING, facing);
    }
}
