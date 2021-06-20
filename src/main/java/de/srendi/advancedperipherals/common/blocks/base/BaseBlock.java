package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

public abstract class BaseBlock extends ContainerBlock {

    public BaseBlock() {
        super(Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_BLUE).strength(1, 5).harvestLevel(0).sound(SoundType.METAL).noOcclusion().harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
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
