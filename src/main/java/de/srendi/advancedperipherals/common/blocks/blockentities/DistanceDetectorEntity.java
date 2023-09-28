package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.network.PacketHandler;
import de.srendi.advancedperipherals.common.network.toclient.DistanceDetectorSyncPacket;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.HitResultUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

public class DistanceDetectorEntity extends PeripheralBlockEntity<DistanceDetectorPeripheral> {

    private double maxRange = APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get();
    private float currentDistance = 0;
    private boolean showLaser = true;
    private boolean shouldCalculatePeriodically = false;
    private boolean ignoreTransparent = true;
    private DistanceDetectorPeripheral.DetectionType detectionType = DistanceDetectorPeripheral.DetectionType.BOTH;

    public DistanceDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.DISTANCE_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected DistanceDetectorPeripheral createPeripheral() {
        return new DistanceDetectorPeripheral(this);
    }

    public void setShowLaser(boolean showLaser) {
        if (this.showLaser != showLaser)
            PacketHandler.sendToAll(new DistanceDetectorSyncPacket(getBlockPos(), getLevel().dimension(), currentDistance, showLaser));
        this.showLaser = showLaser;
    }

    public void setCurrentDistance(float currentDistance) {
        if (this.currentDistance != currentDistance)
            PacketHandler.sendToAll(new DistanceDetectorSyncPacket(getBlockPos(), getLevel().dimension(), currentDistance, showLaser));
        this.currentDistance = currentDistance;
    }

    public void setShouldCalculatePeriodically(boolean shouldCalculatePeriodically) {
        this.shouldCalculatePeriodically = shouldCalculatePeriodically;
    }

    public double getMaxDistance() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    public float getCurrentDistance() {
        return currentDistance;
    }

    public boolean getLaserVisibility() {
        return showLaser;
    }

    public boolean shouldCalculatePeriodically() {
        return shouldCalculatePeriodically;
    }

    public boolean ignoreTransparent() {
        return ignoreTransparent;
    }

    public void setIgnoreTransparent(boolean ignoreTransparent) {
        this.ignoreTransparent = ignoreTransparent;
    }

    public DistanceDetectorPeripheral.DetectionType getDetectionType() {
        return detectionType;
    }

    public void setDetectionType(DistanceDetectorPeripheral.DetectionType detectionType) {
        this.detectionType = detectionType;
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.getGameTime() % APConfig.PERIPHERALS_CONFIG.distanceDetectorUpdateRate.get() == 0 && shouldCalculatePeriodically) {
            // We calculate the distance every 2 ticks, so we do not have to run the getDistance function of the peripheral
            // on the main thread which prevents the 1 tick yield time of the function.
            // The calculateDistance function is not thread safe, so we have to run it on the main thread.
            // It should be okay to run that function every 2 ticks, calculating it does not take too much time.
            calculateDistance();
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        Direction direction = getBlockState().getValue(BaseBlock.ORIENTATION).front();
        return AABB.ofSize(Vec3.atCenterOf(getBlockPos()), direction.getStepX() * currentDistance + 1, direction.getStepY() * currentDistance + 1, direction.getStepZ() * currentDistance + 1)
                .move(direction.getStepX() * currentDistance / 2, direction.getStepY() * currentDistance / 2, direction.getStepZ() * currentDistance / 2);
    }

    public double calculateDistance() {
        Direction direction = getBlockState().getValue(BaseBlock.ORIENTATION).front();
        Vec3 center = Vec3.atCenterOf(getBlockPos());
        Vec3 from = center.add(direction.getStepX() * 0.501, direction.getStepY() * 0.501, direction.getStepZ() * 0.501);
        Vec3 to = from.add(direction.getStepX() * maxRange, direction.getStepY() * maxRange, direction.getStepZ() * maxRange);
        HitResult result = getResult(to, from);

        float distance = calculateDistance(result, center, direction);
        setCurrentDistance(distance);
        return distance;
    }

    private HitResult getResult(Vec3 to, Vec3 from) {
        if (detectionType == DistanceDetectorPeripheral.DetectionType.ENTITIES)
            return HitResultUtil.getEntityHitResult(to, from, getLevel());
        if (detectionType == DistanceDetectorPeripheral.DetectionType.BLOCK)
            return HitResultUtil.getBlockHitResult(to, from, getLevel(), ignoreTransparent);
        return HitResultUtil.getHitResult(to, from, getLevel(), ignoreTransparent);
    }

    private float calculateDistance(HitResult result, Vec3 center, Direction direction) {
        float distance = 0;
        if (result.getType() != HitResult.Type.MISS) {
            if (result instanceof BlockHitResult blockHitResult) {
                BlockState resultBlock = getLevel().getBlockState(blockHitResult.getBlockPos());
                distance = distManhattan(Vec3.atCenterOf(blockHitResult.getBlockPos()), center);

                distance = amendDistance(resultBlock, direction, distance);
            }
            if (result instanceof EntityHitResult entityHitResult) {
                distance = distManhattan(entityHitResult.getLocation(), center);
            }
        }
        return distance;
    }

    private float amendDistance(BlockState resultBlock, Direction direction, float distance) {
        if (resultBlock.getBlock() instanceof SlabBlock && direction.getAxis() == Direction.Axis.Y) {
            SlabType type = resultBlock.getValue(SlabBlock.TYPE);
            if (type == SlabType.TOP && direction == Direction.UP)
                return distance + 0.5f;
            if (type == SlabType.BOTTOM && direction == Direction.DOWN)
                return distance - 0.5f;
        }
        return distance;
    }

    private float distManhattan(Vec3 from, Vec3 to) {
        float f = (float) Math.abs(from.x - to.x);
        float f1 = (float) Math.abs(from.y - to.y);
        float f2 = (float) Math.abs(from.z - to.z);
        return f + f1 + f2;
    }
}
