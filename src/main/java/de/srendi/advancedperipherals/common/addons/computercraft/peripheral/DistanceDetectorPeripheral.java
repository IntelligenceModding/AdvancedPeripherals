package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;

public class DistanceDetectorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<DistanceDetectorEntity>> {

    public static final String PERIPHERAL_TYPE = "distanceDetector";
    private double height = 0.5;
    private DetectionType detectionType = DetectionType.BOTH;

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
    public final String setDetectionMode(int mode) {
        if (mode > 2) mode = 2;
        if (mode < 0) mode = 0;
        detectionType = DetectionType.values()[mode];
        return detectionType.toString();
    }

    @LuaFunction
    public final boolean detectsEntities() {
        return detectionType == DetectionType.ENTITIES || detectionType == DetectionType.BOTH;
    }

    @LuaFunction
    public final boolean detectsBlocks() {
        return detectionType == DetectionType.BLOCK || detectionType == DetectionType.BOTH;
    }

    @LuaFunction
    public final String getDetectionMode() {
        return detectionType.toString();
    }

    @LuaFunction
    public final double setHeight(double height) {
        this.height = Math.max(0, Math.min(1, height));
        return this.height;
    }

    @LuaFunction
    public final double getHeight() {
        return this.height;
    }

    @LuaFunction
    public final double getDistance() {
        return getPeripheralOwner().tileEntity.getDistance() - 1;
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
