package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleSaddleUpgrade;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class SaddlePeripheral extends BasePeripheral<TurtlePeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "saddle";
    private TurtleSeatEntity seat = null;

    public SaddlePeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(PERIPHERAL_TYPE, new TurtlePeripheralOwner(turtle, side));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableSaddleTurtle.get();
    }

    @Nullable
    public Entity getRidingEntity() {
        return this.seat;
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
        super.detach(computer);
        if (this.seat != null) {
            this.seat.discard();
            this.seat = null;
        }
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
            if (!this.seat.position().equals(newPos)) {
                // TODO: for some reason the client position will not sync here
                this.seat.moveTo(newPos);
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

    private static class TurtleSeatEntity extends SeatEntity {
        private ITurtleAccess turtle;
        private int life;

        public TurtleSeatEntity(ITurtleAccess turtle) {
            super(turtle.getLevel(), turtle.getPosition());
            this.turtle = turtle;
            this.life = 1;
        }

        public ITurtleAccess getOwner() {
            return turtle;
        }

        public void keepAlive() {
            this.life = 3;
        }

        @Override
        public void tick() {
            if (this.level.isClientSide) {
                return;
            }
            BlockEntity block = this.level.getBlockEntity(this.blockPosition());
            if (this.isVehicle()) {
                this.life--;
                if (this.life > 0) {
                    return;
                }
            }
            this.discard();
        }
    }
}
