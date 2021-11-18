package de.srendi.advancedperipherals.common.blocks;

import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.blocks.base.BaseTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.PlayerDetectorTile;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
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

public class PlayerDetectorBlock extends BaseTileEntityBlock {

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return TileEntityTypes.PLAYER_DETECTOR.get().create();
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (APConfig.PERIPHERALS_CONFIG.ENABLE_PLAYER_DETECTOR.get()) {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof PlayerDetectorTile) {
                PlayerDetectorTile entity = (PlayerDetectorTile) tileEntity;
                for (IComputerAccess computer : entity.getConnectedComputers()) {
                    computer.queueEvent("playerClick", player.getName().getString());
                    //Todo: Let the eyes glow when clicked on the detector.
                }
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
