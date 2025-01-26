package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.HitResultUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DistanceDetectorPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "distance_detector";

    private final DistanceDetectorEntity tileEntity;
    private final AtomicInteger maxRange;
    private volatile float currentDistance;
    private final AtomicBoolean showLaser;
    private volatile boolean calculatePeriodically;
    private volatile boolean ignoreTransparent;
    private final AtomicReference<DetectionType> detectionType;

    public DistanceDetectorPeripheral(DistanceDetectorEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
        this.tileEntity = tileEntity;
        this.maxRange = new AtomicInteger(Float.floatToRawIntBits(this.tileEntity.getMaxRange()));
        this.currentDistance = this.tileEntity.getCurrentDistance();
        this.showLaser = new AtomicBoolean(this.tileEntity.getShowLaser());
        this.calculatePeriodically = this.tileEntity.getCalculatePeriodically();
        this.ignoreTransparent = this.tileEntity.getIgnoreTransparent();
        this.detectionType = new AtomicReference<>(this.tileEntity.getDetectionType());
    }

    // TODO: thread safely save data
    protected DistanceDetectorPeripheral(IPeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
        this.tileEntity = null;
        CompoundTag data = this.owner.getDataStorage();
        this.maxRange = new AtomicInteger(Float.floatToRawIntBits(data.contains("maxRange") ? data.getFloat("maxRange") : this.getConfiguredMaxRange()));
        this.currentDistance = data.contains("currentDistance") ? data.getFloat("currentDistance") : -1;
        this.showLaser = new AtomicBoolean(data.contains("showLaser") ? data.getBoolean("showLaser") : true);
        this.calculatePeriodically = data.contains("calculatePeriodically") ? data.getBoolean("calculatePeriodically") : false;
        this.ignoreTransparent = data.contains("ignoreTransparent") ? data.getBoolean("ignoreTransparent") : true;
        this.detectionType = new AtomicReference<>(data.contains("detectionType") ? DetectionType.values()[data.getByte("detectionType")] : DetectionType.BOTH);
    }

    public DistanceDetectorPeripheral(IPocketAccess pocket, IPocketUpgrade upgrade) {
        this(new PocketPeripheralOwner(pocket, upgrade));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableDistanceDetector.get();
    }

    public float getConfiguredMaxRange() {
        return APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get().floatValue();
    }

    public float getMaxRange() {
        return Float.intBitsToFloat(this.maxRange.get());
    }

    public void setMaxRange(float maxRange) {
        maxRange = Math.min(Math.max(maxRange, 0), this.getConfiguredMaxRange());
        int maxRangeBits = Float.floatToRawIntBits(maxRange);
        if (this.maxRange.getAndSet(maxRangeBits) == maxRange) {
            return;
        }
        if (this.tileEntity != null) {
            this.tileEntity.setMaxRange(maxRange);
            this.tileEntity.sendUpdate();
        }
        this.owner.markDataStorageDirty();
    }

    public float getCurrentDistance() {
        return this.currentDistance;
    }

    public void setCurrentDistance(float currentDistance) {
        // Since setCurrentDistance should only invokes from main thread, volatile field should be safe here.
        if (this.currentDistance == currentDistance) {
            return;
        }
        this.currentDistance = currentDistance;
        if (this.tileEntity != null) {
            this.tileEntity.setCurrentDistance(currentDistance);
            this.tileEntity.sendUpdate();
        }
        this.owner.markDataStorageDirty();
    }

    public boolean getCalculatePeriodically() {
        return this.calculatePeriodically;
    }

    public void setCalculatePeriodically(boolean calculatePeriodically) {
        this.calculatePeriodically = calculatePeriodically;
        if (this.tileEntity != null) {
            this.tileEntity.setCalculatePeriodically(calculatePeriodically);
        }
        this.owner.markDataStorageDirty();
    }

    public boolean getShowLaser() {
        return this.showLaser.get();
    }

    public void setShowLaser(boolean showLaser) {
        if (this.showLaser.getAndSet(showLaser) == showLaser) {
            return;
        }
        if (this.tileEntity != null) {
            this.tileEntity.setShowLaser(showLaser);
            this.tileEntity.sendUpdate();
        }
        this.owner.markDataStorageDirty();
    }

    public boolean getIgnoreTransparent() {
        return this.ignoreTransparent;
    }

    public void setIgnoreTransparent(boolean ignoreTransparent) {
        this.ignoreTransparent = ignoreTransparent;
        if (this.tileEntity != null) {
            this.tileEntity.setIgnoreTransparent(ignoreTransparent);
        }
        this.owner.markDataStorageDirty();
    }

    public DetectionType getDetectionType() {
        return this.detectionType.get();
    }

    public void setDetectionType(DetectionType detectionType) {
        if (this.detectionType.getAndSet(detectionType) == detectionType) {
            return;
        }
        if (this.tileEntity != null) {
            this.tileEntity.setDetectionType(detectionType);
        }
        this.owner.markDataStorageDirty();
    }

    @LuaFunction
    public final void setLaserVisibility(boolean laser) {
        this.setShowLaser(laser);
    }

    @LuaFunction
    public final boolean getLaserVisibility() {
        return this.getShowLaser();
    }

    @LuaFunction(value = {"setIgnoreTransparency"})
    public final void setIgnoreTransparencyLua(boolean enable) {
        this.setIgnoreTransparent(enable);
    }

    @LuaFunction
    public final boolean ignoresTransparency() {
        return this.getIgnoreTransparent();
    }

    @LuaFunction
    public final void setDetectionMode(IArguments args) throws LuaException {
        Object mode = args.get(0);
        if (mode == null) {
            throw new LuaException("arg #1 must provide a mode name or an index between [0, 2]");
        }
        DetectionType detectionType;
        if (mode instanceof Number modeInd) {
            int index = Math.min(Math.max(modeInd.intValue(), 0), 2);
            detectionType = DetectionType.values()[index];
        } else if (mode instanceof String modeStr) {
            detectionType = switch (modeStr.toUpperCase()) {
                case "BLOCK" -> DetectionType.BLOCK;
                case "ENTITY" -> DetectionType.ENTITY;
                case "BOTH" -> DetectionType.BOTH;
                default -> throw new LuaException("Unknown detection mode '" + mode + "'");
            };
        } else {
            throw new LuaException("arg #1 must be a string or a number");
        }
        this.setDetectionType(detectionType);
    }

    @LuaFunction
    public final boolean detectsEntities() {
        return this.getDetectionType().detectEntity();
    }

    @LuaFunction
    public final boolean detectsBlocks() {
        return this.getDetectionType().detectBlock();
    }

    @LuaFunction
    public final String getDetectionMode() {
        return this.getDetectionType().toString();
    }

    @LuaFunction
    public final double getDistance() {
        return this.getCurrentDistance();
    }

    @LuaFunction(mainThread = true)
    public final double calculateDistance() {
        return this.calculateAndUpdateDistance();
    }

    @LuaFunction
    public final boolean shouldCalculatePeriodically() {
        return this.getCalculatePeriodically();
    }

    @LuaFunction(value = {"setCalculatePeriodically"})
    public final void setCalculatePeriodicallyLua(boolean shouldCalculatePeriodically) {
        this.setCalculatePeriodically(shouldCalculatePeriodically);
    }

    @LuaFunction(value = {"setMaxRange"})
    public final void setMaxRangeLua(double maxDistance) {
        this.setMaxRange((float) maxDistance);
    }

    @LuaFunction(value = {"getMaxRange"})
    public final double getMaxRangeLua() {
        return this.getMaxRange();
    }

    protected double calculateDistanceImpl() {
        final double maxRange = this.getMaxRange();
        Vec3 direction = this.owner.getDirection();
        Vec3 center = this.getPhysicsPos();
        Vec3 from = center;
        Vec3 to = from.add(direction.scale(maxRange));

        HitResult result = this.getHitResult(from, to);
        if (result.getType() == HitResult.Type.MISS) {
            return -1;
        }
        double distance = result.getLocation().distanceTo(center);
        if (this.tileEntity != null) {
            distance -= 0.5;
        }
        return distance;
    }

    /**
     * calculateAndUpdateDistance should only invokes from server main thread
     */
    public double calculateAndUpdateDistance() {
        double distance = this.calculateDistanceImpl();
        this.setCurrentDistance((float) distance);
        return distance;
    }

    protected HitResult getHitResult(Vec3 from, Vec3 to) {
        Level level = this.getLevel();
        ClipContext.ShapeGetter shapeGetter = this.ignoreTransparent ? HitResultUtil.IgnoreNoOccludedContext.INSTANCE : ClipContext.Block.COLLIDER;
        return switch (this.getDetectionType()) {
            case ENTITY -> HitResultUtil.getEntityHitResult(from, to, level, this.owner.getHoldingEntity());
            case BLOCK -> HitResultUtil.getBlockHitResult(from, to, level, shapeGetter, this.getPos());
            case BOTH -> HitResultUtil.getHitResult(from, to, level, shapeGetter, this.owner);
        };
    }

    public enum DetectionType {
        BLOCK(true, false),
        ENTITY(false, true),
        BOTH(true, true);

        private final boolean block, entity;

        DetectionType(boolean block, boolean entity) {
            this.block = block;
            this.entity = entity;
        }

        public boolean detectBlock() {
            return this.block;
        }

        public boolean detectEntity() {
            return this.entity;
        }
    }

}
