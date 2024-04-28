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
import de.srendi.advancedperipherals.common.network.PacketHandler;
import de.srendi.advancedperipherals.common.network.toclient.RidingTurtleInfoPacket;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

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
        this.lastPos = owner.getPos();
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
            BlockPos pos = owner.getPos();
            BlockPos dir = pos.subtract(this.lastPos);
            int dist = Math.abs(dir.getX()) + Math.abs(dir.getY()) + Math.abs(dir.getZ());
            if (dist != 0) {
                Vec3 newPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5);
                if (dist == 1 && ++this.moveProg < ANIM_DURATION) {
                    float step = ((float) this.moveProg) / ANIM_DURATION;
                    newPos = newPos.add(Vec3.atLowerCornerOf(dir).scale(step - 1));
                } else {
                    this.moveProg = 0;
                    this.lastPos = pos;
                }
                this.seat.moveTo(newPos.x(), newPos.y(), newPos.z());
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
            ITurtleAccess turtle = owner.getTurtle();
            RidingTurtleInfoPacket packet = new RidingTurtleInfoPacket(turtle.getFuelLevel(), turtle.getFuelLimit(), barColor);
            PacketHandler.sendTo(packet, player);
        }
    }

    private void clearHUD() {
        if (this.rider instanceof ServerPlayer player) {
            PacketHandler.sendTo(new RidingTurtleInfoPacket(-1, -1, 0), player);
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
        this.rider = entity;
        this.sendHUD();
        return true;
    }

    private boolean standUp() {
        if (this.seat == null) {
            return false;
        }
        this.clearHUD();
        boolean isVehicle = this.seat.isVehicle();
        this.seat.discard();
        this.seat = null;
        this.rider = null;
        if (owner.getTurtle() instanceof TurtleBrain brain) {
            brain.getOwner().createServerComputer().queueEvent("saddle_release");
        }
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
        if (owner.getTurtle() instanceof TurtleBrain brain) {
            brain.getOwner().createServerComputer().queueEvent("saddle_capture");
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
        return MethodResult.of(LuaConverter.completeEntityToLua(entity, getPeripheralOwner().getToolInMainHand(), detailed));
    }
}
