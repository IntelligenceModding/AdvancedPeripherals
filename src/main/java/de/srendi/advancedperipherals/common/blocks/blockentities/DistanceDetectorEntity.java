package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DistanceDetectorEntity extends PeripheralBlockEntity<DistanceDetectorPeripheral> {

    private volatile float maxRange = APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get().floatValue();
    private float currentDistance = -1;
    private volatile boolean showLaser = true;
    private volatile boolean calculatePeriodically = false;
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
        return this.maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = Math.min(Math.max(maxRange, 0), APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get().floatValue());
    }

    public float getCurrentDistance() {
        return this.currentDistance;
    }

    public void setCurrentDistance(float currentDistance) {
        this.currentDistance = currentDistance;
    }

    public boolean getShowLaser() {
        return this.showLaser;
    }

    public void setShowLaser(boolean showLaser) {
        this.showLaser = showLaser;
    }

    public boolean getCalculatePeriodically() {
        return this.calculatePeriodically;
    }

    public void setCalculatePeriodically(boolean calculatePeriodically) {
        this.calculatePeriodically = calculatePeriodically;
    }

    public boolean getIgnoreTransparent() {
        return this.ignoreTransparent;
    }

    public void setIgnoreTransparent(boolean ignoreTransparent) {
        this.ignoreTransparent = ignoreTransparent;
    }

    public DistanceDetectorPeripheral.DetectionType getDetectionType() {
        return this.detectionType;
    }

    public void setDetectionType(DistanceDetectorPeripheral.DetectionType detectionType) {
        this.detectionType = detectionType;
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
        this.setMaxRange(compound.getFloat("maxRange"));
        this.setCurrentDistance(compound.getFloat("currentDistance"));
        this.setShowLaser(compound.getBoolean("showLaser"));
        this.setCalculatePeriodically(compound.getBoolean("calculatePeriodically"));
        this.setIgnoreTransparent(compound.getBoolean("ignoreTransparent"));
        this.setDetectionType(DistanceDetectorPeripheral.DetectionType.values()[compound.getByte("detectionType")]);
        super.load(compound);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putFloat("maxRange", this.getMaxRange());
        compound.putFloat("currentDistance", this.getCurrentDistance());
        compound.putBoolean("showLaser", this.getShowLaser());
        compound.putBoolean("calculatePeriodically", this.getCalculatePeriodically());
        compound.putBoolean("ignoreTransparent", this.getIgnoreTransparent());
        compound.putByte("detectionType", (byte) this.getDetectionType().ordinal());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compound = super.getUpdateTag();
        compound.putFloat("maxRange", this.getMaxRange());
        compound.putFloat("currentDistance", this.getCurrentDistance());
        compound.putBoolean("showLaser", this.getShowLaser());
        return compound;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
