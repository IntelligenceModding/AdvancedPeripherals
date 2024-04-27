package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Predicate;

public class SaddlePeripheral extends BasePeripheral<TurtlePeripheralOwner> {

    private static final int ANIM_DURATION = 8; // Should be same as TurtleBrain.ANIM_DURATION

    public static final String PERIPHERAL_TYPE = "saddle";
    private volatile TurtleSeatEntity seat = null;
    private int moveProg = 0;
    private BlockPos moveDir = null;

    public SaddlePeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(PERIPHERAL_TYPE, new TurtlePeripheralOwner(turtle, side));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableSaddleTurtle.get();
    }

    @Nullable
    public Entity getRidingEntity() {
        return this.seat != null ? this.seat.getFirstPassenger() : null;
    }

    public boolean isEntityRiding() {
        return this.seat != null && this.seat.isAlive() && this.seat.isVehicle();
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        super.attach(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        standUp();
        super.detach(computer);
    }

    public void update() {
        if (this.seat != null) {
            if (!this.seat.isAlive()) {
                this.seat = null;
                return;
            }
            this.seat.keepAlive();
            BlockPos pos = owner.getPos();
            Vec3 newPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5);
            BlockPos dir = pos.subtract(this.seat.blockPosition());
            int dist = Math.abs(dir.getX()) + Math.abs(dir.getY()) + Math.abs(dir.getZ());
            if (dist > 1) {
                this.moveDir = null;
            } else if (dist == 1) {
                this.moveDir = dir;
            }
            if (this.moveDir != null) {
                this.moveProg++;
                if (this.moveProg > ANIM_DURATION) {
                    this.moveProg = 0;
                    this.moveDir = null;
                } else {
                    float step = ((float) this.moveProg) / ANIM_DURATION;
                    newPos = newPos.add(Vec3.atLowerCornerOf(moveDir).scale(step - 1));
                }
            }
            if (!this.seat.position().equals(newPos)) {
                this.seat.moveTo(newPos.x(), newPos.y(), newPos.z());
            }
        }
    }

    private boolean sitDown(@NotNull Entity entity) {
        Level world = owner.getLevel();
        BlockPos pos = owner.getPos();
        this.seat = new TurtleSeatEntity(owner.getTurtle());
        this.seat.setPos(pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5);
        if (!world.addFreshEntity(this.seat)) {
            return false;
        }
        if (!entity.startRiding(this.seat, true)) {
            return false;
        }
        if (entity instanceof TamableAnimal tamable) {
            tamable.setInSittingPose(true);
        }
        this.seat.keepAlive();
        return true;
    }

    private boolean standUp() {
        if (this.seat == null) {
            return false;
        }
        boolean isVehicle = this.seat.isVehicle();
        this.seat.discard();
        this.seat = null;
        return isVehicle;
    }

    @LuaFunction(mainThread = true)
    public MethodResult capture() {
        if (isEntityRiding()) {
            return MethodResult.of(null, "Another entity is riding");
        }
        Predicate<Entity> suitableEntity = (entity) -> entity.isAlive();
        if (!APConfig.PERIPHERALS_CONFIG.allowSaddleTurtleCapturePlayer.get()) {
            Predicate<Entity> oldSuitableEntity = suitableEntity;
            suitableEntity = (entity) -> oldSuitableEntity.test(entity) && !(entity instanceof Player);
        }
        final Predicate<Entity> finalSuitableEntity = suitableEntity;
        HitResult entityHit = owner.withPlayer(player -> player.findHit(false, true, finalSuitableEntity));
        if (entityHit.getType() == HitResult.Type.MISS) {
            return MethodResult.of(null, "Nothing found");
        }
        LivingEntity entity = (LivingEntity) ((EntityHitResult) entityHit).getEntity();
        if (!sitDown(entity)) {
            return MethodResult.of(null, "Entity cannot sit");
        }
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public MethodResult release() {
        if (!standUp()) {
            return MethodResult.of(null, "No entity is riding");
        }
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public boolean hasRider() {
        return this.isEntityRiding();
    }

    @LuaFunction(mainThread = true)
    public MethodResult getRider() {
        Entity entity = getRidingEntity();
        if (entity == null) {
            return MethodResult.of(null);
        }
        return MethodResult.of(LuaConverter.completeEntityToLua(entity));
    }
}
