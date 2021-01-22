package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class RsBridge extends Block {

    public RsBridge() {
        super(Properties.create(Material.IRON).sound(SoundType.METAL).setRequiresTool().harvestTool(ToolType.PICKAXE).harvestLevel(2));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return ModList.get().isLoaded("refinedstorage");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.RS_BRIDGE.get().create();
    }
}
