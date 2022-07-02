package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
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
    private final RegistryObject<TileEntityType<T>> tileEntity;
    private boolean hasTileEntity;

    public APTileEntityBlock(RegistryObject<TileEntityType<T>> tileEntity, Properties properties) {
        super(properties);
        this.tileEntity = tileEntity;
        this.hasTileEntity = tileEntity != null;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public APTileEntityBlock(RegistryObject<TileEntityType<T>> tileEntity) {
        this(tileEntity, Properties.of(Material.METAL));
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
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }
}
