package de.srendi.advancedperipherals.common.blocks;

import org.jetbrains.annotations.Nullable;

import de.srendi.advancedperipherals.common.blocks.base.BaseTileEntityBlock;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ARControllerBlock extends BaseTileEntityBlock {
	@Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileEntityTypes.AR_CONTROLLER.get().create();
    }
}
