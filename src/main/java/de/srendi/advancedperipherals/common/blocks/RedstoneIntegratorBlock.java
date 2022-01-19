package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.RedstoneIntegratorTile;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneIntegratorBlock extends APTileEntityBlock<RedstoneIntegratorTile> {

    public RedstoneIntegratorBlock() {
        super(TileEntityTypes.REDSTONE_INTEGRATOR, Properties.of(Material.METAL).isRedstoneConductor(Blocks::never));
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

}
