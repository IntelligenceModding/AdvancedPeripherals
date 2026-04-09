package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.PlayerDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDetectorBlock extends APBlockEntityBlock<PlayerDetectorEntity> {

    public PlayerDetectorBlock() {
        super(APBlockEntityTypes.PLAYER_DETECTOR);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return APBlockEntityTypes.PLAYER_DETECTOR.get().create(pos, state);
    }

    @Override
    @NotNull
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!APConfig.PERIPHERALS_CONFIG.enablePlayerDetector.get()) {
            return super.useWithoutItem(state, level, pos, player, hit);
        }
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof PlayerDetectorEntity entity) {
            level.playSound(player, pos, SoundEvents.NETHER_WOOD_BUTTON_CLICK_ON, SoundSource.BLOCKS);
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            PlayerDetectorPeripheral peripheral = entity.getPeripheral();
            if (peripheral != null) {
                peripheral.forEachConnectedComputers(
                    (computer) -> computer.queueEvent(
                        CCEvents.PLAYER_CLICK,
                        computer.getAttachmentName(),
                        player.getUUID().toString(),
                        player.getGameProfile().getName()
                    )
                );
            }
            return InteractionResult.CONSUME;
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

}
