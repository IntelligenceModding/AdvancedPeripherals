package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class BaseBlock extends Block implements IHarvestableBlock {

    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

    private final TagKey<Block> harvestTag;

    public BaseBlock() {
        this(Tags.Blocks.NEEDS_WOOD_TOOL);
    }

    public BaseBlock(TagKey<Block> harvestTag) {
        this(Properties.of().sound(SoundType.METAL).mapColor(DyeColor.GRAY).strength(1, 5).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops(), harvestTag);
    }

    public BaseBlock(Properties properties, TagKey<Block> harvestTag) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, FrontAndTop.NORTH_UP));
        this.harvestTag = harvestTag;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public TagKey<Block> getHarvestTag() {
        return harvestTag;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ORIENTATION);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(ORIENTATION, pRotation.rotation().rotate(pState.getValue(ORIENTATION)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.setValue(ORIENTATION, pMirror.rotation().rotate(pState.getValue(ORIENTATION)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getNearestLookingDirection().getOpposite();
        Direction direction1;
        if (direction.getAxis() == Direction.Axis.Y) {
            direction1 = pContext.getHorizontalDirection();
        } else {
            direction1 = Direction.UP;
        }

        return this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(direction, direction1));
    }

}
