package de.srendi.advancedperipherals.common.util.fakeplayer;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class APFakePlayer extends FakePlayer {
    /*
    Highly inspired by https://github.com/SquidDev-CC/plethora/blob/minecraft-1.12/src/main/java/org/squiddev/plethora/gameplay/PlethoraFakePlayer.java
    */
    public static final GameProfile PROFILE = new GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b276"), "[" + AdvancedPeripherals.MOD_ID + "]");
    private static final Predicate<Entity> collidablePredicate = EntitySelector.NO_SPECTATORS;

    private final WeakReference<Entity> owner;

    private BlockPos digPosition;
    private Block digBlock;

    private float currentDamage = 0;

    public APFakePlayer(ServerLevel world, Entity owner, GameProfile profile) {
        super(world, profile != null && profile.isComplete() ? profile : PROFILE);
        if (owner != null) {
            setCustomName(owner.getName());
            this.owner = new WeakReference<>(owner);
        } else {
            this.owner = null;
        }
    }

    @Override
    public void awardStat(@NotNull Stat<?> stat) {
        MinecraftServer server = level.getServer();
        if (server != null && getGameProfile() != PROFILE) {
            Player player = server.getPlayerList().getPlayer(getUUID());
            if (player != null)
                player.awardStat(stat);
        }
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity livingEntity) {
        return true;
    }

    @Override
    public void openTextEdit(@NotNull SignBlockEntity sign) {
    }


    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public void playSound(@NotNull SoundEvent soundIn, float volume, float pitch) {
    }

    private void setState(Block block, BlockPos pos) {

        if (digPosition != null) {
            gameMode.handleBlockBreakAction(digPosition, ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, Direction.EAST, 320, 1);
        }

        digPosition = pos;
        digBlock = block;
        currentDamage = 0;
    }

    @Override
    public float getEyeHeight(@NotNull Pose pose) {
        return 0;
    }

    public static <T> Function<APFakePlayer, T> wrapActionWithRot(float yaw, float pitch, Function<APFakePlayer, T> action) {
        return player -> player.<T>doActionWithRot(yaw, pitch, action);
    }

    public <T> T doActionWithRot(float yaw, float pitch, Function<APFakePlayer, T> action) {
        final float oldRot = this.getYRot();
        this.setRot(oldRot + yaw, pitch);
        try {
            return action.apply(this);
        } finally {
            this.setRot(oldRot, 0);
        }
    }

    public static <T> Function<APFakePlayer, T> wrapActionWithShiftKey(boolean shift, Function<APFakePlayer, T> action) {
        return player -> player.<T>doActionWithShiftKey(shift, action);
    }

    public <T> T doActionWithShiftKey(boolean shift, Function<APFakePlayer, T> action) {
        boolean old = this.isShiftKeyDown();
        this.setShiftKeyDown(shift);
        try {
            return action.apply(this);
        } finally {
            this.setShiftKeyDown(old);
        }
    }

    @Deprecated(forRemoval = true)
    public Pair<Boolean, String> digBlock(Direction direction) {
        return doActionWithRot(direction.toYRot() - this.getYRot(), direction == Direction.DOWN ? 90 : direction == Direction.UP ? -90 : 0, APFakePlayer::digBlock);
    }

    public Pair<Boolean, String> digBlock() {
        Level world = getLevel();
        HitResult hit = findHit(true, false);
        if (hit.getType() == HitResult.Type.MISS) {
            return Pair.of(false, "Nothing to break");
        }
        BlockPos pos = new BlockPos(hit.getLocation());
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        ItemStack tool = getInventory().getSelected();
        if (tool.isEmpty()) {
            return Pair.of(false, "Cannot dig without tool");
        }

        if (block != digBlock || !pos.equals(digPosition)) {
            setState(block, pos);
        }

        Vec3 look = getLookAngle();
        Direction direction = Direction.getNearest(look.x, look.y, look.z).getOpposite();

        if (world.isEmptyBlock(pos) || state.getMaterial().isLiquid()) {
            return Pair.of(false, "Nothing to dig here");
        }

        if (block == Blocks.BEDROCK || state.getDestroySpeed(world, pos) <= -1) {
            return Pair.of(false, "Unbreakable block detected");
        }

        if (!tool.isCorrectToolForDrops(state)) {
            return Pair.of(false, "Tool cannot mine this block");
        }

        ServerPlayerGameMode manager = gameMode;
        float breakSpeed = 0.5f * tool.getDestroySpeed(state) / state.getDestroySpeed(level, pos) - 0.1f;
        for (int i = 0; i < 10; i++) {
            currentDamage += breakSpeed;

            world.destroyBlockProgress(getId(), pos, i);

            if (currentDamage > 9) {
                world.playSound(null, pos, state.getSoundType().getHitSound(), SoundSource.NEUTRAL, .25f, 1);
                manager.handleBlockBreakAction(pos, ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, direction, 320, 1);
                manager.destroyBlock(pos);
                world.destroyBlockProgress(getId(), pos, -1);
                setState(null, null);
                break;
            }
        }

        return Pair.of(true, "block");

    }

    public InteractionResult useOnBlock() {
        return use(true, false);
    }

    public InteractionResult useOnEntity() {
        return use(false, true);
    }

    public InteractionResult useOnFilteredEntity(Predicate<Entity> filter) {
        return use(false, true, filter);
    }

    public InteractionResult useOnSpecificEntity(@NotNull Entity entity, HitResult result) {
        InteractionResult simpleInteraction = interactOn(entity, InteractionHand.MAIN_HAND);
        if (simpleInteraction == InteractionResult.SUCCESS) return simpleInteraction;
        if (ForgeHooks.onInteractEntityAt(this, entity, result.getLocation(), InteractionHand.MAIN_HAND) != null) {
            return InteractionResult.FAIL;
        }

        return entity.interactAt(this, result.getLocation(), InteractionHand.MAIN_HAND);
    }

    public InteractionResult use(boolean skipEntity, boolean skipBlock) {
        return use(skipEntity, skipBlock, null);
    }

    public InteractionResult use(boolean skipEntity, boolean skipBlock, @Nullable Predicate<Entity> entityFilter) {
        HitResult hit = findHit(skipEntity, skipBlock, entityFilter);

        if (hit instanceof BlockHitResult blockHit) {
            ItemStack stack = getMainHandItem();
            BlockPos pos = blockHit.getBlockPos();
            PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(this, InteractionHand.MAIN_HAND, pos, blockHit);
            if (event.isCanceled()) {
                return event.getCancellationResult();
            }
            boolean denied = event.getUseItem() == Event.Result.DENY;
            if (!denied) {
                InteractionResult result = stack.onItemUseFirst(new UseOnContext(level, this, InteractionHand.MAIN_HAND, stack, blockHit));
                if (result != InteractionResult.PASS) {
                    return result;
                }

                boolean bypass = getMainHandItem().doesSneakBypassUse(level, pos, this);
                if (isShiftKeyDown() || bypass || event.getUseBlock() == Event.Result.ALLOW) {
                    InteractionResult useType = gameMode.useItemOn(this, level, stack, InteractionHand.MAIN_HAND, blockHit);
                    if (useType == InteractionResult.SUCCESS) {
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            if (stack.isEmpty() || getCooldowns().isOnCooldown(stack.getItem())) {
                return InteractionResult.PASS;
            }


            if (stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                if (block instanceof CommandBlock || block instanceof StructureBlock) {
                    return InteractionResult.FAIL;
                }
            }

            if (denied) {
                return InteractionResult.PASS;
            }

            ItemStack copyBeforeUse = stack.copy();
            InteractionResult result = stack.useOn(new UseOnContext(level, this, InteractionHand.MAIN_HAND, copyBeforeUse, blockHit));
            if (stack.isEmpty()) {
                ForgeEventFactory.onPlayerDestroyItem(this, copyBeforeUse, InteractionHand.MAIN_HAND);
            }
            return result;
        } else if (hit instanceof EntityHitResult entityHit) {
            return useOnSpecificEntity(entityHit.getEntity(), entityHit);
        }
        return InteractionResult.FAIL;
    }

    public HitResult findHit(boolean skipEntity, boolean skipBlock) {
        return findHit(skipEntity, skipBlock, null);
    }

    @NotNull
    public HitResult findHit(boolean skipEntity, boolean skipBlock, @Nullable Predicate<Entity> entityFilter) {
        AttributeInstance reachAttribute = getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (reachAttribute == null)
            throw new IllegalArgumentException("How did this happened?");

        double range = reachAttribute.getValue();
        Vec3 origin = new Vec3(getX(), getY(), getZ());
        Vec3 look = getLookAngle();
        Vec3 target = new Vec3(origin.x + look.x * range, origin.y + look.y * range, origin.z + look.z * range);
        ClipContext traceContext = new ClipContext(origin, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this);
        Vec3 directionVec = traceContext.getFrom().subtract(traceContext.getTo());
        Direction traceDirection = Direction.getNearest(directionVec.x, directionVec.y, directionVec.z);
        HitResult blockHit;
        if (skipBlock) {
            blockHit = BlockHitResult.miss(traceContext.getTo(), traceDirection, new BlockPos(traceContext.getTo()));
        } else {
            blockHit = BlockGetter.traverseBlocks(traceContext.getFrom(), traceContext.getTo(), traceContext, (rayTraceContext, blockPos) -> {
                if (level.isEmptyBlock(blockPos) || blockPos.equals(blockPosition())) {
                    return null;
                }
                return new BlockHitResult(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), traceDirection, blockPos, false);
            }, rayTraceContext -> BlockHitResult.miss(rayTraceContext.getTo(), traceDirection, new BlockPos(rayTraceContext.getTo())));
        }

        if (skipEntity)
            return blockHit;

        List<Entity> entities = level.getEntities(this, getBoundingBox().expandTowards(look.x * range, look.y * range, look.z * range).inflate(1, 1, 1), collidablePredicate);

        LivingEntity closestEntity = null;
        Vec3 closestVec = null;
        double closestDistance = range;
        for (Entity entityHit : entities) {
            if (!(entityHit instanceof LivingEntity) || entityFilter != null && !entityFilter.test(entityHit))
                continue;
            // Add litter bigger that just pick radius
            AABB box = entityHit.getBoundingBox().inflate(entityHit.getPickRadius() + 0.5);
            Optional<Vec3> clipResult = box.clip(origin, target);

            if (box.contains(origin)) {
                if (closestDistance >= 0.0D) {
                    closestEntity = (LivingEntity) entityHit;
                    closestVec = clipResult.orElse(origin);
                    closestDistance = 0.0D;
                }
            } else if (clipResult.isPresent()) {
                Vec3 clipVec = clipResult.get();
                double distance = origin.distanceTo(clipVec);

                if (distance < closestDistance || closestDistance == 0.0D) {
                    if (entityHit == entityHit.getRootVehicle() && !entityHit.canRiderInteract()) {
                        if (closestDistance == 0.0D) {
                            closestEntity = (LivingEntity) entityHit;
                            closestVec = clipVec;
                        }
                    } else {
                        closestEntity = (LivingEntity) entityHit;
                        closestVec = clipVec;
                        closestDistance = distance;
                    }
                }
            }
        }
        if (closestEntity != null && closestDistance <= range && (blockHit.getType() == HitResult.Type.MISS || distanceToSqr(blockHit.getLocation()) > closestDistance * closestDistance)) {
            return new EntityHitResult(closestEntity, closestVec);
        } else {
            return blockHit;
        }
    }
}
