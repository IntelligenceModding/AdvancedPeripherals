package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class APBlockEntityBlock<T extends BlockEntity> extends BaseBlockEntityBlock {

    private final RegistryObject<BlockEntityType<T>> tileEntity;

    public APBlockEntityBlock(RegistryObject<BlockEntityType<T>> tileEntity, Properties properties, boolean belongToTickingEntity) {
        super(belongToTickingEntity, properties);
        this.tileEntity = tileEntity;
    }

    public APBlockEntityBlock(RegistryObject<BlockEntityType<T>> tileEntity, boolean belongToTickingEntity) {
        this(tileEntity, Properties.of(Material.METAL), belongToTickingEntity);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return tileEntity != null ? tileEntity.get().create(pos, state) : null;
    }

}
