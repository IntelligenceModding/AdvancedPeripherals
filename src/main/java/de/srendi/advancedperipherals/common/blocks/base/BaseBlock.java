package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBlock extends ContainerBlock {

    public BaseBlock() {
        super(Properties.create(Material.IRON).hardnessAndResistance(1, 5).harvestLevel(0).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).setRequiresTool());
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        //Used for the lua function getName()
        worldIn.getTileEntity(pos).getTileData().putString("CustomName", stack.getDisplayName().getString());
        TileEntityList.get(worldIn).setTileEntity(worldIn, pos);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);
        TileEntityList.get((World) worldIn).setTileEntity((World) worldIn, pos);
    }
}
