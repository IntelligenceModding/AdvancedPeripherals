package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
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

    private final boolean belongToTickingEntity;

    public BaseBlockEntityBlock(boolean belongToTickingEntity) {
        this(belongToTickingEntity, Properties.of().sound(SoundType.METAL).mapColor(DyeColor.GRAY).strength(1, 5).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops());
    }

    public BaseBlockEntityBlock(boolean belongToTickingEntity, Properties properties) {
        super(properties, BlockTags.NEEDS_IRON_TOOL);
        this.belongToTickingEntity = belongToTickingEntity;
    }

    @NotNull
    @Override
    public ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof VarNameable nameable && stack.getItem() instanceof NameTagItem) {
            nameable.setName(stack.get(DataComponents.CUSTOM_NAME));
            return ItemInteractionResult.SUCCESS;
        }
        MenuProvider namedContainerProvider = this.getMenuProvider(state, level, pos);
        if (namedContainerProvider != null && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(namedContainerProvider, pos);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof Container container) {
                Containers.dropContents(worldIn, pos, container);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void setPlacedBy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.getBlockEntity(pos) == null)
            return;
        // //Used for the lua function getName()
        // if (worldIn.getBlockEntity(pos) instanceof BaseContainerBlockEntity blockEntity) {
        //     blockEntity.name = stack.getHoverName();
        // }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide || !belongToTickingEntity) return null;
        return (level1, blockPos, blockState, entity) -> {
            if (entity instanceof IPeripheralTileEntity blockEntity) {
                blockEntity.handleTick(level, state, type);
            }
        };
    }


    @Deprecated
    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        return blockentity instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
