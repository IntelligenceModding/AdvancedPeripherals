package de.srendi.advancedperipherals.common.blocks.base;

import com.refinedmods.refinedstorage.common.detector.DetectorBlockEntity;
import de.srendi.advancedperipherals.common.blocks.blockentities.EnergyDetectorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class APBlockEntityBlock<T extends BlockEntity> extends BaseBlockEntityBlock {

    private final DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> tileEntity;

    public APBlockEntityBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> tileEntity, Properties properties, boolean belongToTickingEntity) {
        super(belongToTickingEntity, properties);
        this.tileEntity = tileEntity;
    }

    public APBlockEntityBlock(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> tileEntity, boolean belongToTickingEntity) {
        this(tileEntity, Properties.of().sound(SoundType.METAL).mapColor(DyeColor.GRAY), belongToTickingEntity);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return tileEntity != null ? tileEntity.get().create(pos, state) : null;
    }

    @Override
    public void onNeighborChange(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);

        BlockEntity blockEntity = level.getBlockEntity(pos);

        //TODO: Do we still need this
        /*
        if(blockEntity instanceof BaseDetectorEntity<?,?,?> energyDetector)
            energyDetector.invalidateStorages();
        */
    }
}
