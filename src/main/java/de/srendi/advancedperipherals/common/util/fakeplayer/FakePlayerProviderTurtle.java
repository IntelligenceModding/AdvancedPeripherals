package de.srendi.advancedperipherals.common.util.fakeplayer;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.WeakHashMap;
import java.util.function.Function;

public final class FakePlayerProviderTurtle {

    /*
    Highly inspired by https://github.com/SquidDev-CC/plethora/blob/minecraft-1.12/src/main/java/org/squiddev/plethora/integration/computercraft/FakePlayerProviderTurtle.java
    */
    private static final WeakHashMap<ITurtleAccess, APFakePlayer> registeredPlayers = new WeakHashMap<>();

    private FakePlayerProviderTurtle() {
    }

    public static APFakePlayer getPlayer(ITurtleAccess turtle, GameProfile profile) {
        return registeredPlayers.computeIfAbsent(turtle, iTurtleAccess -> new APFakePlayer((ServerLevel) turtle.getLevel(), null, profile));
    }

    public static void load(APFakePlayer player, ITurtleAccess turtle) {
        Direction direction = turtle.getDirection();
        player.setLevel((ServerLevel) turtle.getLevel());
        // Player position
        BlockPos position = turtle.getPosition();
        player.moveTo(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, direction.toYRot(), 0);
        // Player inventory
        Inventory playerInventory = player.getInventory();
        playerInventory.selected = 0;

        // Copy primary items into player inventory and empty the rest
        Container turtleInventory = turtle.getInventory();
        int size = turtleInventory.getContainerSize();
        int largerSize = playerInventory.getContainerSize();
        playerInventory.selected = turtle.getSelectedSlot();
        for (int i = 0; i < size; i++) {
            playerInventory.setItem(i, turtleInventory.getItem(i));
        }
        for (int i = size; i < largerSize; i++) {
            playerInventory.setItem(i, ItemStack.EMPTY);
        }

        // Add properties
        ItemStack activeStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!activeStack.isEmpty()) {
            player.getAttributes().addTransientAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND));
        }
    }

    public static void unload(APFakePlayer player, ITurtleAccess turtle) {
        Inventory playerInventory = player.getInventory();
        playerInventory.selected = 0;

        // Remove properties
        ItemStack activeStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!activeStack.isEmpty()) {
            player.getAttributes().removeAttributeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND));
        }

        // Copy primary items into turtle inventory and then insert/drop the rest
        Container turtleInventory = turtle.getInventory();
        int size = turtleInventory.getContainerSize();
        int largerSize = playerInventory.getContainerSize();
        playerInventory.selected = turtle.getSelectedSlot();
        for (int i = 0; i < size; i++) {
            turtleInventory.setItem(i, playerInventory.getItem(i));
            playerInventory.setItem(i, ItemStack.EMPTY);
        }

        for (int i = size; i < largerSize; i++) {
            ItemStack remaining = playerInventory.getItem(i);
            if (!remaining.isEmpty()) {
                remaining = InventoryUtil.storeItemsFromOffset(turtleInventory, remaining, 0);
                if (!remaining.isEmpty()) {
                    BlockPos position = turtle.getPosition();
                    WorldUtil.dropItemStack(turtle.getLevel(), position, turtle.getDirection().getOpposite(), remaining);
                }
            }

            playerInventory.setItem(i, ItemStack.EMPTY);
        }
    }


    public static <T> T withPlayer(ITurtleAccess turtle, Function<APFakePlayer, T> function) {
        APFakePlayer player = getPlayer(turtle, turtle.getOwningPlayer());
        load(player, turtle);
        T result = function.apply(player);
        unload(player, turtle);
        return result;
    }

}
