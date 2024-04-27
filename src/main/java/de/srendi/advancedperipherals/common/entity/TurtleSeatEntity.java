package de.srendi.advancedperipherals.common.entity;

import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.setup.APEntities;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;

public class TurtleSeatEntity extends Entity {

    private ITurtleAccess turtle;
    private int life;

    public TurtleSeatEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true;
    }

    public TurtleSeatEntity(ITurtleAccess turtle) {
        this(APEntities.TURTLE_SEAT.get(), turtle.getLevel());
        this.turtle = turtle;
        this.life = 1;
    }

    public ITurtleAccess getOwner() {
        return turtle;
    }

    public void keepAlive() {
        this.life = 3;
    }

    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public void readAdditionalSaveData(CompoundTag storage) {}

    public void addAdditionalSaveData(CompoundTag storage) {}

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
        if (entity instanceof TamableAnimal tamed) {
            tamed.setInSittingPose(false);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
        return super.getDismountLocationForPassenger(entity).add(0, 0.5, 0);
    }

    @Override
    public void tick() {
        if (this.level.isClientSide) {
            return;
        }
        if (this.isVehicle()) {
            this.life--;
            if (this.life > 0) {
                return;
            }
        }
        this.discard();
    }

    public static class Renderer extends EntityRenderer<TurtleSeatEntity> {
        public Renderer(EntityRendererProvider.Context ctx) {
            super(ctx);
        }

        @Override
        public boolean shouldRender(TurtleSeatEntity $$0, Frustum $$1, double $$2, double $$3, double $$4) {
            return false;
        }

        @Override
        public ResourceLocation getTextureLocation(TurtleSeatEntity $$0) {
            return null;
        }
    }
}
