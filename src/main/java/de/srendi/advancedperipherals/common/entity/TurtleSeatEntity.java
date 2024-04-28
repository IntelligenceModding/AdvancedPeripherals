package de.srendi.advancedperipherals.common.entity;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import dan200.computercraft.shared.turtle.items.TurtleItemFactory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import de.srendi.advancedperipherals.common.setup.APEntities;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;

public class TurtleSeatEntity extends Entity implements HasCustomInventoryScreen {

    private ITurtleAccess turtle;
    private int life;

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
        this.life--;
        if (this.life < 0) {
            this.discard();
            return;
        }
        // TODO: better rendering
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!this.level.isClientSide && this.hasPassenger(player)) {
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
}
