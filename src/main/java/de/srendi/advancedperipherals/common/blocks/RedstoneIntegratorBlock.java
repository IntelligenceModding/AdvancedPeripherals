package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.BaseTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.RedstoneIntegratorTile;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneIntegratorBlock extends BaseTileEntityBlock {

    public RedstoneIntegratorBlock() {
        super(Properties.of(Material.METAL).isRedstoneConductor(Blocks::never));
    }

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
    public boolean isSignalSource(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public int getDirectSignal(@NotNull BlockState blockState, IBlockReader blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        TileEntity te = blockAccess.getBlockEntity(pos);
        if (te instanceof RedstoneIntegratorTile)
            return ((RedstoneIntegratorTile) te).power[side.getOpposite().get3DDataValue()];
        return 0;
    }

    @Override
    public int getSignal(@NotNull BlockState blockState, @NotNull IBlockReader blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        return getDirectSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        return state.setValue(APTileEntityBlock.FACING, direction.rotate(state.getValue(APTileEntityBlock.FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(APTileEntityBlock.FACING, mirror.mirror(state.getValue(APTileEntityBlock.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(APTileEntityBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(APTileEntityBlock.FACING, context.getNearestLookingDirection().getOpposite());
    }
}
