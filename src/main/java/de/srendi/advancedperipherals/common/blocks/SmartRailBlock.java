package de.srendi.advancedperipherals.common.blocks;

import com.mojang.serialization.MapCodec;
import de.srendi.advancedperipherals.common.blocks.base.IHarvestableBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.SmartRailBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SmartRailBlock extends BaseRailBlock implements EntityBlock, IHarvestableBlock {
    public static final MapCodec<SmartRailBlock> CODEC = simpleCodec(SmartRailBlock::new);

    public SmartRailBlock() {
        this(
            Properties.of()
                .strength(0.7f)
                .sound(SoundType.METAL)
                .noCollission()
        );
    }

    protected SmartRailBlock(Properties properties) {
        super(true, properties);
        this.registerDefaultState(
            this.getStateDefinition()
                .any()
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(BlockStateProperties.POWERED, false)
                .setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.NORTH_SOUTH)
        );
    }

    @Override
    protected MapCodec<? extends SmartRailBlock> codec() {
        return CODEC;
    }

    @Override
    public final Property<RailShape> getShapeProperty() {
        return BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(
            BlockStateProperties.WATERLOGGED,
            BlockStateProperties.POWERED,
            BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }

    @Override
    public TagKey<Block> getHarvestTag() {
        return BlockTags.NEEDS_STONE_TOOL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmartRailBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level0, BlockState state0, BlockEntityType<T> type) {
        return (level, pos, state, entity) -> {
            if (entity instanceof SmartRailBlockEntity blockEntity) {
                blockEntity.handleTick(level, state, type);
            }
        };
    }

    @Override
    public void onMinecartPass(BlockState blockState, Level level, BlockPos blockPos, AbstractMinecart cart) {
        if (!(level.getBlockEntity(blockPos) instanceof SmartRailBlockEntity be)) {
            return;
        }
        RailPoweredState state = be.getState();
        Vec3 pos = Vec3.atBottomCenterOf(blockPos);
        switch (state) {
            case PASS -> {
                return;
            }
            case STOP -> {
                Vec3 movement = cart.getDeltaMovement();
                double speedSqr = movement.horizontalDistanceSqr();
                if (speedSqr < 0.002) {
                    if (speedSqr > 0) {
                        cart.setDeltaMovement(0, movement.y, 0);
                    }
                    return;
                }
                double dist = cart.position().subtract(pos).horizontalDistanceSqr();
                double acceleration = -speedSqr / 2 / dist;
                double speed = Math.sqrt(speedSqr);
                if (-acceleration > speed) {
                    acceleration = -speed;
                }
                movement = movement.add(movement.x / speed * acceleration, 0, movement.z / speed * acceleration);
                cart.setDeltaMovement(movement);
            }
            case ACC_BOTH -> {
                Vec3 movement = cart.getDeltaMovement();
                double speedSqr = movement.horizontalDistanceSqr();
                if (speedSqr < 0.002) {
                    return;
                }
                double acceleration = 0.06;
                double speed = Math.sqrt(speedSqr);
                movement = movement.add(movement.x / speed * acceleration, 0, movement.z / speed * acceleration);
                cart.setDeltaMovement(movement);
            }
            case ACC_NEG, ACC_POS -> {
                Vec3 movement = cart.getDeltaMovement();
                double acceleration = 0.06;
                if (state == RailPoweredState.ACC_NEG) {
                    acceleration = -acceleration;
                }
                boolean isZaxis = switch (blockState.getValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) {
                    case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> true;
                    default -> false;
                };
                movement = movement.add(isZaxis ? 0 : acceleration, 0, isZaxis ? acceleration : 0);
                cart.setDeltaMovement(movement);
            }
        }
    }

    public enum RailPoweredState {
        STOP,
        PASS,
        ACC_NEG,
        ACC_BOTH,
        ACC_POS;
    }
}
