package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import java.util.Locale;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.util.HitResultUtil;
import de.srendi.advancedperipherals.lib.peripherals.AbstractDataStorage;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DistanceDetectorPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "distance_detector";

    private volatile boolean isDirty = true;
    private final DistanceDetectorEntity tileEntity;
    private float maxRange;
    private float currentDistance;
    private boolean showLaser;
    private boolean calculatePeriodically;
    private boolean ignoreTransparent;
    private DetectionType detectionType;

    protected DistanceDetectorPeripheral(IPeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
        this.tileEntity = owner instanceof BlockEntityPeripheralOwner<?> beOwner ? (DistanceDetectorEntity) beOwner.getBlockEntity() : null;
        try (AbstractDataStorage.ReadView storage = this.owner.getDataStorage().allocRead()) {
            CompoundTag data = storage.getData();
            this.maxRange = data.contains(APDataComponents.MAX_RANGE) ? data.getFloat(APDataComponents.MAX_RANGE) : this.getConfiguredMaxRange();
            this.currentDistance = data.contains(APDataComponents.CURRENT_DISTANCE) ? data.getFloat(APDataComponents.CURRENT_DISTANCE) : -1f;
            this.showLaser = data.contains(APDataComponents.SHOW_LASER) ? data.getBoolean(APDataComponents.SHOW_LASER) : true;
            this.calculatePeriodically = data.contains(APDataComponents.CALCULATE_PERIODICALLY) ? data.getBoolean(APDataComponents.CALCULATE_PERIODICALLY) : false;
            this.ignoreTransparent = data.contains(APDataComponents.IGNORE_TRANSPARENT) ? data.getBoolean(APDataComponents.IGNORE_TRANSPARENT) : true;
            this.detectionType = data.contains(APDataComponents.DETECTION_TYPE) ? DetectionType.values()[data.getByte(APDataComponents.DETECTION_TYPE)] : DetectionType.BOTH;
        }
    }

    public DistanceDetectorPeripheral(DistanceDetectorEntity tileEntity) {
        this(new BlockEntityPeripheralOwner<>(tileEntity));
    }

    public DistanceDetectorPeripheral(IPocketAccess pocket) {
        this(PocketPeripheralOwner.of(pocket));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableDistanceDetector.get();
    }

    public float getConfiguredMaxRange() {
        return APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get().floatValue();
    }

    public int getUpdateRate() {
        return APConfig.PERIPHERALS_CONFIG.distanceDetectorUpdateRate.get();
    }

    public float getMaxRange() {
        return this.maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = Math.min(Math.max(maxRange, 0), this.getConfiguredMaxRange());
        this.isDirty = true;
    }

    public float getCurrentDistance() {
        return this.currentDistance;
    }

    public void setCurrentDistance(float currentDistance) {
        if (this.currentDistance == currentDistance) {
            return;
        }
        this.currentDistance = currentDistance;
        this.isDirty = true;
    }

    public boolean getCalculatePeriodically() {
        return this.calculatePeriodically;
    }

    public void setCalculatePeriodically(boolean calculatePeriodically) {
        this.calculatePeriodically = calculatePeriodically;
        this.isDirty = true;
    }

    public boolean getShowLaser() {
        return this.showLaser;
    }

    public void setShowLaser(boolean showLaser) {
        this.showLaser = showLaser;
        this.isDirty = true;
    }

    public boolean getIgnoreTransparent() {
        return this.ignoreTransparent;
    }

    public void setIgnoreTransparent(boolean ignoreTransparent) {
        this.ignoreTransparent = ignoreTransparent;
        this.isDirty = true;
    }

    public DetectionType getDetectionType() {
        return this.detectionType;
    }

    public void setDetectionType(DetectionType detectionType) {
        this.detectionType = detectionType;
        this.isDirty = true;
    }

    @LuaFunction
    public final boolean getLaserVisibility() {
        return this.getShowLaser();
    }

    @LuaFunction
    public final void setLaserVisibility(boolean laser) {
        this.setShowLaser(laser);
    }

    @LuaFunction("getIgnoreTransparent")
    public final boolean getIgnoreTransparentLua() {
        return this.getIgnoreTransparent();
    }

    @LuaFunction("setIgnoreTransparency")
    public final void setIgnoreTransparencyLua(boolean enable) {
        this.setIgnoreTransparent(enable);
    }

    @LuaFunction
    public final boolean detectingEntities() {
        return this.getDetectionType().detectEntity();
    }

    @LuaFunction
    public final boolean detectingBlocks() {
        return this.getDetectionType().detectBlock();
    }

    @LuaFunction
    public final MethodResult getDetectionMode() {
        DetectionType type = this.getDetectionType();
        return MethodResult.of(type.name(), type.ordinal());
    }

    @LuaFunction
    public final void setDetectionMode(IArguments args) throws LuaException {
        Object mode = args.get(0);
        if (mode == null) {
            throw new LuaException("argument #1 must provide a mode name or an index between [0, 2]");
        }
        DetectionType detectionType;
        if (mode instanceof Number modeInd) {
            int index = Math.min(Math.max(modeInd.intValue(), 0), 2);
            detectionType = DetectionType.values()[index];
        } else if (mode instanceof String modeStr) {
            detectionType = switch (modeStr.toUpperCase(Locale.ROOT)) {
                case "BLOCK" -> DetectionType.BLOCK;
                case "ENTITY" -> DetectionType.ENTITY;
                case "BOTH" -> DetectionType.BOTH;
                default -> throw new LuaException("Unknown detection mode '" + mode + "'");
            };
        } else {
            throw LuaValues.badArgumentOf(args, 0, "string or number");
        }
        this.setDetectionType(detectionType);
    }

    @LuaFunction
    public final double getDistance() {
        return this.getCurrentDistance();
    }

    @LuaFunction(mainThread = true)
    public final double calculateDistance() {
        return this.calculateAndUpdateDistance();
    }

    @LuaFunction("getCalculatePeriodically")
    public final boolean getCalculatePeriodicallyLua() {
        return this.getCalculatePeriodically();
    }

    @LuaFunction("setCalculatePeriodically")
    public final void setCalculatePeriodicallyLua(boolean shouldCalculatePeriodically) {
        this.setCalculatePeriodically(shouldCalculatePeriodically);
    }

    @LuaFunction("getMaxRange")
    public final double getMaxRangeLua() {
        return this.getMaxRange();
    }

    @LuaFunction("setMaxRange")
    public final void setMaxRangeLua(double maxDistance) {
        this.setMaxRange((float) maxDistance);
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

    @Override
    public void update() {
        if (this.getCalculatePeriodically() && this.getLevel().getGameTime() % this.getUpdateRate() == 0) {
            // We calculate the distance every 2 ticks, so we do not have to run the getDistance function of the peripheral
            // on the main thread which prevents the 1 tick yield time of the function.
            // The calculateDistance function is not thread safe, so we have to run it on the main thread.
            // It should be okay to run that function every 2 ticks, calculating it does not take too much time.
            this.calculateAndUpdateDistance();
        }

        if (this.isDirty) {
            this.isDirty = false;
            try (AbstractDataStorage.WriteView storage = this.owner.getDataStorage().allocWrite()) {
                CompoundTag data = storage.getData();
                data.putFloat(APDataComponents.MAX_RANGE, this.getMaxRange());
                data.putFloat(APDataComponents.CURRENT_DISTANCE, this.getCurrentDistance());
                data.putBoolean(APDataComponents.SHOW_LASER, this.getShowLaser());
                data.putBoolean(APDataComponents.CALCULATE_PERIODICALLY, this.getCalculatePeriodically());
                data.putBoolean(APDataComponents.IGNORE_TRANSPARENT, this.getIgnoreTransparent());
                data.putByte(APDataComponents.DETECTION_TYPE, (byte) this.getDetectionType().ordinal());
                storage.setData(data);
            }
            if (this.tileEntity != null) {
                this.tileEntity.setMaxRange(this.getMaxRange());
                this.tileEntity.setCurrentDistance(this.getCurrentDistance());
                this.tileEntity.setShowLaser(this.getShowLaser());
                this.tileEntity.sendUpdate();
            }
        }
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

    public enum DetectionType implements StringRepresentable {
        BLOCK("block", true, false),
        ENTITY("entity", false, true),
        BOTH("both", true, true);

        private final String name;
        private final boolean block, entity;

        DetectionType(String name, boolean block, boolean entity) {
            this.name = name;
            this.block = block;
            this.entity = entity;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public boolean detectBlock() {
            return this.block;
        }

        public boolean detectEntity() {
            return this.entity;
        }
    }
}
