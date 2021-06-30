package de.srendi.advancedperipherals.common.util;

import com.mojang.authlib.GameProfile;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.commons.lang3.tuple.Pair;
import org.squiddev.plethora.api.Constants;
import org.squiddev.plethora.utils.FakeNetHandler;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class PlethoraFakePlayer extends FakePlayer {
	public static final GameProfile PROFILE = new GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b276"), "[" + AdvancedPeripherals.MOD_ID + "]");

	private final WeakReference<Entity> owner;

	private BlockPos digPosition;
	private Block digBlock;

	private int currentDamage = -1;
	private int currentDamageState = -1;

	public PlethoraFakePlayer(ServerWorld world, Entity owner, GameProfile profile) {
		super(world, profile != null && profile.isComplete() ? profile : PROFILE);
		setSize(0, 0);
		if (owner != null) {
			setCustomNameTag(owner.getName());
			this.owner = new WeakReference<>(owner);
		} else {
			this.owner = null;
		}
	}

	@Deprecated
	public PlethoraFakePlayer(World world) {
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

	//region Dig
	private void setState(Block block, BlockPos pos) {
		interactionManager.cancelDestroyingBlock();
		interactionManager.durabilityRemainingOnBlock = -1;

		digPosition = pos;
		digBlock = block;
		currentDamage = -1;
		currentDamageState = -1;
	}

	public Pair<Boolean, String> dig(BlockPos pos, Direction direction) {
		World world = getLevel();
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (block != digBlock || !pos.equals(digPosition)) setState(block, pos);

		if (!world.isEmptyBlock(pos) && !state.getMaterial().isLiquid()) {
			if (block == Blocks.BEDROCK || state.getHarvestLevel() <= -1) {
				return Pair.of(false, "Unbreakable block detected");
			}

			PlayerInteractionManager manager = interactionManager;
			for (int i = 0; i < 10; i++) {
				if (currentDamageState == -1) {
					manager.
					manager.onBlockClicked(pos, direction.getOpposite());
					currentDamageState = manager.durabilityRemainingOnBlock;
				} else {
					currentDamage++;
					state.getHarde
					float hardness = state.getPlayerRelativeBlockHardness(this, world, pos) * (currentDamage + 1);
					int hardnessState = (int) (hardness * 10);

					if (hardnessState != currentDamageState) {
						world.sendBlockBreakProgress(getEntityId(), pos, hardnessState);
						currentDamageState = hardnessState;
					}

					if (hardness >= 1) {
						manager.handleBlockBreakAction(pos, CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, direction.getOpposite(), 1);
						manager.tryHarvestBlock(pos);

						setState(null, null);
						break;
					}
				}
			}

			return Pair.of(true, "block");
		}

		return Pair.of(false, "Nothing to dig here");
	}
	//endregion
}
