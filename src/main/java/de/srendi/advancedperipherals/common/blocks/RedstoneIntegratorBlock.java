package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.RedstoneIntegratorEntity;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneIntegratorBlock extends BaseBlockEntityBlock {

    public RedstoneIntegratorBlock() {
        super(false, Properties.of(Material.METAL).isRedstoneConductor(Blocks::never));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return BlockEntityTypes.REDSTONE_INTEGRATOR.get().create(pos, state);
    }

    @Override
    public boolean isSignalSource(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public int getDirectSignal(@NotNull BlockState blockState, BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull Direction side) {
        BlockEntity te = blockGetter.getBlockEntity(pos);
        if (te instanceof RedstoneIntegratorEntity redstoneIntegratorTile)
            return redstoneIntegratorTile.power[side.getOpposite().get3DDataValue()];
        return 0;
    }

    @Override
    public int getSignal(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull Direction side) {
        return getDirectSignal(blockState, blockGetter, pos, side);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
        return state.setValue(APBlockEntityBlock.FACING, direction.rotate(state.getValue(APBlockEntityBlock.FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(APBlockEntityBlock.FACING, mirror.mirror(state.getValue(APBlockEntityBlock.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(APBlockEntityBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(APBlockEntityBlock.FACING, context.getNearestLookingDirection().getOpposite());
    }
}
