package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.blocks.base.BaseBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.RedstoneIntegratorEntity;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneIntegratorBlock extends BaseBlockEntityBlock {

    public RedstoneIntegratorBlock() {
        super(false, Properties.of().sound(SoundType.METAL).mapColor(DyeColor.GRAY).isRedstoneConductor(Blocks::never));
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
}
