package de.srendi.advancedperipherals.network;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.network.messages.ClearHudCanvasMessage;
import de.srendi.advancedperipherals.network.messages.RequestHudCanvasMessage;
import de.srendi.advancedperipherals.network.messages.UpdateHudCanvasMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MNetwork {
	private static int id = 0;
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(AdvancedPeripherals.MOD_ID, "main_channel"), () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void init() {
		NETWORK_CHANNEL.registerMessage(++id, ClearHudCanvasMessage.class, ClearHudCanvasMessage::encode,
				ClearHudCanvasMessage::decode, ClearHudCanvasMessage::handle);
		NETWORK_CHANNEL.registerMessage(++id, RequestHudCanvasMessage.class, RequestHudCanvasMessage::encode,
				RequestHudCanvasMessage::decode, RequestHudCanvasMessage::handle);
		NETWORK_CHANNEL.registerMessage(++id, UpdateHudCanvasMessage.class, UpdateHudCanvasMessage::encode,
				UpdateHudCanvasMessage::decode, UpdateHudCanvasMessage::handle);
	}

	/**
	 * Sends a packet to the server.<br>
	 * Must be called Client side.
	 */
	public static void sendToServer(Object msg) {
		NETWORK_CHANNEL.sendToServer(msg);
	}

	/**
	 * Send a packet to a specific player.<br>
	 * Must be called Server side.
	 */
	public static void sendTo(Object msg, ServerPlayerEntity player) {
		if (!(player instanceof FakePlayer)) {
			NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
		}
	}

	public static void sendPacketToAll(Object packet) {
		NETWORK_CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
	}

	public static SUpdateTileEntityPacket createTEUpdatePacket(TileEntity tile) {
		return new SUpdateTileEntityPacket(tile.getPos(), -1, tile.getUpdateTag());
	}

	public static void sendToAllAround(Object mes, RegistryKey<World> dim, BlockPos pos, int radius) {
		NETWORK_CHANNEL.send(PacketDistributor.NEAR
				.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), radius, dim)), mes);
	}

	public static void sendToAllInWorld(Object mes, ServerWorld world) {
		NETWORK_CHANNEL.send(PacketDistributor.DIMENSION.with(world::getDimensionKey), mes);
	}
}
