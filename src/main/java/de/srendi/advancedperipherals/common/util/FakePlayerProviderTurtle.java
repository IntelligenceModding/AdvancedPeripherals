package de.srendi.advancedperipherals.common.util;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.util.DirectionUtil;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.squiddev.plethora.api.IPlayerOwnable;
import org.squiddev.plethora.gameplay.PlethoraFakePlayer;

import java.util.WeakHashMap;

public final class FakePlayerProviderTurtle {
	private static final WeakHashMap<ITurtleAccess, PlethoraFakePlayer> registeredPlayers = new WeakHashMap<>();

	private FakePlayerProviderTurtle() {
	}

	public static PlethoraFakePlayer getPlayer(ITurtleAccess entity, IPlayerOwnable ownable) {
		return getPlayer(entity, ownable == null ? null : ownable.getOwningProfile());
	}

	public static PlethoraFakePlayer getPlayer(ITurtleAccess turtle, GameProfile profile) {
		PlethoraFakePlayer fake = registeredPlayers.get(turtle);
		if (fake == null) {
			fake = new PlethoraFakePlayer((WorldServer) turtle.getWorld(), null, profile);
			registeredPlayers.put(turtle, fake);
		}

		return fake;
	}

	public static void load(PlethoraFakePlayer player, ITurtleAccess turtle, EnumFacing direction) {
		player.setWorld(turtle.getWorld());

		BlockPos position = turtle.getPosition();
		player.setPositionAndRotation(
			position.getX() + 0.5 + 0.51 * direction.getXOffset(),
			position.getY() + 0.5 + 0.51 * direction.getYOffset(),
			position.getZ() + 0.5 + 0.51 * direction.getZOffset(),
			(direction.getAxis() != EnumFacing.Axis.Y ? direction : turtle.getDirection()).getHorizontalAngle(),
			direction.getAxis() != EnumFacing.Axis.Y ? 0 : DirectionUtil.toPitchAngle(direction)
		);

		player.setSize(1, 1);
		player.eyeHeight = 0.0f;
		player.setSneaking(false);

		player.inventory.currentItem = 0;

		// Copy primary items into player inventory and empty the rest
		IItemHandler turtleInventory = turtle.getItemHandler();
		int size = turtleInventory.getSlots();
		int largerSize = player.inventory.getSizeInventory();
		player.inventory.currentItem = turtle.getSelectedSlot();
		for (int i = 0; i < size; i++) {
			player.inventory.setInventorySlotContents(i, turtleInventory.getStackInSlot(i));
		}
		for (int i = size; i < largerSize; i++) {
			player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
		}

		player.inventory.markDirty();

		// Add properties
		ItemStack activeStack = player.getHeldItem(EnumHand.MAIN_HAND);
		if (!activeStack.isEmpty()) {
			player.getAttributeMap().applyAttributeModifiers(activeStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
		}
	}

	public static void unload(PlethoraFakePlayer player, ITurtleAccess turtle) {
		player.inventory.currentItem = 0;
		player.setSize(0, 0);
		player.eyeHeight = player.getDefaultEyeHeight();

		// Remove properties
		ItemStack activeStack = player.getHeldItem(EnumHand.MAIN_HAND);
		if (!activeStack.isEmpty()) {
			player.getAttributeMap().removeAttributeModifiers(activeStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
		}

		// Copy primary items into turtle inventory and then insert/drop the rest
		IItemHandlerModifiable turtleInventory = turtle.getItemHandler();
		int size = turtleInventory.getSlots();
		int largerSize = player.inventory.getSizeInventory();
		player.inventory.currentItem = turtle.getSelectedSlot();
		for (int i = 0; i < size; i++) {
			turtleInventory.setStackInSlot(i, player.inventory.getStackInSlot(i));
			player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
		}

		for (int i = size; i < largerSize; i++) {
			ItemStack remaining = player.inventory.getStackInSlot(i);
			if (!remaining.isEmpty()) {
				remaining = ItemHandlerHelper.insertItem(turtleInventory, remaining, false);
				if (!remaining.isEmpty()) {
					BlockPos position = turtle.getPosition();
					WorldUtil.dropItemStack(remaining, turtle.getWorld(), position, turtle.getDirection().getOpposite());
				}
			}

			player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
		}

		player.inventory.markDirty();
	}


}
