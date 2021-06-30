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
        this(Properties.of(Material.METAL).strength(1, 5).harvestLevel(0).sound(SoundType.METAL).noOcclusion().harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
    }

    public BaseBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return null;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
