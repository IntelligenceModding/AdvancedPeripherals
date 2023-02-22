package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DistanceDetectorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<DistanceDetectorEntity>> {

    public static final String TYPE = "distanceDetector";
    private double height = 0.5;

    public DistanceDetectorPeripheral(DistanceDetectorEntity tileEntity) {
        super(TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public final boolean setLaserVisibility(boolean laser) {
        return false;
    }

    public final boolean getLaserVisibility() {
        return false;
    }

    public final boolean setTransparencyDetection(boolean enable) {
        return false;
    }

    public final boolean getTransparencyDetection() {
        return false;
    }

    public final boolean setAllowEntityDetection(boolean enable) {
        return false;
    }

    public final boolean getEntityDetection() {
        return false;
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
        //Just testing, will be the direction of the block later
        Direction direction = getPeripheralOwner().getOrientation().front();
        Vec3 from = Vec3.atCenterOf(getPos()).add(direction.getNormal().getX() * 0.501, direction.getNormal().getY() * 0.501, direction.getNormal().getZ() * 0.501);
        Vec3 to = from.add(direction.getNormal().getX() * 256, direction.getNormal().getY() * 256, direction.getNormal().getZ() * 256);
        BlockHitResult result = getLevel().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));

        float distance = 0;
        BlockState resultBlock;
        if (result.getType() != HitResult.Type.MISS) {
            resultBlock = getLevel().getBlockState(result.getBlockPos());
            distance = getPos().distManhattan(result.getBlockPos());

            if(resultBlock.getBlock() instanceof SlabBlock && direction.getAxis() == Direction.Axis.Y) {
                if(resultBlock.getValue(SlabBlock.TYPE) == SlabType.TOP && direction == Direction.UP)
                    distance = distance + 0.5f;
                if(resultBlock.getValue(SlabBlock.TYPE) == SlabType.BOTTOM && direction == Direction.DOWN)
                    distance = distance - 0.5f;
            }
        }
        return distance - 1;
    }

}
