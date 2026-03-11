package de.srendi.advancedperipherals.common.blocks;

import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.PlayerDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
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
    public InteractionResult useWithoutItem(@NotNull BlockState state, Level levelIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (!APConfig.PERIPHERALS_CONFIG.enablePlayerDetector.get()) {
            return super.useWithoutItem(state, levelIn, pos, player, hit);
        }
        BlockEntity tileEntity = levelIn.getBlockEntity(pos);
        if (tileEntity instanceof PlayerDetectorEntity entity) {
            for (IComputerAccess computer : entity.getConnectedComputers()) {
                computer.queueEvent("player_click", playerName, level.dimension().location().toString());
            }
        }
        return super.useWithoutItem(state, levelIn, pos, player, hit);
    }

}
