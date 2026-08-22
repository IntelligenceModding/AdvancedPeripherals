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
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DistanceDetectorEntity extends PeripheralBlockEntity<DistanceDetectorPeripheral> {

    private volatile float maxRange = APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get().floatValue();
    private volatile float currentDistance = -1;
    private volatile boolean showLaser = true;

    private int lerpSteps = 0;
    public float currentDistanceLerped = 0;
    public float currentDistanceO = 0;

    public DistanceDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.DISTANCE_DETECTOR.get(), pos, state);
    }

    @Override
    @NotNull
    protected DistanceDetectorPeripheral buildPeripheral() {
        return new DistanceDetectorPeripheral(this);
    }

    public float getMaxRange() {
        return this.maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = Math.min(Math.max(maxRange, 0), APConfig.PERIPHERALS_CONFIG.distanceDetectorRange.get().floatValue());
    }

    public boolean detected() {
        return this.currentDistance != -1f;
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

    @Override
    public void load(@NotNull CompoundTag compound) {
        this.setMaxRange(compound.getFloat("maxRange"));
        this.currentDistance = compound.getFloat("currentDistance");
        this.lerpSteps = 2;
        this.showLaser = compound.getBoolean("showLaser");
        super.load(compound);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compound = super.getUpdateTag();
        compound.putFloat("maxRange", this.maxRange);
        compound.putFloat("currentDistance", this.currentDistance);
        compound.putBoolean("showLaser", this.showLaser);
        return compound;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public <U extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<U> type) {
        super.handleTick(level, state, type);
        if (!level.isClientSide()) {
            return;
        }
        this.currentDistanceO = this.currentDistanceLerped;
        if (this.lerpSteps > 0) {
            float dt = 1.0f / this.lerpSteps;
            this.lerpSteps--;
            float distance = this.currentDistance;
            this.currentDistanceLerped = Mth.lerp(dt, this.currentDistanceLerped, distance == -1 ? this.getMaxRange() : distance);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        float currentDistance = this.getCurrentDistance();
        if (currentDistance == -1) {
            currentDistance = this.getMaxRange();
        }
        currentDistance += 1.5f;
        Direction direction = this.getBlockState().getValue(BaseBlock.ORIENTATION).front();
        Vec3 blockPos = Vec3.atCenterOf(this.getBlockPos());
        return new AABB(blockPos, blockPos.add(direction.getStepX() * currentDistance, direction.getStepY() * currentDistance, direction.getStepZ() * currentDistance));
    }
}
