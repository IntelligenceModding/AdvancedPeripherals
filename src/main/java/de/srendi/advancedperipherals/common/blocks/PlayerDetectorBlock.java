package de.srendi.advancedperipherals.common.blocks;

import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.PlayerDetectorTile;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PlayerDetectorBlock extends APTileEntityBlock<PlayerDetectorTile> {

    public PlayerDetectorBlock() {
        super(TileEntityTypes.PLAYER_DETECTOR);
    }

    public PlayerDetectorBlock() {
        super(false);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TileEntityTypes.PLAYER_DETECTOR.get().create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (APConfig.PERIPHERALS_CONFIG.ENABLE_PLAYER_DETECTOR.get()) {
            BlockEntity tileEntity = levelIn.getBlockEntity(pos);
            if (tileEntity instanceof PlayerDetectorTile entity) {
                for (IComputerAccess computer : entity.getConnectedComputers()) {
                    computer.queueEvent("playerClick", player.getName().getString());
                    //Todo: Let the eyes glow when clicked on the detector.
                }
            }
        }
        return super.use(state, levelIn, pos, player, handIn, hit);
    }

}
