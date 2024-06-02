package de.srendi.advancedperipherals.common.entity;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import dan200.computercraft.shared.turtle.items.TurtleItemFactory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import de.srendi.advancedperipherals.common.setup.APEntities;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

public class TurtleSeatEntity extends Entity implements HasCustomInventoryScreen {

    // TODO: better rendering

    private ITurtleAccess turtle;
    private int life;

    private boolean forwardKey = false;
    private boolean backKey = false;
    private boolean leftKey = false;
    private boolean rightKey = false;
    private boolean upKey = false;
    private boolean downKey = false;
    private boolean forwardKeyOld = false;
    private boolean backKeyOld = false;
    private boolean leftKeyOld = false;
    private boolean rightKeyOld = false;
    private boolean upKeyOld = false;
    private boolean downKeyOld = false;

    public TurtleSeatEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true;
    }

    public TurtleSeatEntity(ITurtleAccess turtle) {
        this(APEntities.TURTLE_SEAT.get(), turtle.getLevel());
        this.turtle = turtle;
        this.life = 0;
    }

    public ITurtleAccess getOwner() {
        return turtle;
    }

    @Nullable
    private ServerComputer getServerComputer() {
        Player player = this.getSelfAndPassengers().filter(e -> e instanceof Player).map(e -> (Player) e).findFirst().orElse(null);
        if (player != null && this.turtle instanceof TurtleBrain turtle) {
            TileTurtle tile = turtle.getOwner();
            if (tile.isUsable(player)) {
                return tile.createServerComputer();
            }
        }
        return null;
    }

    public void keepAlive() {
        this.life = 2;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag storage) {}

    @Override
    public void addAdditionalSaveData(CompoundTag storage) {}

    @Override
    protected void defineSynchedData() {}

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        AABB bb = this.getBoundingBox();
        this.setBoundingBox(bb.move(new Vec3(x, y, z).subtract(bb.getCenter())));
    }

    @Override
    protected void removePassenger(Entity entity) {
        super.removePassenger(entity);
        this.forwardKey = false;
        this.backKey = false;
        this.leftKey = false;
        this.rightKey = false;
        this.upKey = false;
        this.downKey = false;
        this.forwardKeyOld = false;
        this.backKeyOld = false;
        this.leftKeyOld = false;
        this.rightKeyOld = false;
        this.upKeyOld = false;
        this.downKeyOld = false;
        if (entity instanceof TamableAnimal tamed) {
            tamed.setInSittingPose(false);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        return super.getDismountLocationForPassenger(entity).add(0, 0.5, 0);
    }

    @Override
    public Entity getControllingPassenger() {
        return null; // this.getFirstPassenger();
    }

    @Override
    public void tick() {
        if (this.level.isClientSide) {
            return;
        }
        this.life--;
        if (this.life < 0) {
            this.discard();
            return;
        }
        ServerComputer computer = this.getServerComputer();
        if (computer != null) {
            if (this.forwardKey != this.forwardKeyOld) {
                this.forwardKeyOld = this.forwardKey;
                computer.queueEvent("saddle_control", new Object[]{"forward", this.forwardKey});
            }
            if (this.backKey != this.backKeyOld) {
                this.backKeyOld = this.backKey;
                computer.queueEvent("saddle_control", new Object[]{"back", this.backKey});
            }
            if (this.leftKey != this.leftKeyOld) {
                this.leftKeyOld = this.leftKey;
                computer.queueEvent("saddle_control", new Object[]{"left", this.leftKey});
            }
            if (this.rightKey != this.rightKeyOld) {
                this.rightKeyOld = this.rightKey;
                computer.queueEvent("saddle_control", new Object[]{"right", this.rightKey});
            }
            if (this.upKey != this.upKeyOld) {
                this.upKeyOld = this.upKey;
                computer.queueEvent("saddle_control", new Object[]{"up", this.upKey});
            }
            if (this.downKey != this.downKeyOld) {
                this.downKeyOld = this.downKey;
                computer.queueEvent("saddle_control", new Object[]{"down", this.downKey});
            }
        }
    }

    public void handleSaddleTurtleControlPacket(SaddleTurtleControlPacket packet) {
        this.forwardKey = packet.forward;
        this.backKey = packet.back;
        this.leftKey = packet.left;
        this.rightKey = packet.right;
        this.upKey = packet.up;
        this.downKey = packet.down;
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!this.level.isClientSide && this.hasPassenger(player)) {
            if (this.downKey) {
                player.stopRiding();
                this.discard();
                return;
            }
            if (this.turtle instanceof TurtleBrain turtle) {
                TileTurtle tile = turtle.getOwner();
                if (!tile.isUsable(player)) {
                    return;
                }
                ServerComputer computer = tile.createServerComputer();
                ItemStack stack = TurtleItemFactory.create(tile);
                new ComputerContainerData(computer, stack).open(player, tile);
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
    public boolean canChangeDimensions() {
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
