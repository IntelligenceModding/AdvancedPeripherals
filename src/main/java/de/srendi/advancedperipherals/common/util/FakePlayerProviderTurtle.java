package de.srendi.advancedperipherals.common.util;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.entity.Pose;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.WeakHashMap;
import java.util.function.Function;

public final class FakePlayerProviderTurtle {
	/*
	Highly inspired by https://github.com/SquidDev-CC/plethora/blob/minecraft-1.12/src/main/java/org/squiddev/plethora/integration/computercraft/FakePlayerProviderTurtle.java
	*/
	private static final WeakHashMap<ITurtleAccess, TurtleFakePlayer> registeredPlayers = new WeakHashMap<>();

	private FakePlayerProviderTurtle() {
	}

	public static TurtleFakePlayer getPlayer(ITurtleAccess turtle, GameProfile profile) {
		TurtleFakePlayer fake = registeredPlayers.get(turtle);
		if (fake == null) {
			fake = new TurtleFakePlayer((ServerWorld) turtle.getWorld(), null, profile);
			registeredPlayers.put(turtle, fake);
		}

		return fake;
	}

	public static void load(TurtleFakePlayer player, ITurtleAccess turtle) {
		Direction direction = turtle.getDirection();
		player.setLevel(turtle.getWorld());
		BlockPos position = turtle.getPosition();
		// Player position
		float pitch = direction == Direction.UP ? -90 : direction == Direction.DOWN ? 90 : 0;
		float yaw = direction == Direction.SOUTH ? 0 : direction == Direction.WEST ? 90 : direction == Direction.NORTH ? 180 : -90;
		Vector3i sideVec = direction.getNormal();
		Direction.Axis a = direction.getAxis();
		Direction.AxisDirection ad = direction.getAxisDirection();
		double x = a == Direction.Axis.X && ad == Direction.AxisDirection.NEGATIVE ? -.5 : .5 + sideVec.getX() / 1.9D;
		double y = 0.5 + sideVec.getY() / 1.9D;
		double z = a == Direction.Axis.Z && ad == Direction.AxisDirection.NEGATIVE ? -.5 : .5 + sideVec.getZ() / 1.9D;
		player.moveTo(position.getX() + x, position.getY() + y, position.getZ() + z, yaw, pitch);
		// Player inventory
		player.inventory.selected = 0;

		// Copy primary items into player inventory and empty the rest
		IItemHandler turtleInventory = turtle.getItemHandler();
		int size = turtleInventory.getSlots();
		int largerSize = player.inventory.getContainerSize();
		player.inventory.selected = turtle.getSelectedSlot();
		for (int i = 0; i < size; i++) {
			player.inventory.setItem(i, turtleInventory.getStackInSlot(i));
		}
		for (int i = size; i < largerSize; i++) {
			player.inventory.setItem(i, ItemStack.EMPTY);
		}

		// Add properties
		ItemStack activeStack = player.getItemInHand(Hand.MAIN_HAND);
		if (!activeStack.isEmpty()) {
			player.getAttributes().addTransientAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlotType.MAINHAND));
		}
	}

	public static void unload(TurtleFakePlayer player, ITurtleAccess turtle) {
		player.inventory.selected = 0;

		// Remove properties
		ItemStack activeStack = player.getItemInHand(Hand.MAIN_HAND);
		if (!activeStack.isEmpty()) {
			player.getAttributes().removeAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlotType.MAINHAND));
		}

		// Copy primary items into turtle inventory and then insert/drop the rest
		IItemHandlerModifiable turtleInventory = turtle.getItemHandler();
		int size = turtleInventory.getSlots();
		int largerSize = player.inventory.getContainerSize();
		player.inventory.selected = turtle.getSelectedSlot();
		for (int i = 0; i < size; i++) {
			turtleInventory.setStackInSlot(i, player.inventory.getItem(i));
			player.inventory.setItem(i, ItemStack.EMPTY);
		}

		for (int i = size; i < largerSize; i++) {
			ItemStack remaining = player.inventory.getItem(i);
			if (!remaining.isEmpty()) {
				remaining = ItemHandlerHelper.insertItem(turtleInventory, remaining, false);
				if (!remaining.isEmpty()) {
					BlockPos position = turtle.getPosition();
					WorldUtil.dropItemStack(remaining, turtle.getWorld(), position, turtle.getDirection().getOpposite());
				}
			}

			player.inventory.setItem(i, ItemStack.EMPTY);
		}
	}

	public static <T> T withPlayer(ITurtleAccess turtle, Function<TurtleFakePlayer, T> function) {
		TurtleFakePlayer player = getPlayer(turtle, turtle.getOwningPlayer());
		load(player, turtle);
		T result = function.apply(player);
		unload(player, turtle);
		return result;
	}

}
