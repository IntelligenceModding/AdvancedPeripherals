package de.srendi.advancedperipherals.common.util.fakeplayer;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.HitResultUtil;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

public class APFakePlayer extends FakePlayer {
    /*
    Highly inspired by https://github.com/SquidDev-CC/plethora/blob/minecraft-1.12/src/main/java/org/squiddev/plethora/gameplay/PlethoraFakePlayer.java
    */
    public static final GameProfile PROFILE = new GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b276"), "[" + AdvancedPeripherals.MOD_ID + "]");
    private static final Predicate<Entity> DEFAULT_ENTITY_FILTER = EntitySelector.NO_SPECTATORS.and(LivingEntity.class::isInstance).and((entity) -> !entity.isPassenger());

    private BlockPos source = null;
    private BlockPos digPosition = null;
    private Block digBlock = null;
    private float currentDamage = 0;
    private double reachRange = -1;

    public APFakePlayer(ServerLevel world, Entity owner, GameProfile profile) {
        super(world, profile != null && profile.isComplete() ? profile : PROFILE);
        if (owner != null) {
            setCustomName(owner.getName());
        }
    }

    public void setSourceBlock(BlockPos pos) {
        this.source = pos;
    }

    @Override
    public void awardStat(@NotNull Stat<?> stat) {
        MinecraftServer server = level.getServer();
        if (server != null && getGameProfile() != PROFILE) {
            Player player = server.getPlayerList().getPlayer(getUUID());
            if (player != null) {
                player.awardStat(stat);
            }
        }
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

    public static <T> Action<T> wrapActionWithRot(float yaw, float pitch, Action<T> action) {
        return player -> player.<T>doActionWithRot(yaw, pitch, action);
    }

    public <T> T doActionWithRot(float yaw, float pitch, Action<T> action) {
        final float yRot = this.getYRot(), xRot = this.getXRot();
        this.setRot(yRot + yaw, xRot + pitch);
        try {
            return action.apply(this);
        } finally {
            this.setRot(yRot, xRot);
        }
    }

    public static <T> Action<T> wrapActionWithShiftKey(boolean shift, Action<T> action) {
        return player -> player.<T>doActionWithShiftKey(shift, action);
    }

    public <T> T doActionWithShiftKey(boolean shift, Action<T> action) {
        boolean old = this.isShiftKeyDown();
        this.setShiftKeyDown(shift);
        try {
            return action.apply(this);
        } finally {
            this.setShiftKeyDown(old);
        }
    }

    public static <T> Action<T> wrapActionWithReachRange(double range, Action<T> action) {
        return player -> player.<T>doActionWithReachRange(range, action);
    }

    public <T> T doActionWithReachRange(double range, Action<T> action) {
        this.reachRange = range;
        try {
            return action.apply(this);
        } finally {
            this.reachRange = -1;
        }
    }

    public double getReachRange() {
        AttributeInstance reachAttribute = this.getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (reachAttribute == null) {
            throw new IllegalArgumentException("How did this happened?");
        }
        double range = reachAttribute.getValue();
        if (this.reachRange >= 0 && this.reachRange < range) {
            range = this.reachRange;
        }
        return range;
    }

    public Pair<Boolean, String> digBlock() {
        Level world = getLevel();
        HitResult hit = findHit(true, false);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() == HitResult.Type.MISS) {
            return Pair.of(false, "Nothing to break");
        }
        BlockPos pos = blockHit.getBlockPos();
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
            boolean usedItem = event.getUseItem() != Event.Result.DENY;
            boolean usedOnBlock = event.getUseBlock() != Event.Result.DENY;
            if (usedItem) {
                InteractionResult result = stack.onItemUseFirst(new UseOnContext(level, this, InteractionHand.MAIN_HAND, stack, blockHit));
                if (result != InteractionResult.PASS) {
                    return result;
                }

                boolean bypass = getMainHandItem().doesSneakBypassUse(level, pos, this);
                if (isShiftKeyDown() || bypass || usedOnBlock) {
                    InteractionResult useType = gameMode.useItemOn(this, level, stack, InteractionHand.MAIN_HAND, blockHit);
                    if (useType.consumesAction()) {
                        return useType;
                    }
                }
            }

            if (!stack.isEmpty() && getCooldowns().isOnCooldown(stack.getItem())) {
                return InteractionResult.PASS;
            }

            if (stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                if (block instanceof CommandBlock || block instanceof StructureBlock) {
                    return InteractionResult.FAIL;
                }
            }

            if (!usedItem && !usedOnBlock) {
                return InteractionResult.PASS;
            }

            ItemStack copyBeforeUse = stack.copy();
            InteractionResult result = stack.useOn(new UseOnContext(level, this, InteractionHand.MAIN_HAND, stack, blockHit));
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
        return findHit(skipEntity, skipBlock, DEFAULT_ENTITY_FILTER);
    }

    @NotNull
    public HitResult findHit(boolean skipEntity, boolean skipBlock, @NotNull Predicate<Entity> entityFilter) {
        double range = this.getReachRange();
        Vec3 origin = new Vec3(this.getX(), this.getY(), this.getZ());
        Vec3 look = this.getLookAngle();
        Vec3 target = new Vec3(origin.x + look.x * range, origin.y + look.y * range, origin.z + look.z * range);

        BlockHitResult blockHit;
        if (skipBlock) {
            Direction traceDirection = Direction.getNearest(look.x, look.y, look.z);
            blockHit = BlockHitResult.miss(target, traceDirection, new BlockPos(target));
        } else {
            blockHit = HitResultUtil.getBlockHitResult(origin, target, level, ClipContext.Block.OUTLINE, this.source);
        }

        if (skipEntity) {
            return blockHit;
        }

        // TODO: maybe let entityFilter returns the priority of the entity, instead of only returns the closest one.
        EntityHitResult entityHit = HitResultUtil.getEntityHitResult(origin, target, level, this, entityFilter);
        if (entityHit.getType() == HitResult.Type.ENTITY) {
            return entityHit;
        }

        return blockHit;
    }

    @FunctionalInterface
    public interface Action<T> {
        T apply(APFakePlayer player);
    }
}
