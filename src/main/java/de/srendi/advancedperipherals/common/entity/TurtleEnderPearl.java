package de.srendi.advancedperipherals.common.entity;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APEntities;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class TurtleEnderPearl extends ThrowableProjectile {

    private ITurtleAccess turtle = null;
    private BlockPos spawnPos = null;
    private Consumer<TurtleEnderPearl> callback = null;
    private int life = 20;
    private boolean changedDim = false;

    public TurtleEnderPearl(EntityType<TurtleEnderPearl> type, Level world) {
        super(type, world);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public TurtleEnderPearl(ITurtleAccess turtle, @Nullable Direction direction) {
        this(APEntities.TURTLE_ENDER_PEARL.get(), turtle.getLevel());
        this.turtle = turtle;
        this.spawnPos = turtle.getPosition();
        this.setPos(Vec3.atCenterOf(this.spawnPos));
        if (direction == null) {
            direction = turtle.getDirection();
        }
        this.setDeltaMovement(Vec3.atLowerCornerOf(direction.getNormal()).scale(1 / 20.0));
    }

    @Nullable
    public ITurtleAccess getTurtle() {
        return turtle;
    }

    public void setCallback(Consumer<TurtleEnderPearl> callback) {
        this.callback = callback;
    }

    @Nullable
    private ServerComputer getServerComputer() {
        if (this.turtle instanceof TurtleBrain turtle) {
            return turtle.getOwner().createServerComputer();
        }
        return null;
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
    public void tick() {
        if (this.level.isClientSide) {
            super.tick();
            return;
        }
        if (this.turtle == null) {
            this.discard();
            return;
        }
        if (this.life < 0) {
            this.life--;
            // clean after 5s
            if (this.life < -100) {
                this.discard();
                return;
            }
            return;
        }
        if (this.life == 0) {
            this.life = -1;
            this.setDeltaMovement(Vec3.ZERO);
            this.moveTo(Vec3.atCenterOf(this.blockPosition()));
            AdvancedPeripherals.debug("Turtle Ender Pearl stabled: " + this.toString());
            if (this.callback != null) {
                this.callback.accept(this);
            }
            return;
        }
        this.life--;
        super.tick();
    }

    // TODO: the turtle ender pearl should have a fancy and 999w flash render
    public static class Renderer extends EntityRenderer<TurtleEnderPearl> {
        public Renderer(EntityRendererProvider.Context ctx) {
            super(ctx);
        }

        @Override
        public boolean shouldRender(TurtleEnderPearl entity, Frustum view, double x, double y, double z) {
            return entity.turtle != null && super.shouldRender(entity, view, x, y, z);
        }

        @Override
        public ResourceLocation getTextureLocation(TurtleEnderPearl entity) {
            return null;
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        if (reason.shouldDestroy()) {
            if (this.callback != null) {
                this.callback.accept(null);
            }
        }
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (!(entity instanceof TurtleEnderPearl oldPearl)) {
            return;
        }
        this.turtle = oldPearl.turtle;
        this.callback = oldPearl.callback;
        this.life = oldPearl.life + 20;
        this.changedDim = true;
    }

    @Override
    public boolean canChangeDimensions() {
        return !this.changedDim && super.canChangeDimensions();
    }

    @Override
    public Entity changeDimension(ServerLevel newWorld) {
        if (this.changedDim) {
            return null;
        }
        Entity newEntity = super.changeDimension(newWorld);
        this.changedDim = newEntity != null;
        if (newEntity instanceof TurtleEnderPearl newPearl) {
            AdvancedPeripherals.debug("Turtle Ender Pearl crossed to dimension " + newWorld.dimension().toString());
            newPearl.spawnPos = newPearl.blockPosition();
            ChunkManager.get(newWorld).addForceChunk(newWorld, newPearl.getUUID(), newPearl.chunkPosition());
            if (newWorld.dimension() == Level.END) {
                newPearl.life = 0;
                // do not spawn turtle on the obsidian platform
                final int maxHeight = newWorld.getMaxBuildHeight();
                int lowestY = maxHeight;
                for (; lowestY > newPearl.spawnPos.getY() + 2; lowestY--) {
                    if (!newWorld.getBlockState(newPearl.spawnPos.atY(lowestY - 1)).isAir()) {
                        break;
                    }
                }
                AdvancedPeripherals.debug("Turtle Ender Pearl lowest Y: " + lowestY);
                for (int y = lowestY; y <= maxHeight; y++) {
                    BlockPos pos = newPearl.spawnPos.atY(y);
                    List<TurtleEnderPearl> pearlList = newWorld.<TurtleEnderPearl>getEntities(APEntities.TURTLE_ENDER_PEARL.get(), new AABB(pos), entity -> true);
                    if (pearlList.isEmpty()) {
                        AdvancedPeripherals.debug("Turtle Ender Pearl moved to " + pos);
                        newPearl.moveTo(Vec3.atCenterOf(pos));
                        break;
                    }
                }
            }
        }
        return newEntity;
    }

    @Override
    protected void onHit(HitResult hit) {
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            if (this.spawnPos == null || !this.spawnPos.equals(blockHit.getBlockPos())) {
                this.discard();
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return false;
    }
}
