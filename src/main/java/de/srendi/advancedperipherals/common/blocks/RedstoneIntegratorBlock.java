package de.srendi.advancedperipherals.common.blocks;

import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.util.RedstoneUtil;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.RedstoneIntegratorEntity;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneIntegratorBlock extends BaseBlockEntityBlock {

    public RedstoneIntegratorBlock() {
        super(false, Properties.of(Material.METAL).isRedstoneConductor(APBlocks::never));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return APBlockEntityTypes.REDSTONE_INTEGRATOR.get().create(pos, state);
    }

    @Override
    public boolean isSignalSource(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public int getDirectSignal(@NotNull BlockState blockState, BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull Direction side) {
        BlockEntity te = blockGetter.getBlockEntity(pos);
        if (te instanceof RedstoneIntegratorEntity redstoneIntegratorTile) {
            return redstoneIntegratorTile.getOutput(side.getOpposite());
        }
        return 0;
    }

    @Override
    public int getSignal(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull Direction side) {
        return getDirectSignal(blockState, blockGetter, pos, side);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof RedstoneIntegratorEntity redstoneIntegratorTile) {
            for (Direction direction : Direction.values()) {
                if (pos.relative(direction).equals(neighborPos)) {
                    FrontAndTop orientation = redstoneIntegratorTile.getBlockState().getValue(ORIENTATION);
                    ComputerSide side = CoordUtil.getComputerSide(orientation, direction);
                    redstoneIntegratorTile.setInput(side, RedstoneUtil.getRedstoneInput(world, neighborPos, direction));
                    return;
                }
            }
        }
    }
}
