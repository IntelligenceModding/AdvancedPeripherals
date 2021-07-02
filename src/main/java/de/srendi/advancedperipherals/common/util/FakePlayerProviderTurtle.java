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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.WeakHashMap;

public final class FakePlayerProviderTurtle {
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

	public static void load(TurtleFakePlayer player, ITurtleAccess turtle, Direction direction) {
		player.setLevel(turtle.getWorld());

		BlockPos position = turtle.getPosition();
		player.setPos(
			position.getX() + 0.5 + 0.51 * direction.getStepX(),
			position.getY() + 0.5 + 0.51 * direction.getStepY(),
			position.getZ() + 0.5 + 0.51 * direction.getStepZ()
		);
		player.setYBodyRot(direction.toYRot());
		player.setYHeadRot(direction.toYRot());
		player.setPose(Pose.CROUCHING);

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


}
