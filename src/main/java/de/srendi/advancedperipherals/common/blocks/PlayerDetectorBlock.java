package de.srendi.advancedperipherals.common.blocks;

import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.PlayerDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDetectorBlock extends APBlockEntityBlock<PlayerDetectorEntity> {

    public PlayerDetectorBlock() {
        super(APBlockEntityTypes.PLAYER_DETECTOR, true);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return APBlockEntityTypes.PLAYER_DETECTOR.get().create(pos, state);
    }

    @NotNull
    @Override
    public ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        queuePlayerEvent(level, pos, player.getName().getString());
        return super.useItemOn(stack, state, level, pos, player, handIn, hit);
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull BlockHitResult hitResult) {
        queuePlayerEvent(level, pos, player.getName().getString());
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    private void queuePlayerEvent(Level level, BlockPos pos, String playerName) {
        if (!APConfig.PERIPHERALS_CONFIG.enablePlayerDetector.get())
            return;
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof PlayerDetectorEntity entity) {
            for (IComputerAccess computer : entity.getConnectedComputers()) {
                computer.queueEvent("playerClick", playerName, level.dimension().location().toString());
            }
        }
    }

}
