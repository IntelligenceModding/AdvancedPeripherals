package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class HitResultUtil {

    /**
     * This method is used to get the hit result of an entity from the start position of a block
     *
     * @param from        the source position like a block
     * @param to          the target position/max position
     * @param level       the level
     * @param shapeGetter the block collision shape getter
     * @return the hit result. {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} if nothing found
     */
    @NotNull
    public static HitResult getHitResult(Vec3 from, Vec3 to, Level level, ClipContext.ShapeGetter shapeGetter) {
        return getHitResult(from, to, level, shapeGetter, null);
    }

    /**
     * This method is used to get the hit result of an entity from the start position of a block
     *
     * @param from        the source position like a block
     * @param to          the target position/max position
     * @param level       the level
     * @param shapeGetter the block collision shape getter
     * @param source      the source Entity/BlockPos that will be ignored
     * @return the hit result. {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} if nothing found
     */
    @NotNull
    public static HitResult getHitResult(Vec3 from, Vec3 to, Level level, ClipContext.ShapeGetter shapeGetter, Object source) {
        EntityHitResult entityResult = getEntityHitResult(from, to, level, source instanceof Entity ? (Entity) source : null, source instanceof Predicate<?> ? (Predicate<Entity>) source : EntitySelector.NO_SPECTATORS);
        BlockHitResult blockResult = getBlockHitResult(from, to, level, shapeGetter, source instanceof BlockPos ? (BlockPos) source : null);

        if (entityResult.getType() == HitResult.Type.MISS) {
            if (blockResult.getType() == HitResult.Type.MISS) {
                return BlockHitResult.miss(from, blockResult.getDirection(), new BlockPos(to));
            }
            return blockResult;
        } else if (blockResult.getType() == HitResult.Type.MISS) {
            return entityResult;
        }

        double blockDistance = from.distanceToSqr(blockResult.getLocation());
        double entityDistance = from.distanceToSqr(entityResult.getLocation());

        return blockDistance < entityDistance ? blockResult : entityResult;
    }

    /**
     * This method is used to get the hit result of an entity from the start position of a block
     * This could be used to find an entity from the eyes position of another entity but since
     * this method uses one AABB made out of the two coordinates, this would also find any entities
     * which are not located in the ray you might want. {@link DistanceDetectorPeripheral#getDistance()}
     *
     * @param from  the source position like a block
     * @param to    the target position/max position
     * @param level the world
     * @return the entity hit result. An empty HitResult with {@link HitResult.Type#MISS} as type if nothing found
     */
    @NotNull
    public static EntityHitResult getEntityHitResult(Vec3 from, Vec3 to, Level level) {
        return getEntityHitResult(from, to, level, null, EntitySelector.NO_SPECTATORS);
    }

    /**
     * This method is used to get the hit result of an entity from the start position of a block
     * This could be used to find an entity from the eyes position of another entity but since
     * this method uses one AABB made out of the two coordinates, this would also find any entities
     * which are not located in the ray you might want. {@link DistanceDetectorPeripheral#getDistance()}
     *
     * @param from         the source position like a block
     * @param to           the target position/max position
     * @param level        the world
     * @param source       the source Entity that will be ignored
     * @param entityFilter the entity filter
     * @return the entity hit result. An empty HitResult with {@link HitResult.Type#MISS} as type if nothing found
     */
    @NotNull
    public static EntityHitResult getEntityHitResult(Vec3 from, Vec3 to, Level level, Entity source, Predicate<Entity> entityFilter) {
        AABB checkingBox = new AABB(to, from);

        List<Entity> entities = level.getEntities(source, checkingBox, entityFilter);

        Entity nearestEntity = null;
        Vec3 hitPos = null;
        double nearestDist = 0;

        // Find the nearest entity
        for (Entity entity : entities) {
            Vec3 pos = entity.getBoundingBox().clip(from, to).orElse(null);
            if (pos != null) {
                double distance = from.distanceToSqr(pos);
                if (nearestEntity == null || distance < nearestDist) {
                    nearestEntity = entity;
                    hitPos = pos;
                    nearestDist = distance;
                }
            }
        }

        return nearestEntity == null ? EmptyEntityHitResult.INSTANCE : new EntityHitResult(nearestEntity, hitPos);
    }

    /**
     * This method is used to get the hit result of a block from the start position of a block
     *
     * @param from        the source position
     * @param to          the target position/max position
     * @param level       the world
     * @param shapeGetter the block collision shape getter
     * @return the block hit result. {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} if nothing found
     */
    @NotNull
    public static BlockHitResult getBlockHitResult(Vec3 from, Vec3 to, Level level, ClipContext.ShapeGetter shapeGetter) {
        return getBlockHitResult(from, to, level, shapeGetter, null);
    }

    /**
     * This method is used to get the hit result of a block from the start position of a block
     *
     * @param from        the source position
     * @param to          the target position/max position
     * @param level       the world
     * @param shapeGetter the block collision shape getter
     * @param source      the source BlockPos that will be ignored
     * @return the block hit result. {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} if nothing found
     */
    @NotNull
    public static BlockHitResult getBlockHitResult(Vec3 from, Vec3 to, Level level, ClipContext.ShapeGetter shapeGetter, BlockPos source) {
        return level.clip(new AdvancedClipContext(from, to, shapeGetter, ClipContext.Fluid.NONE, null, source));
    }

    public static class EmptyEntityHitResult extends EntityHitResult {
        public static final EmptyEntityHitResult INSTANCE = new EmptyEntityHitResult();

        /**
         * The super constructor is a NotNull argument but since this result is empty, we'll just return null
         */
        private EmptyEntityHitResult() {
            super(null, null);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.MISS;
        }
    }

    /**
     * A shape getter which ignores blocks which are not occluding like glass
     */
    public static class IgnoreNoOccludedContext implements ClipContext.ShapeGetter {
        public static final IgnoreNoOccludedContext INSTANCE = new IgnoreNoOccludedContext();

        private IgnoreNoOccludedContext() {}

        @NotNull
        @Override
        public VoxelShape get(BlockState pState, @NotNull BlockGetter pBlock, @NotNull BlockPos pPos, @NotNull CollisionContext pCollisionContext) {
            return !pState.canOcclude() ? Shapes.empty() : pState.getCollisionShape(pBlock, pPos, pCollisionContext);
        }
    }

    /**
     * A clip context but with a custom shape getter. Used to define another shape getter for the block like {@link IgnoreNoOccludedContext}
     */
    private static class AdvancedClipContext extends ClipContext {

        private final ShapeGetter blockShapeGetter;
        private final BlockPos source;

        protected AdvancedClipContext(Vec3 from, Vec3 to, ShapeGetter blockShapeGetter, Fluid fluidShapeGetter, @Nullable Entity entity, BlockPos source) {
            super(from, to, Block.COLLIDER, fluidShapeGetter, entity);
            this.blockShapeGetter = blockShapeGetter;
            this.source = source;
        }

        @NotNull
        @Override
        public VoxelShape getBlockShape(@NotNull BlockState pBlockState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
            if (this.source != null && this.source.equals(pPos)) {
                return Shapes.empty();
            }
            return blockShapeGetter.get(pBlockState, pLevel, pPos, this.collisionContext);
        }
    }
}
