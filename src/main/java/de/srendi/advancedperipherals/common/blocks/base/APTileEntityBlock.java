package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class APTileEntityBlock<T extends TileEntity> extends BaseTileEntityBlock {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    private RegistryObject<TileEntityType<T>> tileEntity;
    private boolean isRotatable;
    private boolean hasTileEntity = true;

    public APTileEntityBlock(RegistryObject<TileEntityType<T>> tileEntity, boolean isRotatable) {
        this.tileEntity = tileEntity;
        this.isRotatable = isRotatable;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH));
    }

    public APTileEntityBlock(RegistryObject<TileEntityType<T>> tileEntity, boolean isRotatable, boolean hasTileEntity) {
        this(tileEntity, isRotatable);
        this.hasTileEntity = hasTileEntity;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return hasTileEntity;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return tileEntity.get().create();
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
