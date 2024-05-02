package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBlockEntityBlock extends BaseBlock implements EntityBlock {

    private final boolean belongToTickingEntity;

    public BaseBlockEntityBlock(boolean belongToTickingEntity) {
        this(belongToTickingEntity, Properties.of(Material.METAL).strength(1, 5).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops());
    }

    public BaseBlockEntityBlock(boolean belongToTickingEntity, Properties properties) {
        super(properties, BlockTags.NEEDS_IRON_TOOL);
        this.belongToTickingEntity = belongToTickingEntity;
    }

    @NotNull
    @Override
    public abstract BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state);

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, Level levelIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (levelIn.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity tileEntity = levelIn.getBlockEntity(pos);
        if (tileEntity != null && !(tileEntity instanceof IInventoryBlock)) return InteractionResult.PASS;
        MenuProvider namedContainerProvider = this.getMenuProvider(state, levelIn, pos);
        if (namedContainerProvider != null) {
            if (!(player instanceof ServerPlayer serverPlayerEntity)) return InteractionResult.PASS;
            NetworkHooks.openScreen(serverPlayerEntity, namedContainerProvider, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof Container container) Containers.dropContents(worldIn, pos, container);
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void setPlacedBy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.getBlockEntity(pos) == null)
            return;
        //Used for the lua function getName()
        worldIn.getBlockEntity(pos).getPersistentData().putString("CustomName", stack.getDisplayName().getString());
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
        if (!(blockentity instanceof MenuProvider menuProvider)) return null;
        return menuProvider;
    }

    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
