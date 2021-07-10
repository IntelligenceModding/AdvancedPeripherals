package de.srendi.advancedperipherals.common.util.fakeplayer;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.Stat;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    private static final Predicate<Entity> collidablePredicate = EntityPredicates.NO_SPECTATORS;

    private final WeakReference<Entity> owner;

    private BlockPos digPosition;
    private Block digBlock;

    private float currentDamage = 0;

    public APFakePlayer(ServerWorld world, Entity owner, GameProfile profile) {
        super(world, profile != null && profile.isComplete() ? profile : PROFILE);
        connection = new FakeNetHandler(this);
        if (owner != null) {
            setCustomName(owner.getName());
            this.owner = new WeakReference<>(owner);
        } else {
            this.owner = null;
        }
    }

    @Deprecated
    public APFakePlayer(World world) {
        super((ServerWorld) world, PROFILE);
        owner = null;
    }

    @Override
    public void awardStat(@NotNull Stat<?> stat) {
        MinecraftServer server = level.getServer();
        if (server != null && getGameProfile() != PROFILE) {
            PlayerEntity player = server.getPlayerList().getPlayer(getUUID());
            if (player != null) player.awardStat(stat);
        }
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity p_213336_1_) {
        return true;
    }

    @Override
    public void openTextEdit(@NotNull SignTileEntity p_175141_1_) {
    }


    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public void playSound(@Nonnull SoundEvent soundIn, float volume, float pitch) {
    }

    private void setState(Block block, BlockPos pos) {

        if (digPosition != null) {
            gameMode.handleBlockBreakAction(digPosition, CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, Direction.EAST, 1);
        }

        digPosition = pos;
        digBlock = block;
        currentDamage = 0;
    }

    @Override
    public float getEyeHeight(@NotNull Pose pose) {
        return 0;
    }

    public Pair<Boolean, String> digBlock(Direction direction) {
        World world = getLevel();
        RayTraceResult hit = findHit(true, false);
        if (hit.getType() == RayTraceResult.Type.MISS) {
            return Pair.of(false, "Nothing to break");
        }
        BlockPos pos = new BlockPos(hit.getLocation().x, hit.getLocation().y, hit.getLocation().z);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        ItemStack tool = inventory.getSelected();

        if (tool.isEmpty()) {
            return Pair.of(false, "Cannot dig without tool");
        }

        if (block != digBlock || !pos.equals(digPosition)) setState(block, pos);

        if (!world.isEmptyBlock(pos) && !state.getMaterial().isLiquid()) {
            if (block == Blocks.BEDROCK || state.getDestroySpeed(world, pos) <= -1) {
                return Pair.of(false, "Unbreakable block detected");
            }

            if (tool.getHarvestLevel(ToolType.PICKAXE, this, state) < state.getHarvestLevel()) {
                return Pair.of(false, "Tool are too cheap for this block");
            }

            PlayerInteractionManager manager = gameMode;
            float breakSpeed = 0.5f * tool.getDestroySpeed(state) / state.getDestroySpeed(level, pos) - 0.1f;
            for (int i = 0; i < 10; i++) {
                currentDamage += breakSpeed;

                world.destroyBlockProgress(getId(), pos, i);

                if (currentDamage > 9) {
                    world.playSound(null, pos, state.getSoundType().getHitSound(), SoundCategory.NEUTRAL, .25f, 1);
                    manager.handleBlockBreakAction(pos, CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, direction.getOpposite(), 1);
                    manager.destroyBlock(pos);
                    world.destroyBlockProgress(getId(), pos, -1);
                    setState(null, null);
                    break;
                }
            }

            return Pair.of(true, "block");
        }

        return Pair.of(false, "Nothing to dig here");
    }

    public ActionResultType useOnBlock() {
        return use(true, false);
    }

    public ActionResultType useOnEntity() {
        return use(false, true);
    }

    public ActionResultType useOnFilteredEntity(Predicate<Entity> filter) {
        return use(false, true, filter);
    }

    public ActionResultType useOnSpecificEntity(@NotNull Entity entity, RayTraceResult result){
        ActionResultType simpleInteraction = interactOn(entity, Hand.MAIN_HAND);
        if (simpleInteraction == ActionResultType.SUCCESS)
            return simpleInteraction;
        if (ForgeHooks.onInteractEntityAt(this, entity, result.getLocation(), Hand.MAIN_HAND) != null) {
            return ActionResultType.FAIL;
        }
        return entity.interactAt(this, result.getLocation(), Hand.MAIN_HAND);
    }
    public ActionResultType use(boolean skipEntity, boolean skipBlock) {
        return use(skipEntity, skipBlock, null);
    }

    public ActionResultType use(boolean skipEntity, boolean skipBlock, @Nullable Predicate<Entity> entityFilter) {
        RayTraceResult hit = findHit(skipEntity, skipBlock, entityFilter);

        if (hit instanceof BlockRayTraceResult) {

            ItemStack stack = getMainHandItem();
            BlockRayTraceResult blockHit = (BlockRayTraceResult) hit;
            BlockPos pos = ((BlockRayTraceResult) hit).getBlockPos();
            PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(this, Hand.MAIN_HAND, pos, blockHit);
            if (event.isCanceled()) return event.getCancellationResult();

            if (event.getUseItem() != Event.Result.DENY) {
                ActionResultType result = stack.onItemUseFirst(new ItemUseContext(level, this, Hand.MAIN_HAND, stack, blockHit));
                if (result != ActionResultType.PASS) return result;
            }

            boolean bypass = getMainHandItem().doesSneakBypassUse(level, pos, this) && getMainHandItem().doesSneakBypassUse(level, pos, this);
            if (getPose() != Pose.CROUCHING || bypass || event.getUseBlock() == Event.Result.ALLOW) {
                ActionResultType useType = gameMode.useItemOn(this, level, stack, Hand.MAIN_HAND, blockHit);
                if (event.getUseBlock() != Event.Result.DENY && useType == ActionResultType.SUCCESS) {
                    return ActionResultType.SUCCESS;
                }
            }

            if (stack.isEmpty() || getCooldowns().isOnCooldown(stack.getItem())) return ActionResultType.PASS;


            if (stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (block instanceof CommandBlockBlock || block instanceof StructureBlock) return ActionResultType.FAIL;
            }

            if (event.getUseItem() == Event.Result.DENY) return ActionResultType.PASS;

            ItemStack copyBeforeUse = stack.copy();
            ActionResultType result = stack.useOn(new ItemUseContext(level, this, Hand.MAIN_HAND, copyBeforeUse, blockHit));
            if (stack.isEmpty()) ForgeEventFactory.onPlayerDestroyItem(this, copyBeforeUse, Hand.MAIN_HAND);
            return result;
        } else if (hit instanceof EntityRayTraceResult) {
            EntityRayTraceResult entityHit = (EntityRayTraceResult) hit;
            return useOnSpecificEntity(entityHit.getEntity(), entityHit);
        }
        return ActionResultType.FAIL;
    }

    public RayTraceResult findHit(boolean skipEntity, boolean skipBlock) {
        return findHit(skipEntity, skipBlock, null);
    }

    @Nonnull
    public RayTraceResult findHit(boolean skipEntity, boolean skipBlock, @Nullable Predicate<Entity> entityFilter) {
        ModifiableAttributeInstance reachAttribute = getAttribute(ForgeMod.REACH_DISTANCE.get());
        if (reachAttribute == null) {
            throw new IllegalArgumentException("How did this happened?");
        }
        double range = reachAttribute.getValue();
        Vector3d origin = new Vector3d(getX(), getY(), getZ());
        Vector3d look = getLookAngle();
        Vector3d target = new Vector3d(origin.x + look.x * range, origin.y + look.y * range, origin.z + look.z * range);
        RayTraceContext traceContext = new RayTraceContext(origin, target, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, this);
        Vector3d directionVec = traceContext.getFrom().subtract(traceContext.getTo());
        Direction traceDirection = Direction.getNearest(directionVec.x, directionVec.y, directionVec.z);
        RayTraceResult blockHit;
        if (skipBlock) {
            blockHit = BlockRayTraceResult.miss(traceContext.getTo(), traceDirection , new BlockPos(traceContext.getTo()));
        } else {
            blockHit = IBlockReader.traverseBlocks(traceContext, (rayTraceContext, blockPos) -> {
                if (level.isEmptyBlock(blockPos)) {
                    return null;
                }
                return new BlockRayTraceResult(
                        new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), traceDirection,
                        blockPos, false
                );
            }, (rayTraceContext) -> BlockRayTraceResult.miss(rayTraceContext.getTo(), traceDirection, new BlockPos(rayTraceContext.getTo())));
        }

        if (skipEntity) {
            return blockHit;
        }

        List<Entity> entities = level.getEntities(
                this, getBoundingBox().expandTowards(look.x * range, look.y * range, look.z * range).inflate(1, 1, 1),
                collidablePredicate
        );

        LivingEntity closestEntity = null;
        Vector3d closestVec = null;
        double closestDistance = range;
        for (Entity entityHit : entities) {
            if (!(entityHit instanceof LivingEntity))
                continue;
            if (entityFilter != null && !entityFilter.test(entityHit))
                continue;
            // Add litter bigger that just pick radius
            AxisAlignedBB box = entityHit.getBoundingBox().inflate(entityHit.getPickRadius() + 0.5);
            Optional<Vector3d> clipResult = box.clip(origin, target);

            if (box.contains(origin)) {
                if (closestDistance >= 0.0D) {
                    closestEntity = (LivingEntity) entityHit;
                    closestVec = clipResult.orElse(origin);
                    closestDistance = 0.0D;
                }
            } else if (clipResult.isPresent()) {
                Vector3d clipVec = clipResult.get();
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
        if (closestEntity != null && closestDistance <= range && (blockHit.getType() == RayTraceResult.Type.MISS || distanceToSqr(blockHit.getLocation()) > closestDistance * closestDistance)) {
            return new EntityRayTraceResult(closestEntity, closestVec);
        } else {
            return blockHit;
        }
    }
}
