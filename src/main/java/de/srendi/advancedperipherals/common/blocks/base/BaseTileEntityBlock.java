package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BaseTileEntityBlock extends BaseBlock {

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        //Used for the lua function getName()
        worldIn.getTileEntity(pos).getTileData().putString("CustomName", stack.getDisplayName().getString());
        TileEntityList.get(worldIn).setTileEntity(worldIn, pos, true);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);
        TileEntityList.get((World) worldIn).setTileEntity((World) worldIn, pos, false);
    }

}
