package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toclient.SaddleTurtleInfoPacket;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.TeleportUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Predicate;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation.SADDLE_CAPTURE;

public class SaddlePeripheral extends BasePeripheral<TurtlePeripheralOwner> {

    private static final int ANIM_DURATION = 8; // Should be same as TurtleBrain.ANIM_DURATION

    public static final String PERIPHERAL_TYPE = "saddle";
    private TurtleSeatEntity seat = null;
    private volatile Entity rider = null;
    private BlockPos lastPos = null;
    private int moveProg = 0;
    private int tickCount = 0;

    private int barColor = 0;

    public SaddlePeripheral(ITurtleAccess turtle, TurtleSide side) {
        super(PERIPHERAL_TYPE, new TurtlePeripheralOwner(turtle, side));
        owner.attachOperation(SADDLE_CAPTURE);
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableSaddleTurtle.get();
    }

    @Nullable
    public Entity getRidingEntity() {
        return this.isEntityRiding() ? this.rider : null;
    }

    public boolean isEntityRiding() {
        return this.seat != null && this.rider != null && this.seat.isAlive() && this.seat.hasPassenger(this.rider);
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        super.attach(computer);
        this.lastPos = this.owner.getPos();
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        this.standUp();
        super.detach(computer);
    }

    public void update() {
        if (this.seat != null) {
            if (!isEntityRiding()) {
                this.standUp();
                return;
            }
            this.seat.keepAlive();
            BlockPos pos = this.owner.getPos();
            Level level = this.owner.getLevel();
            if (this.seat.getLevel() != this.owner.getLevel()) {
                this.seat = TeleportUtil.teleportToWithPassengers(this.seat, (ServerLevel) level, this.seat.getTurtlePos());
                this.seat.setTurtle(this.owner.getTurtle());
                this.seat.keepAlive();
                this.rider = this.seat.getFirstPassenger();
                this.moveProg = 0;
                this.lastPos = pos;
            } else {
                BlockPos dir = pos.subtract(this.lastPos);
                int dist = Math.abs(dir.getX()) + Math.abs(dir.getY()) + Math.abs(dir.getZ());
                if (dist != 0) {
                    Vec3 newPos = this.seat.getTurtlePos();
                    if (dist == 1 && ++this.moveProg < ANIM_DURATION) {
                        float step = ((float) this.moveProg) / ANIM_DURATION;
                        newPos = newPos.add(Vec3.atLowerCornerOf(dir).scale(step - 1));
                    } else {
                        this.moveProg = 0;
                        this.lastPos = pos;
                    }
                    this.seat.moveTo(newPos);
                }
            }
            this.tickCount++;
            if (this.tickCount > 40) {
                this.tickCount = 0;
                this.sendHUD();
            }
        }
    }

    private void sendHUD() {
        if (this.rider instanceof ServerPlayer player) {
            ITurtleAccess turtle = this.owner.getTurtle();
            SaddleTurtleInfoPacket packet = new SaddleTurtleInfoPacket(turtle.getFuelLevel(), turtle.getFuelLimit(), barColor);
            APNetworking.sendTo(packet, player);
        }
    }

    private boolean sitDown(@NotNull Entity entity) {
        Level world = this.owner.getLevel();
        this.seat = new TurtleSeatEntity(this.owner.getTurtle());
        this.seat.setPos(this.seat.getTurtlePos());
        if (!world.addFreshEntity(this.seat)) {
            return false;
        }
        if (!entity.startRiding(this.seat)) {
            return false;
        }
        if (entity instanceof TamableAnimal tamable) {
            tamable.setInSittingPose(true);
        }
        this.seat.keepAlive();
        this.rider = entity;
        this.sendHUD();
        return true;
    }

    private boolean standUp() {
        if (this.seat == null) {
            return false;
        }
        Entity passenger = this.seat.getFirstPassenger();
        if (passenger != null) {
            this.seat.ejectPassengers();
            BlockPos pos = this.owner.getPos();
            passenger.dismountTo(pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5);
        }
        this.seat.discard();
        this.seat = null;
        this.rider = null;
        if (owner.getTurtle() instanceof TurtleBrain brain) {
            brain.getOwner().createServerComputer().queueEvent("saddle_release");
        }
        return passenger != null;
    }

    @LuaFunction(mainThread = true)
    public MethodResult capture() throws LuaException {
        if (isEntityRiding()) {
            return MethodResult.of(null, "Another entity is riding");
        }
        return withOperation(SADDLE_CAPTURE, null, null, context -> {
            Predicate<Entity> suitableEntity = EntitySelector.NO_SPECTATORS
                .and((entity) -> entity instanceof LivingEntity || entity instanceof AbstractMinecart || entity instanceof Boat)
                .and((entity) -> !entity.isPassenger());
            if (!APConfig.PERIPHERALS_CONFIG.allowSaddleTurtleCapturePlayer.get()) {
                suitableEntity = suitableEntity.and((entity) -> !(entity instanceof Player));
            }
            final Predicate<Entity> finalSuitableEntity = suitableEntity;
            final APFakePlayer.Action<HitResult> action = (player) -> player.findHit(false, true, finalSuitableEntity);
            HitResult entityHit = owner.withPlayer(action);
            if (entityHit.getType() == HitResult.Type.MISS) {
                entityHit = owner.withPlayer(APFakePlayer.wrapActionWithReachRange(1, APFakePlayer.wrapActionWithRot(0, -90, action)));
                if (entityHit.getType() == HitResult.Type.MISS) {
                    return MethodResult.of(null, "Nothing found");
                }
            }
            Entity entity = ((EntityHitResult) entityHit).getEntity();
            if (!sitDown(entity)) {
                return MethodResult.of(null, "Entity cannot sit");
            }
            if (owner.getTurtle() instanceof TurtleBrain brain) {
                brain.getOwner().createServerComputer().queueEvent("saddle_capture");
            }
            return MethodResult.of(true);
        }, null);
    }

    @LuaFunction(mainThread = true)
    public MethodResult release() {
        if (!standUp()) {
            return MethodResult.of(null, "No entity is riding");
        }
        return MethodResult.of(true);
    }

    @LuaFunction
    public boolean hasRider() {
        return this.rider != null;
    }

    @LuaFunction(mainThread = true)
    public MethodResult getRider(IArguments args) throws LuaException {
        boolean detailed = args.count() > 0 ? args.getBoolean(0) : false;
        Entity entity = getRidingEntity();
        if (entity == null) {
            return MethodResult.of(null, "No entity is riding");
        }
        Map<String, Object> data = LuaConverter.completeEntityToLua(entity, getPeripheralOwner().getToolInMainHand(), detailed);
        if (data.get("pitch") instanceof Number pitch) {
            data.put("pitch", (pitch.floatValue() - owner.getTurtle().getDirection().toYRot() + 360 + 180) % 360 - 180);
        }
        return MethodResult.of(data);
    }
}
