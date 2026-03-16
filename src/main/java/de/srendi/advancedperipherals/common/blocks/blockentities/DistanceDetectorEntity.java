package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
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
    public void loadAdditional(@NotNull CompoundTag compound, @NotNull HolderLookup.Provider provider) {
        this.setMaxRange(compound.getFloat("maxRange"));
        this.setCurrentDistance(compound.getFloat("currentDistance"));
        this.setShowLaser(compound.getBoolean("showLaser"));
        this.setCalculatePeriodically(compound.getBoolean("calculatePeriodically"));
        this.setIgnoreTransparent(compound.getBoolean("ignoreTransparent"));
        this.setDetectionType(DistanceDetectorPeripheral.DetectionType.values()[compound.getByte("detectionType")]);
        super.loadAdditional(compound, provider);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putFloat("maxRange", this.getMaxRange());
        compound.putFloat("currentDistance", this.getCurrentDistance());
        compound.putBoolean("showLaser", this.getShowLaser());
        compound.putBoolean("calculatePeriodically", this.getCalculatePeriodically());
        compound.putBoolean("ignoreTransparent", this.getIgnoreTransparent());
        compound.putByte("detectionType", (byte) this.getDetectionType().ordinal());
    }

    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag compound = super.getUpdateTag(provider);
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
