package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
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

}
