package de.srendi.advancedperipherals.common.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.items.SmartChestplateItem;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.common.util.fakeplayer.SmartHandFakePlayerProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SmartChestHand extends Entity {
    private static final float MAX_RANGE = 10;
    public static final EntityDataAccessor<ItemStack> DATA_HOLDING_STACK = SynchedEntityData.defineId(SmartChestHand.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> DATA_LEFT_HAND = SynchedEntityData.defineId(SmartChestHand.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Vector3f> DATA_REL_POS = SynchedEntityData.defineId(SmartChestHand.class, EntityDataSerializers.VECTOR3);

    private final ItemStack chestStack;
    private final int index;

    private double interactRange = 0.8;

    private boolean didAction = false;
    private int attackStrengthTicker = 0;

    private BlockPos breakingPos = null;
    private float breakingProg = 0;
    private int breakingTicks = 0;

    public SmartChestHand(EntityType<?> type, Level level) {
        super(type, level);
        this.chestStack = ItemStack.EMPTY;
        this.index = -1;
    }

    public SmartChestHand(EntityType<?> type, ServerLevel level, ItemStack chestStack, int index) {
        super(type, level);
        this.chestStack = chestStack;
        this.index = index;
        this.entityData.set(DATA_LEFT_HAND, false/*this.index % 2 != 0*/);
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

    public boolean allocAction() {
        if (this.didAction) {
            return false;
        }
        this.didAction = true;
        return true;
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

    public float breaking(BlockPos pos, float speed) {
        this.breakingTicks = 0;
        if (!pos.equals(this.breakingPos)) {
            this.breakingPos = pos;
            this.breakingProg = 0;
        }
        this.breakingProg += speed;
        if (this.breakingProg < 1) {
            this.level().destroyBlockProgress(this.getId(), pos, (int) (this.breakingProg * 10));
            return this.breakingProg;
        }
        this.breakingPos = null;
        this.breakingProg = 0;
        this.level().destroyBlockProgress(this.getId(), pos, -1);
        return 1;
    }

    public boolean stillValid() {
        LivingEntity owner = this.getOwner();
        if (owner == null) {
            return false;
        }
        if (owner.isRemoved()) {
            return false;
        }
        return SmartChestplateItem.getEquipped(owner) == this.chestStack;
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
        this.didAction = false;
        this.attackStrengthTicker++;
        if (this.breakingProg > 0) {
            this.breakingTicks++;
            if (this.breakingTicks > 20) {
                this.breakingPos = null;
                this.breakingProg = 0;
                this.breakingTicks = 0;
            }
        }
    }

    public MethodResult doAction(APFakePlayer.Action<MethodResult> action) throws LuaException {
        if (this.didAction) {
            return MethodResult.of(null, "ACTION_CONFLICT");
        }
        this.didAction = true;
        LivingEntity owner = this.getOwner();
        return SmartHandFakePlayerProvider.doAction(
            owner, index, this.position(), this.chestStack,
            APFakePlayer.wrapActionWithRot(
                this.getYRot(), this.getXRot(),
                APFakePlayer.wrapActionWithReachRange(this.interactRange, action)
            )
        );
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
            poseStack.pushPose();
            poseStack.mulPose(
                new Quaternionf()
                    .rotationYXZ(
                        Mth.DEG_TO_RAD * (180 - entity.getViewYRot(partialTick)),
                        Mth.DEG_TO_RAD * entity.getViewXRot(partialTick),
                        0
                    )
            );
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
            poseStack.popPose();
        }
    }
}
