package de.srendi.advancedperipherals.common.blocks;

import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.PlayerDetectorTileEntity;
import de.srendi.advancedperipherals.common.blocks.base.TileEntityList;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PlayerDetectorBlock extends BaseBlock {

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.PLAYER_DETECTOR.get().create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        //Todo: This stuff is called twice. I need to prevent this
        if (AdvancedPeripheralsConfig.enablePlayerDetector) {
            for (TileEntity tileEntity : TileEntityList.get(worldIn).getTileEntitys(worldIn)) {
                if (tileEntity instanceof PlayerDetectorTileEntity) {
                    PlayerDetectorTileEntity entity = (PlayerDetectorTileEntity) tileEntity;
                    for (IComputerAccess computer : entity.getConnectedComputers()) {
                        computer.queueEvent("playerClick", player.getName().getString());
                        //Todo: Let the eyes glow when clicked on the detector. I already made an texture.
                    }
                }
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

}
