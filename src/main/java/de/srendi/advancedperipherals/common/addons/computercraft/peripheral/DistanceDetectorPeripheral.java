/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        if (mode > 2)
            mode = 2;
        if (mode < 0)
            mode = 0;
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
        getPeripheralOwner().tileEntity.setMaxRange(
                Math.max(0, Math.min(APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get(), maxDistance)));
    }

    @LuaFunction
    public final double getMaxRange() {
        return getPeripheralOwner().tileEntity.getMaxDistance();
    }

    public enum DetectionType {
        BLOCK, ENTITIES, BOTH
    }

}
