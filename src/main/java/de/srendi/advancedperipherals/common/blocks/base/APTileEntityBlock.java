package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.fmllegacy.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class APTileEntityBlock<T extends BlockEntity> extends BaseTileEntityBlock {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    private RegistryObject<BlockEntityType<T>> tileEntity;
    private boolean isRotatable;
    private boolean hasTileEntity = true;

    public APTileEntityBlock(RegistryObject<BlockEntityType<T>> tileEntity, boolean isRotatable) {
        this.tileEntity = tileEntity;
        this.isRotatable = isRotatable;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH));
    }

    public APTileEntityBlock(RegistryObject<BlockEntityType<T>> tileEntity, boolean isRotatable, boolean hasTileEntity) {
        this(tileEntity, isRotatable);
        this.hasTileEntity = hasTileEntity;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return hasTileEntity;
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        if (isRotatable)
            return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
        return state;
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        if (isRotatable)
            return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        if (isRotatable)
            return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite().getOpposite());
        return this.defaultBlockState();
    }
}
