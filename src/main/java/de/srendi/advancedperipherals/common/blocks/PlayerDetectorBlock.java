package de.srendi.advancedperipherals.common.blocks;

import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.PlayerDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class PlayerDetectorBlock extends APBlockEntityBlock<PlayerDetectorEntity> {

    public PlayerDetectorBlock() {
        super(APBlockEntityTypes.PLAYER_DETECTOR, true);
    }

    @NotNull
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return APBlockEntityTypes.PLAYER_DETECTOR.get().create(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level levelIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (!APConfig.PERIPHERALS_CONFIG.enablePlayerDetector.get())
            return super.use(state, levelIn, pos, player, handIn, hit);
        BlockEntity tileEntity = levelIn.getBlockEntity(pos);
        if (tileEntity instanceof PlayerDetectorEntity entity) {
            for (IComputerAccess computer : entity.getConnectedComputers()) {
                computer.queueEvent("playerClick", player.getName().getString(), computer.getAttachmentName());
            }
        }

        return super.use(state, levelIn, pos, player, handIn, hit);
    }

}
