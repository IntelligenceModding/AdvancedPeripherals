package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class APBlockEntityBlock<T extends BlockEntity> extends BaseBlockEntityBlock {

    private final DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> tileEntity;

    public APBlockEntityBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> tileEntity, Properties properties) {
        super(properties);
        this.tileEntity = tileEntity;
    }

    public APBlockEntityBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> tileEntity) {
        this(
            tileEntity,
            Properties.of()
                .sound(SoundType.METAL)
                .mapColor(MapColor.METAL)
                .strength(1, 5)
                .noOcclusion()
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return tileEntity != null ? tileEntity.get().create(pos, state) : null;
    }
}
