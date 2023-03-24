package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.HitResultUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DistanceDetectorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<DistanceDetectorEntity>> {

    public static final String PERIPHERAL_TYPE = "distanceDetector";
    private double height = 0.5;
    private DetectionType detectionType = DetectionType.BOTH;
    private boolean ignoreTransparent = true;
    private boolean laserVisible = true;

    public DistanceDetectorPeripheral(DistanceDetectorEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableDistanceDetector.get();
    }

    @LuaFunction
    public final boolean setLaserVisibility(boolean laser) {
        getPeripheralOwner().tileEntity.setShowLaser(laser);
        return laserVisible = laser;
    }

    @LuaFunction
    public final boolean getLaserVisibility() {
        return laserVisible;
    }

    @LuaFunction
    public final boolean setTransparencyDetection(boolean enable) {
        return ignoreTransparent = enable;
    }

    @LuaFunction
    public final boolean getTransparencyDetection() {
        return ignoreTransparent;
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

    public final double setHeight(double height) {
        if (height > 1) this.height = 1;
        if (height < 0) this.height = 0;
        return this.height;
    }

    public final double getHeight() {
        return this.height;
    }

    @LuaFunction(mainThread = true)
    public final double getDistance() {
        Direction direction = getPeripheralOwner().getOrientation().front();
        Vec3 from = Vec3.atCenterOf(getPos()).add(direction.getNormal().getX() * 0.501, direction.getNormal().getY() * 0.501, direction.getNormal().getZ() * 0.501);
        Vec3 to = from.add(direction.getNormal().getX() * 256, direction.getNormal().getY() * 256, direction.getNormal().getZ() * 256);
        HitResult result = detectionType == DetectionType.BOTH ? HitResultUtil.getHitResult(to, from, getLevel()) : detectionType == DetectionType.BLOCK ? HitResultUtil.getBlockHitResult(to, from, getLevel()) : HitResultUtil.getEntityHitResult(to, from, getLevel());

        float distance = 0;
        BlockState resultBlock;
        if (result.getType() != HitResult.Type.MISS) {
            if (result instanceof BlockHitResult blockHitResult) {
                resultBlock = getLevel().getBlockState(blockHitResult.getBlockPos());
                distance = getPos().distManhattan(blockHitResult.getBlockPos());

                if (resultBlock.getBlock() instanceof SlabBlock && direction.getAxis() == Direction.Axis.Y) {
                    if (resultBlock.getValue(SlabBlock.TYPE) == SlabType.TOP && direction == Direction.UP)
                        distance = distance + 0.5f;
                    if (resultBlock.getValue(SlabBlock.TYPE) == SlabType.BOTTOM && direction == Direction.DOWN)
                        distance = distance - 0.5f;
                }
            }
            if (result instanceof EntityHitResult entityHitResult) {
                distance = getPos().distManhattan(new Vec3i(entityHitResult.getLocation().x, entityHitResult.getLocation().y, entityHitResult.getLocation().z));
            }
        }
        getPeripheralOwner().tileEntity.setDistance(distance -1);
        return distance - 1;
    }

    private enum DetectionType {
        BLOCK,
        ENTITIES,
        BOTH
    }

}
