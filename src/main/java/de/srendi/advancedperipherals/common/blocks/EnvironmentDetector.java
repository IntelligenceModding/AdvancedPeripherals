package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.blocks.tileentity.TileEntityList;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

public class EnvironmentDetector extends Block {

    public EnvironmentDetector() {
        super(Properties.create(Material.IRON)
                .hardnessAndResistance(1, 5)
                .harvestLevel(0)
                .sound(SoundType.METAL)
                .harvestTool(ToolType.AXE)
                .setRequiresTool());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.ENVIRONMENT_DETECTOR.get().create();
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
        TileEntityList.get().setTileEntity(pos);
    }
}
