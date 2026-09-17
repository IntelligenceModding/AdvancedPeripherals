package de.srendi.advancedperipherals.common.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.items.SmartChestMountItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;

public class SmartChestHand extends Entity {
    private static final float MAX_RANGE = 10;
    public static final EntityDataAccessor<ItemStack> DATA_HOLDING_STACK = SynchedEntityData.defineId(SmartChestHand.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> DATA_LEFT_HAND = SynchedEntityData.defineId(SmartChestHand.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Vector3f> DATA_REL_POS = SynchedEntityData.defineId(SmartChestHand.class, EntityDataSerializers.VECTOR3);

    private final ItemStack chestStack;
    private int attackStrengthTicker = 0;

    public SmartChestHand(EntityType<?> type, Level level) {
        super(type, level);
        this.chestStack = ItemStack.EMPTY;
    }

    public SmartChestHand(EntityType<?> type, ServerLevel level, ItemStack chestStack, boolean isLeft) {
        super(type, level);
        this.chestStack = chestStack;
        this.entityData.set(DATA_LEFT_HAND, isLeft);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_HOLDING_STACK, ItemStack.EMPTY);
        builder.define(DATA_LEFT_HAND, false);
        builder.define(DATA_REL_POS, new Vector3f());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

    public ItemStack getHoldingStack() {
        return this.entityData.get(DATA_HOLDING_STACK);
    }

    public void setHoldingStack(ItemStack stack) {
        if (ItemStack.matches(this.getHoldingStack(), stack)) {
            return;
        }
        this.entityData.set(DATA_HOLDING_STACK, stack);
        this.resetAttackStrengthTicker();
    }

    public ItemDisplayContext getDisplayContext() {
        return this.entityData.get(DATA_LEFT_HAND)
            ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND
            : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    public LivingEntity getOwner() {
        return this.getVehicle() instanceof LivingEntity owner ? owner : null;
    }

    public boolean stillValid() {
        LivingEntity owner = this.getOwner();
        if (owner == null) {
            return false;
        }
        if (owner.isRemoved()) {
            return false;
        }
        return SmartChestMountItem.getEquipped(owner) == this.chestStack;
    }

    @Override
    public Vec3 getVehicleAttachmentPoint(Entity owner) {
        Vec3 pos = owner.getPassengerRidingPosition(this);
        Vec3 basePos = owner.getEyePosition();
        return pos.subtract(basePos.add(new Vec3(this.getRelativePos())));
    }

    public Vector3f getRelativePos() {
        return this.entityData.get(DATA_REL_POS);
    }

    public void setRelativePos(Vector3f pos) {
        if (pos.lengthSquared() > MAX_RANGE * MAX_RANGE) {
            pos.normalize(MAX_RANGE);
        }
        this.entityData.set(DATA_REL_POS, pos);
    }

    public void setRelativePos(float x, float y, float z) {
        this.setRelativePos(new Vector3f(x, y, z));
    }

    public void addRelativePos(float x, float y, float z) {
        this.setRelativePos(this.getRelativePos().add(x, y, z, new Vector3f()));
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return true;
    }

    public int getAttackStrengthTicker() {
        return this.attackStrengthTicker;
    }

    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }

    @Override
    public void tick() {
        if (this.getOwner() == null) {
            this.discard();
            return;
        }
        super.tick();
        if (this.level() instanceof ServerLevel) {
            if (!this.stillValid()) {
                this.discard();
                return;
            }
        }
        this.attackStrengthTicker++;
    }

    public static class Renderer extends EntityRenderer<SmartChestHand> {
        private final ItemRenderer itemRenderer;

        public Renderer(EntityRendererProvider.Context ctx) {
            super(ctx);
            this.itemRenderer = ctx.getItemRenderer();
        }

        @Override
        public ResourceLocation getTextureLocation(SmartChestHand entity) {
            return null;
        }

        @Override
        public void render(SmartChestHand entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
            LivingEntity owner = entity.getOwner();
            if (owner == null) {
                return;
            }
            // TODO: draw arm & hand

            ItemStack stack = entity.getHoldingStack();
            if (stack.isEmpty()) {
                return;
            }
            this.itemRenderer.renderStatic(
                null,
                stack,
                entity.getDisplayContext(),
                false,
                poseStack,
                bufferSource,
                entity.level(),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                entity.getId() + entity.getDisplayContext().ordinal()
            );
        }
    }
}
