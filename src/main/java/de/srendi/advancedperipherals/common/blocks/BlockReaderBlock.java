package de.srendi.advancedperipherals.common.blocks;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.BlockReaderPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.BlockReaderEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockReaderBlock extends APBlockEntityBlock<BlockReaderEntity> {

    public BlockReaderBlock() {
        super(APBlockEntityTypes.BLOCK_READER);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return APBlockEntityTypes.BLOCK_READER.get().create(pos, state);
    }

    @Override
    @NotNull
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!APConfig.PERIPHERALS_CONFIG.enableBlockReader.get()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (!(tileEntity instanceof BlockReaderEntity entity)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        if (player.isSecondaryUseActive()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockReaderPeripheral peripheral = entity.getPeripheral();
        if (peripheral != null) {
            peripheral.forEachConnectedComputers(
                (computer) -> computer.queueEvent(
                    CCEvents.ITEM_CLICK,
                    computer.getAttachmentName(),
                    LuaConverter.itemStackToLua(stack)
                )
            );
        }
        return InteractionResult.CONSUME;
    }

}
