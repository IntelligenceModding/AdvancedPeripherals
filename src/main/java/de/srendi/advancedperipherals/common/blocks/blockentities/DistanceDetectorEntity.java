package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.network.DistanceDetectorSyncPacket;
import de.srendi.advancedperipherals.common.network.PacketHandler;
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

    private DistanceDetectorPeripheral.DetectionType detectionType = DistanceDetectorPeripheral.DetectionType.BOTH;
    private float distance = 0;
    private double maxRange = APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get();
    private boolean showLaser = true;
    private boolean shouldCalculatePeriodically = false;
    private boolean ignoreTransparent = true;

    public DistanceDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.DISTANCE_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected DistanceDetectorPeripheral createPeripheral() {
        return new DistanceDetectorPeripheral(this);
    }

    public void setShowLaser(boolean showLaser) {
        if(this.showLaser != showLaser)
            PacketHandler.sendToAll(new DistanceDetectorSyncPacket(getBlockPos(), getLevel().dimension(), distance, showLaser));
        this.showLaser = showLaser;
    }

    public void setDistance(float distance) {
        if(this.distance != distance)
            PacketHandler.sendToAll(new DistanceDetectorSyncPacket(getBlockPos(), getLevel().dimension(), distance, showLaser));
        this.distance = distance;
    }

    public void setShouldCalculatePeriodically(boolean shouldCalculatePeriodically) {
        this.shouldCalculatePeriodically = shouldCalculatePeriodically;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    public float getDistance() {
        return distance;
    }

    public boolean getLaserVisibility() {
        return showLaser;
    }

    public boolean shouldCalculatePeriodically() {
        return shouldCalculatePeriodically;
    }

    public void setIgnoreTransparent(boolean ignoreTransparent) {
        this.ignoreTransparent = ignoreTransparent;
    }

    public boolean ignoreTransparent() {
        return ignoreTransparent;
    }

    public double getMaxDistance() {
        return maxRange;
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
        return AABB.ofSize(getBlockPos().getCenter(), direction.getStepX() * distance + 1, direction.getStepY() * distance + 1, direction.getStepZ() * distance + 1)
                .move(direction.getStepX() * distance / 2, direction.getStepY() * distance / 2, direction.getStepZ() * distance / 2);
    }

    public double calculateDistance() {
        Direction direction = getBlockState().getValue(BaseBlock.ORIENTATION).front();
        Vec3 from = Vec3.atCenterOf(getBlockPos()).add(direction.getNormal().getX() * 0.501, direction.getNormal().getY() * 0.501, direction.getNormal().getZ() * 0.501);
        Vec3 to = from.add(direction.getNormal().getX() * maxRange, direction.getNormal().getY() * maxRange, direction.getNormal().getZ() * maxRange);
        HitResult result = detectionType == DistanceDetectorPeripheral.DetectionType.BOTH ? HitResultUtil.getHitResult(to, from, getLevel(), ignoreTransparent) : detectionType == DistanceDetectorPeripheral.DetectionType.BLOCK ? HitResultUtil.getBlockHitResult(to, from, getLevel(), ignoreTransparent) : HitResultUtil.getEntityHitResult(to, from, getLevel());

        float distance = 0;
        BlockState resultBlock;
        if (result.getType() != HitResult.Type.MISS) {
            if (result instanceof BlockHitResult blockHitResult) {
                resultBlock = getLevel().getBlockState(blockHitResult.getBlockPos());
                distance = distManhattan(blockHitResult.getBlockPos().getCenter(), getBlockPos().getCenter());

                if (resultBlock.getBlock() instanceof SlabBlock && direction.getAxis() == Direction.Axis.Y) {
                    if (resultBlock.getValue(SlabBlock.TYPE) == SlabType.TOP && direction == Direction.UP)
                        distance = distance + 0.5f;
                    if (resultBlock.getValue(SlabBlock.TYPE) == SlabType.BOTTOM && direction == Direction.DOWN)
                        distance = distance - 0.5f;
                }
            }
            if (result instanceof EntityHitResult entityHitResult) {
                distance = distManhattan(entityHitResult.getLocation(), getBlockPos().getCenter());
            }
        }
        setDistance(distance);
        return distance;
    }


    private float distManhattan(Vec3 from, Vec3 to) {
        float f = (float)Math.abs(from.x - to.x);
        float f1 = (float)Math.abs(from.y - to.y);
        float f2 = (float)Math.abs(from.z - to.z);
        return f + f1 + f2;
    }
}
