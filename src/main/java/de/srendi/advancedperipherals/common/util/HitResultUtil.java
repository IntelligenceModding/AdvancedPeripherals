package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HitResultUtil {

    @NotNull
    public static HitResult getHitResult(Vec3 to, Vec3 from, Level level) {
        EntityHitResult entityResult = getEntityHitResult(to, from, level);
        BlockHitResult blockResult = getBlockHitResult(to, from, level);

        if(entityResult.getType() != HitResult.Type.MISS && blockResult.getType() == HitResult.Type.MISS)
            return entityResult;

        if(entityResult.getType() == HitResult.Type.MISS && blockResult.getType() != HitResult.Type.MISS)
            return blockResult;

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

        return new EntityHitResult(entities.get(0));
    }

    @NotNull
    public static BlockHitResult getBlockHitResult(Vec3 to, Vec3 from, Level level) {
        BlockHitResult result = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
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
}
