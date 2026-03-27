package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBlockEntityBlock extends BaseBlock implements EntityBlock {
    public BaseBlockEntityBlock() {
        this(Properties.of().sound(SoundType.METAL).mapColor(DyeColor.GRAY).strength(1, 5).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops());
    }

    public BaseBlockEntityBlock(Properties properties) {
        super(properties, BlockTags.NEEDS_IRON_TOOL);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof VarNameable nameable && stack.getItem() instanceof NameTagItem) {
            if (level.isClientSide()) {
                return ItemInteractionResult.SUCCESS;
            }
            nameable.setName(stack.get(DataComponents.CUSTOM_NAME));
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        MenuProvider namedContainerProvider = this.getMenuProvider(state, level, pos);
        if (namedContainerProvider != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(namedContainerProvider, pos);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (state.is(newState.getBlock())) {
            return;
        }
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof Container container) {
            Containers.dropContents(level, pos, container);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level0, @NotNull BlockState state0, @NotNull BlockEntityType<T> type) {
        return (level, pos, state, entity) -> {
            if (entity instanceof IPeripheralBlockEntity blockEntity) {
                blockEntity.handleTick(level, state, type);
            }
        };
    }

    @Deprecated
    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity instanceof IInventoryMenuBlock menuProvider ? menuProvider : null;
    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
