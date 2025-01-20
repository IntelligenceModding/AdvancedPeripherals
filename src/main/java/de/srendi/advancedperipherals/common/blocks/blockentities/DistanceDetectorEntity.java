package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.HitResultUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DistanceDetectorEntity extends PeripheralBlockEntity<DistanceDetectorPeripheral> {

    private final AtomicInteger maxRange = new AtomicInteger(Float.floatToRawIntBits(APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get().floatValue()));
    private final AtomicInteger currentDistance = new AtomicInteger(Float.floatToRawIntBits(-1));
    private final AtomicBoolean showLaser = new AtomicBoolean(true);
    private volatile boolean periodicallyCalculate = false;
    private volatile boolean ignoreTransparent = true;
    private volatile DistanceDetectorPeripheral.DetectionType detectionType = DistanceDetectorPeripheral.DetectionType.BOTH;

    public DistanceDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.DISTANCE_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected DistanceDetectorPeripheral createPeripheral() {
        return new DistanceDetectorPeripheral(this);
    }

    public float getMaxRange() {
        return Float.intBitsToFloat(this.maxRange.get());
    }

    protected void setMaxRangeNoUpdate(float maxRange) {
        maxRange = Math.min(Math.max(maxRange, 0), APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get().floatValue());
        int maxRangeBits = Float.floatToRawIntBits(maxRange);
        this.maxRange.set(maxRangeBits);
    }

    public void setMaxRange(float maxRange) {
        maxRange = Math.min(Math.max(maxRange, 0), APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get().floatValue());
        int maxRangeBits = Float.floatToRawIntBits(maxRange);
        if (this.maxRange.getAndSet(maxRangeBits) != maxRange) {
            this.sendUpdate();
        }
    }

    public float getCurrentDistance() {
        return Float.intBitsToFloat(this.currentDistance.get());
    }

    protected void setCurrentDistanceNoUpdate(float currentDistance) {
        int currentDistanceBits = Float.floatToRawIntBits(currentDistance);
        this.currentDistance.set(currentDistanceBits);
    }

    public void setCurrentDistance(float currentDistance) {
        int currentDistanceBits = Float.floatToRawIntBits(currentDistance);
        if (this.currentDistance.getAndSet(currentDistanceBits) != currentDistanceBits) {
            this.sendUpdate();
        }
    }

    public boolean getLaserVisibility() {
        return this.showLaser.get();
    }

    protected void setShowLaserNoUpdate(boolean showLaser) {
        this.showLaser.set(showLaser);
    }

    public void setShowLaser(boolean showLaser) {
        if (this.showLaser.getAndSet(showLaser) != showLaser) {
            this.sendUpdate();
        }
    }

    public boolean shouldCalculatePeriodically() {
        return this.periodicallyCalculate;
    }

    public void setShouldCalculatePeriodically(boolean periodicallyCalculate) {
        this.periodicallyCalculate = periodicallyCalculate;
        this.setChanged();
    }

    public boolean ignoreTransparent() {
        return this.ignoreTransparent;
    }

    public void setIgnoreTransparent(boolean ignoreTransparent) {
        this.ignoreTransparent = ignoreTransparent;
        this.setChanged();
    }

    public DistanceDetectorPeripheral.DetectionType getDetectionType() {
        return this.detectionType;
    }

    public void setDetectionType(DistanceDetectorPeripheral.DetectionType detectionType) {
        this.detectionType = detectionType;
        this.setChanged();
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.getGameTime() % APConfig.PERIPHERALS_CONFIG.distanceDetectorUpdateRate.get() == 0 && this.shouldCalculatePeriodically()) {
            // We calculate the distance every 2 ticks, so we do not have to run the getDistance function of the peripheral
            // on the main thread which prevents the 1 tick yield time of the function.
            // The calculateDistance function is not thread safe, so we have to run it on the main thread.
            // It should be okay to run that function every 2 ticks, calculating it does not take too much time.
            this.calculateAndUpdateDistance();
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        float currentDistance = this.getCurrentDistance();
        if (currentDistance == -1) {
            currentDistance = this.getMaxRange();
        }
        currentDistance += 1.5f;
        Direction direction = getBlockState().getValue(BaseBlock.ORIENTATION).front();
        Vec3 blockPos = Vec3.atCenterOf(this.getBlockPos());
        return new AABB(blockPos, blockPos.add(direction.getStepX() * currentDistance, direction.getStepY() * currentDistance, direction.getStepZ() * currentDistance));
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        this.setMaxRangeNoUpdate(compound.getFloat("maxRange"));
        this.setCurrentDistanceNoUpdate(compound.getFloat("currentDistance"));
        this.setShowLaserNoUpdate(compound.getBoolean("showLaser"));
        this.setShouldCalculatePeriodically(compound.getBoolean("calculatePeriodically"));
        this.setIgnoreTransparent(compound.getBoolean("ignoreTransparent"));
        this.setDetectionType(DistanceDetectorPeripheral.DetectionType.values()[compound.getByte("detectionType")]);
        super.load(compound);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putFloat("maxRange", this.getMaxRange());
        compound.putFloat("currentDistance", this.getCurrentDistance());
        compound.putBoolean("showLaser", this.getLaserVisibility());
        compound.putBoolean("calculatePeriodically", this.shouldCalculatePeriodically());
        compound.putBoolean("ignoreTransparent", this.ignoreTransparent());
        compound.putByte("detectionType", (byte) this.getDetectionType().ordinal());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compound = super.getUpdateTag();
        compound.putFloat("maxRange", this.getMaxRange());
        compound.putFloat("currentDistance", this.getCurrentDistance());
        compound.putBoolean("showLaser", this.getLaserVisibility());
        return compound;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    protected Vec3 getCenterPos() {
        Vec3 pos = Vec3.atCenterOf(this.getBlockPos());
        if (!APAddons.vs2Loaded) {
            return pos;
        }
        Ship ship = APAddons.getVS2Ship(this.getLevel(), this.getBlockPos());
        if (ship == null) {
            return pos;
        }
        Vector3d newPos = ship.getShipToWorld().transformPosition(new Vector3d(pos.x, pos.y, pos.z));
        return new Vec3(newPos.x, newPos.y, newPos.z);
    }

    protected Vec3 getDirection() {
        Vec3 dir = Vec3.atLowerCornerOf(getBlockState().getValue(BaseBlock.ORIENTATION).front().getNormal());
        if (!APAddons.vs2Loaded) {
            return dir;
        }
        Ship ship = APAddons.getVS2Ship(this.getLevel(), this.getBlockPos());
        if (ship == null) {
            return dir;
        }
        Vector3d newDir = ship.getShipToWorld().transformDirection(new Vector3d(dir.x, dir.y, dir.z));
        return new Vec3(newDir.x, newDir.y, newDir.z);
    }

    public double calculateDistance() {
        final double maxRange = this.getMaxRange();
        Vec3 direction = getDirection();
        Vec3 center = getCenterPos();
        Vec3 from = center;
        Vec3 to = from.add(direction.x * maxRange, direction.y * maxRange, direction.z * maxRange);

        HitResult result = this.getHitResult(from, to);
        if (result.getType() == HitResult.Type.MISS) {
            return -1;
        }
        return result.getLocation().distanceTo(center) - 0.5f;
    }

    public double calculateAndUpdateDistance() {
        double distance = this.calculateDistance();
        this.setCurrentDistance((float) distance);
        return distance;
    }

    private HitResult getHitResult(Vec3 from, Vec3 to) {
        Level level = this.getLevel();
        return switch (this.detectionType) {
            case ENTITY -> HitResultUtil.getEntityHitResult(from, to, level);
            case BLOCK -> HitResultUtil.getBlockHitResult(from, to, level, this.ignoreTransparent ? HitResultUtil.IgnoreNoOccludedContext.INSTANCE : ClipContext.Block.COLLIDER, this.getBlockPos());
            case BOTH -> HitResultUtil.getHitResult(from, to, level, this.ignoreTransparent ? HitResultUtil.IgnoreNoOccludedContext.INSTANCE : ClipContext.Block.COLLIDER, this.getBlockPos());
        };
    }
}
