package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

public class DistanceDetectorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<DistanceDetectorEntity>> {

    public static final String PERIPHERAL_TYPE = "distance_detector";

    public DistanceDetectorPeripheral(DistanceDetectorEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableDistanceDetector.get();
    }

    @LuaFunction
    public final void setLaserVisibility(boolean laser) {
        getPeripheralOwner().tileEntity.setShowLaser(laser);
    }

    @LuaFunction
    public final boolean getLaserVisibility() {
        return getPeripheralOwner().tileEntity.getLaserVisibility();
    }

    @LuaFunction
    public final void setIgnoreTransparency(boolean enable) {
        getPeripheralOwner().tileEntity.setIgnoreTransparent(enable);
    }

    @LuaFunction
    public final boolean ignoresTransparency() {
        return getPeripheralOwner().tileEntity.ignoreTransparent();
    }

    @LuaFunction
    public final void setDetectionMode(int mode) {
        if (mode > 2) mode = 2;
        if (mode < 0) mode = 0;
        getPeripheralOwner().tileEntity.setDetectionType(DetectionType.values()[mode]);
    }

    @LuaFunction
    public final boolean detectsEntities() {
        DetectionType detectionType = getPeripheralOwner().tileEntity.getDetectionType();
        return detectionType == DetectionType.ENTITIES || detectionType == DetectionType.BOTH;
    }

    @LuaFunction
    public final boolean detectsBlocks() {
        DetectionType detectionType = getPeripheralOwner().tileEntity.getDetectionType();
        return detectionType == DetectionType.BLOCK || detectionType == DetectionType.BOTH;
    }

    @LuaFunction
    public final String getDetectionMode() {
        DetectionType detectionType = getPeripheralOwner().tileEntity.getDetectionType();
        return detectionType.toString();
    }

    @LuaFunction
    public final double getDistance() {
        return getPeripheralOwner().tileEntity.getCurrentDistance() - 1;
    }

    @LuaFunction
    public final double calculateDistance() {
        return getPeripheralOwner().tileEntity.calculateDistance() - 1;
    }

    @LuaFunction
    public final boolean shouldCalculatePeriodically() {
        return getPeripheralOwner().tileEntity.shouldCalculatePeriodically();
    }

    @LuaFunction
    public final void setCalculatePeriodically(boolean shouldRenderPeriodically) {
        getPeripheralOwner().tileEntity.setShouldCalculatePeriodically(shouldRenderPeriodically);
    }

    @LuaFunction
    public final void setMaxRange(double maxDistance) {
        getPeripheralOwner().tileEntity.setMaxRange(Math.max(0, Math.min(APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get(), maxDistance)));
    }

    @LuaFunction
    public final double getMaxRange() {
        return getPeripheralOwner().tileEntity.getMaxDistance();
    }

    public enum DetectionType {
        BLOCK,
        ENTITIES,
        BOTH
    }

}
