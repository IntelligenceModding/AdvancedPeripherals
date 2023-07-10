package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HitResultUtil {

    /**
     * This method is used to get the hit result of an entity from the start position of a block
     *
     * @param to                the target position/max position
     * @param from              the source position like a block
     * @param level             the level
     * @param ignoreTransparent if transparent blocks should be ignored
     * @return the hit result. {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} if nothing found
     */
    @NotNull
    public static HitResult getHitResult(Vec3 to, Vec3 from, Level level, boolean ignoreTransparent) {
        EntityHitResult entityResult = getEntityHitResult(to, from, level);
        BlockHitResult blockResult = getBlockHitResult(to, from, level, ignoreTransparent);

        if (entityResult.getType() != HitResult.Type.MISS && blockResult.getType() == HitResult.Type.MISS)
            return entityResult;

        if (entityResult.getType() == HitResult.Type.MISS && blockResult.getType() != HitResult.Type.MISS)
            return blockResult;

        if (entityResult.getType() == HitResult.Type.MISS && blockResult.getType() == HitResult.Type.MISS)
            return BlockHitResult.miss(from, blockResult.getDirection(), new BlockPos(to));

        double blockDistance = new BlockPos(from).distManhattan(blockResult.getBlockPos());
        double entityDistance = new BlockPos(from).distManhattan(new Vec3i(entityResult.getLocation().x, entityResult.getLocation().y, entityResult.getLocation().z));

        if (blockDistance < entityDistance)
            return blockResult;

        return entityResult;
    }

    /**
     * This method is used to get the hit result of an entity from the start position of a block
     * This could be used to find an entity from the eyes position of another entity but since
     * this method uses one AABB made out of the two coordinates, this would also find any entities
     * which are not located in the ray you might want. {@link DistanceDetectorPeripheral#getDistance()}
     *
     * @param to    the target position/max position
     * @param from  the source position like a block
     * @param level the world
     * @return the entity hit result. An empty HitResult with {@link HitResult.Type#MISS} as type if nothing found
     */
    @NotNull
    public static EntityHitResult getEntityHitResult(Vec3 to, Vec3 from, Level level) {
        AABB checkingBox = new AABB(to, from);

        List<Entity> entities = level.getEntities((Entity) null, checkingBox, (entity) -> true);

        if (entities.isEmpty())
            return new EmptyEntityHitResult();

        Entity nearestEntity = null;

        // Find the nearest entity
        for (Entity entity : entities) {
            if (nearestEntity == null) {
                nearestEntity = entity;
                continue;
            }

            double distance = new BlockPos(from).distManhattan(new Vec3i(entity.getX(), entity.getY(), entity.getZ()));
            double nearestDistance = new BlockPos(from).distManhattan(new Vec3i(nearestEntity.getX(), nearestEntity.getY(), nearestEntity.getZ()));

            // If it's closer, set it as the nearest entity
            if (distance < nearestDistance)
                nearestEntity = entity;
        }

        return new EntityHitResult(nearestEntity);
    }

    /**
     * This method is used to get the hit result of a block from the start position of a block
     *
     * @param to               the target position/max position
     * @param from             the source position
     * @param level            the world
     * @param ignoreNoOccluded if true, the method will ignore blocks which are not occluding like glass
     * @return the block hit result. {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} if nothing found
     */
    @NotNull
    public static BlockHitResult getBlockHitResult(Vec3 to, Vec3 from, Level level, boolean ignoreNoOccluded) {
        return level.clip(new AdvancecClipContext(from, to, ignoreNoOccluded ? IgnoreNoOccludedContext.INSTANCE : ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
    }

    public static class EmptyEntityHitResult extends EntityHitResult {

        /**
         * The super constructor is a NotNull argument but since this result is empty, we'll just return null
         */
        public EmptyEntityHitResult() {
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
    private enum IgnoreNoOccludedContext implements ClipContext.ShapeGetter {

        INSTANCE;

        @NotNull
        @Override
        public VoxelShape get(BlockState pState, @NotNull BlockGetter pBlock, @NotNull BlockPos pPos, @NotNull CollisionContext pCollisionContext) {
            return !pState.canOcclude() ? Shapes.empty() : pState.getCollisionShape(pBlock, pPos, pCollisionContext);
        }
    }

    /**
     * A clip context but with a custom shape getter. Used to define another shape getter for the block like {@link IgnoreNoOccludedContext}
     */
    private static class AdvancecClipContext extends ClipContext {

        private final ShapeGetter blockShapeGetter;

        protected AdvancecClipContext(Vec3 from, Vec3 to, ShapeGetter blockShapeGetter, Fluid fluidShapeGetter, @Nullable Entity entity) {
            super(from, to, Block.COLLIDER, fluidShapeGetter, entity);
            this.blockShapeGetter = blockShapeGetter;
        }

        @NotNull
        @Override
        public VoxelShape getBlockShape(@NotNull BlockState pBlockState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
            return blockShapeGetter.get(pBlockState, pLevel, pPos, this.collisionContext);
        }
    }
}
