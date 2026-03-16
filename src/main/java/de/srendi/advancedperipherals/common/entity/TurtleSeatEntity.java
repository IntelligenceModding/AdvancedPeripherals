package de.srendi.advancedperipherals.common.entity;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import de.srendi.advancedperipherals.common.setup.APEntities;
import de.srendi.advancedperipherals.common.util.InputKeySet;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TurtleSeatEntity extends Entity implements HasCustomInventoryScreen {

    // TODO: better rendering

    private ITurtleAccess turtle = null;
    private int life = 0;

    private InputKeySet inputs = InputKeySet.NONE;
    private InputKeySet oldInputs = InputKeySet.NONE;

    public TurtleSeatEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true;
    }

    public TurtleSeatEntity(ITurtleAccess turtle) {
        this(APEntities.TURTLE_SEAT.get(), turtle.getLevel());
        this.turtle = turtle;
    }

    public void setTurtle(ITurtleAccess turtle) {
        this.turtle = turtle;
    }

    public ITurtleAccess getOwner() {
        return turtle;
    }

    @Nullable
    private ServerComputer getServerComputer() {
        Player player = this.getSelfAndPassengers().filter(e -> e instanceof Player).map(e -> (Player) e).findFirst().orElse(null);
        if (player != null && this.turtle instanceof TurtleBrain turtle) {
            TurtleBlockEntity tile = turtle.getOwner();
            if (tile.isUsable(player)) {
                return tile.createServerComputer();
            }
        }
        return null;
    }

    public void keepAlive() {
        this.life = 2;
    }

    public Vec3 getTurtlePos() {
        BlockPos pos = this.turtle.getPosition();
        return Vec3.atCenterOf(pos);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag storage) {}

    @Override
    public void addAdditionalSaveData(CompoundTag storage) {}

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        AABB bb = this.getBoundingBox();
        this.setBoundingBox(bb.move(new Vec3(x, y, z).subtract(bb.getCenter())));
    }

    @Override
    protected void removePassenger(Entity entity) {
        super.removePassenger(entity);
        this.inputs = InputKeySet.NONE;
        this.oldInputs = InputKeySet.NONE;
        if (entity instanceof TamableAnimal tamed) {
            tamed.setInSittingPose(false);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        return this.getTurtlePos().add(0, 0.4, 0);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return null; // this.getFirstPassenger();
    }

    // @Override
    // public double getPassengersRidingOffset() {
    //     return 0.05;
    // }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            return;
        }
        this.life--;
        if (this.life < 0) {
            this.discard();
            return;
        }
        ServerComputer computer = this.getServerComputer();
        if (computer != null && this.inputs != this.oldInputs) {
            if (this.inputs.forward() != this.oldInputs.forward()) {
                computer.queueEvent("saddle_control", new Object[]{"forward", this.inputs.forward()});
            }
            if (this.inputs.back() != this.oldInputs.back()) {
                computer.queueEvent("saddle_control", new Object[]{"back", this.inputs.back()});
            }
            if (this.inputs.left() != this.oldInputs.left()) {
                computer.queueEvent("saddle_control", new Object[]{"left", this.inputs.left()});
            }
            if (this.inputs.right() != this.oldInputs.right()) {
                computer.queueEvent("saddle_control", new Object[]{"right", this.inputs.right()});
            }
            if (this.inputs.up() != this.oldInputs.up()) {
                computer.queueEvent("saddle_control", new Object[]{"up", this.inputs.up()});
            }
            if (this.inputs.down() != this.oldInputs.down()) {
                computer.queueEvent("saddle_control", new Object[]{"down", this.inputs.down()});
            }
            this.oldInputs = this.inputs;
        }
    }

    public void handleSaddleTurtleControlPacket(SaddleTurtleControlPacket packet) {
        this.inputs = packet.inputs;
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!this.level().isClientSide && this.hasPassenger(player)) {
            if (this.inputs.down()) {
                player.stopRiding();
                this.discard();
                return;
            }
            if (this.turtle instanceof TurtleBrain turtle) {
                TurtleBlockEntity tile = turtle.getOwner();
                if (!tile.isUsable(player)) {
                    return;
                }
                ServerComputer computer = tile.createServerComputer();
                BlockState state = tile.getBlockState();
                ItemStack stack = new ItemStack(tile.getBlockState().getBlock());
                //stack.applyComponents(Util.make(DataComponentMap.builder(), tile::collectComponents).build());
                stack.applyComponents(tile.collectComponents());
                PlatformHelper.get().openMenu(player, tile.getName(), tile, new ComputerContainerData(computer, stack));
            }
        }
    }

    public static class Renderer extends EntityRenderer<TurtleSeatEntity> {
        public Renderer(EntityRendererProvider.Context ctx) {
            super(ctx);
        }

        @Override
        public boolean shouldRender(TurtleSeatEntity a0, Frustum a1, double a2, double a3, double a4) {
            return false;
        }

        @Override
        public ResourceLocation getTextureLocation(TurtleSeatEntity a0) {
            return null;
        }
    }

    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        return false;
    }

    @Override
    public boolean shouldBlockExplode(net.minecraft.world.level.Explosion a0, net.minecraft.world.level.BlockGetter a1, BlockPos a2, BlockState a3, float a4) {
        return false;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public void setDeltaMovement(Vec3 a0) {}
}
