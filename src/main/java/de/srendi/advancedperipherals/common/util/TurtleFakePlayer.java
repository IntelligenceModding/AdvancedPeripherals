package de.srendi.advancedperipherals.common.util;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.Stat;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class TurtleFakePlayer extends FakePlayer {
	/*
	Highly inspired by https://github.com/SquidDev-CC/plethora/blob/minecraft-1.12/src/main/java/org/squiddev/plethora/gameplay/PlethoraFakePlayer.java
	*/
	public static final GameProfile PROFILE = new GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b276"), "[" + AdvancedPeripherals.MOD_ID + "]");

	private final WeakReference<Entity> owner;

	private BlockPos digPosition;
	private Block digBlock;

	private float currentDamage = 0;

	public TurtleFakePlayer(ServerWorld world, Entity owner, GameProfile profile) {
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
	public TurtleFakePlayer(World world) {
		super((ServerWorld) world, PROFILE);
		owner = null;
	}
	public Entity getOwner() {
		return owner == null ? null : owner.get();
	}

	@Override
	public void awardStat(Stat<?> stat) {
		MinecraftServer server = level.getServer();
		if (server != null && getGameProfile() != PROFILE) {
			PlayerEntity player = server.getPlayerList().getPlayer(getUUID());
			if (player != null) player.awardStat(stat);
		}
	}

	@Override
	public boolean canAttack(LivingEntity p_213336_1_) {
		return true;
	}

	@Override
	public void openTextEdit(SignTileEntity p_175141_1_) {}


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

	public Pair<Boolean, String> dig(BlockPos pos, Direction direction) {
		World world = getLevel();
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
	//endregion
}
