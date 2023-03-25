package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import net.minecraft.core.BlockPos;
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

    @NotNull
    public static HitResult getHitResult(Vec3 to, Vec3 from, Level level, boolean ignoreTransparent) {
        EntityHitResult entityResult = getEntityHitResult(to, from, level);
        BlockHitResult blockResult = getBlockHitResult(to, from, level, ignoreTransparent);

        if(entityResult.getType() != HitResult.Type.MISS && blockResult.getType() == HitResult.Type.MISS)
            return entityResult;

        if(entityResult.getType() == HitResult.Type.MISS && blockResult.getType() != HitResult.Type.MISS)
            return blockResult;

        if(entityResult.getType() == HitResult.Type.MISS && blockResult.getType() == HitResult.Type.MISS)
            return BlockHitResult.miss(from, null, new BlockPos(to));

        double blockDistance = new BlockPos(from).distManhattan(blockResult.getBlockPos());
        double entityDistance = new BlockPos(from).distManhattan(new Vec3i(entityResult.getLocation().x, entityResult.getLocation().y, entityResult.getLocation().z));

        if(blockDistance < entityDistance)
            return blockResult;

        return entityResult;
    }

    /**
     * This method is used to get the hit result of an entity from the start position of a block
     * This could be used to find an entity from the eyes position of another entity but since
     * this method uses one AABB made out of the two coordinates, this would also find any entities
     * which are not located in the ray you might want. {@link DistanceDetectorPeripheral#getDistance()}
     *
     *
     * @param to the target position or max distance
     * @param from the source position like a block
     * @param level the world
     * @return the entity hit result. An empty HitResult with {@link HitResult.Type#MISS} as type if nothing found
     */
    @NotNull
    public static EntityHitResult getEntityHitResult(Vec3 to, Vec3 from, Level level) {
        AABB checkingBox = new AABB(to, from);

        List<Entity> entities = level.getEntities((Entity) null, checkingBox, (entity) -> true);

        if(entities.isEmpty())
            return new EmptyEntityHitResult();

        Entity nearestEntity = null;

        for (Entity entity : entities) {
            if(nearestEntity == null) {
                nearestEntity = entity;
                continue;
            }

            double distance = new BlockPos(from).distManhattan(new Vec3i(entity.getX(), entity.getY(), entity.getZ()));
            double nearestDistance = new BlockPos(from).distManhattan(new Vec3i(nearestEntity.getX(), nearestEntity.getY(), nearestEntity.getZ()));

            if(distance < nearestDistance)
                nearestEntity = entity;
        }

        return new EntityHitResult(nearestEntity);
    }

    @NotNull
    public static BlockHitResult getBlockHitResult(Vec3 to, Vec3 from, Level level, boolean ignoreNoOccluded) {
        BlockHitResult result = level.clip(new AdvancecClipContext(from, to, ignoreNoOccluded ? IgnoreNoOccludedContext.INSTANCE : ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        return result;
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

    private enum IgnoreNoOccludedContext implements ClipContext.ShapeGetter {

        INSTANCE;

        @NotNull
        @Override
        public VoxelShape get(BlockState pState, @NotNull BlockGetter pBlock, @NotNull BlockPos pPos, @NotNull CollisionContext pCollisionContext) {
            return !pState.canOcclude() ? Shapes.empty() : pState.getCollisionShape(pBlock, pPos, pCollisionContext);
        }
    }

    private static class AdvancecClipContext extends ClipContext {

        private final ShapeGetter blockShapeGetter;

        public AdvancecClipContext(Vec3 from, Vec3 to, ShapeGetter blockShapeGetter, Fluid fluidShapeGetter, @Nullable Entity entity) {
            super(from, to, Block.COLLIDER, fluidShapeGetter, entity);
            this.blockShapeGetter = blockShapeGetter;
        }

        @Override
        public VoxelShape getBlockShape(BlockState pBlockState, BlockGetter pLevel, BlockPos pPos) {
            return blockShapeGetter.get(pBlockState, pLevel, pPos, this.collisionContext);
        }
    }
}
