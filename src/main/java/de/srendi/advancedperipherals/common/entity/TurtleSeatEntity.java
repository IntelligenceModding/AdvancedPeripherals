package de.srendi.advancedperipherals.common.entity;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import de.srendi.advancedperipherals.common.network.toserver.SaddleTurtleControlPacket;
import de.srendi.advancedperipherals.common.setup.APEntities;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.util.InputKeySet;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

// TODO: better rendering
public class TurtleSeatEntity extends Entity implements HasCustomInventoryScreen {
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
        Player player = this.getSelfAndPassengers().filter(Player.class::isInstance).map(Player.class::cast).findFirst().orElse(null);
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

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void baseTick() {
        if (this.level().isClientSide && (this.turtle == null || this.turtle.isRemoved())) {
            if (this.level().getBlockEntity(this.blockPosition()) instanceof TurtleBlockEntity be) {
                this.turtle = be.getAccess();
            }
        }
        if (this.turtle != null && !this.turtle.isRemoved() && this.turtle instanceof TurtleBrain brain) {
            this.moveTo(brain.getVisualPosition(1.0f));
        }
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
                computer.queueEvent(CCEvents.SADDLE_CONTROL, new Object[]{"forward", this.inputs.forward()});
            }
            if (this.inputs.back() != this.oldInputs.back()) {
                computer.queueEvent(CCEvents.SADDLE_CONTROL, new Object[]{"back", this.inputs.back()});
            }
            if (this.inputs.left() != this.oldInputs.left()) {
                computer.queueEvent(CCEvents.SADDLE_CONTROL, new Object[]{"left", this.inputs.left()});
            }
            if (this.inputs.right() != this.oldInputs.right()) {
                computer.queueEvent(CCEvents.SADDLE_CONTROL, new Object[]{"right", this.inputs.right()});
            }
            if (this.inputs.up() != this.oldInputs.up()) {
                computer.queueEvent(CCEvents.SADDLE_CONTROL, new Object[]{"up", this.inputs.up()});
            }
            if (this.inputs.down() != this.oldInputs.down()) {
                computer.queueEvent(CCEvents.SADDLE_CONTROL, new Object[]{"down", this.inputs.down()});
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
                ItemStack stack = new ItemStack(tile.getBlockState().getBlock().asItem());
                tile.saveToItem(stack);
                PlatformHelper.get().openMenu(
                    player,
                    tile.getDisplayName(),
                    tile::createMenu,
                    new ComputerContainerData(computer, stack)
                );
            }
        }
    }

    public static class Renderer extends EntityRenderer<TurtleSeatEntity> {
        public Renderer(EntityRendererProvider.Context ctx) {
            super(ctx);
        }

        @Override
        public boolean shouldRender(TurtleSeatEntity entity, Frustum frustum, double x, double y, double z) {
            return true;
        }

        @Override
        public ResourceLocation getTextureLocation(TurtleSeatEntity entity) {
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
    public void setDeltaMovement(Vec3 movement) {}
}
