package de.srendi.advancedperipherals.common.util.fakeplayer;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.WeakHashMap;

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
        player.setLevel(turtle.getLevel());
        // Player position
        BlockPos position = turtle.getPosition();
        player.moveTo(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, direction.toYRot(), 0);
        // Player inventory
        Inventory playerInventory = player.getInventory();
        playerInventory.selected = 0;
        playerInventory.clearContent();

        // Copy primary items into player inventory and empty the rest
        Container turtleInventory = turtle.getInventory();
        int size = turtleInventory.getContainerSize();
        int selectedSlot = turtle.getSelectedSlot();

        playerInventory.setItem(0, turtleInventory.getItem(selectedSlot));
        int i = 0;
        for (; i < size; i++) {
            playerInventory.setItem(i + 1, i == selectedSlot ? ItemStack.EMPTY : turtleInventory.getItem(i));
        }

        // Add properties
        ItemStack activeStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!activeStack.isEmpty()) {
            activeStack.getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (holder, mod) -> {
                AttributeInstance attr = player.getAttribute(holder);
                if (attr != null) {
                    attr.addOrUpdateTransientModifier(mod);
                }
            });
        }
    }

    public static void unload(APFakePlayer player, ITurtleAccess turtle) {
        Inventory playerInventory = player.getInventory();

        // Remove properties
        ItemStack activeStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!activeStack.isEmpty()) {
            activeStack.getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (holder, mod) -> {
                AttributeInstance attr = player.getAttribute(holder);
                if (attr != null) {
                    attr.removeModifier(mod);
                }
            });
        }

        // Copy primary items into turtle inventory and then insert/drop the rest
        Container turtleInventory = turtle.getInventory();
        int size = turtleInventory.getContainerSize();
        int largerSize = playerInventory.getContainerSize();
        int selectedSlot = turtle.getSelectedSlot();

        turtleInventory.setItem(selectedSlot, playerInventory.getItem(0));
        playerInventory.setItem(0, ItemStack.EMPTY);
        int i = 0;
        for (; i < size; i++) {
            if (i != selectedSlot) {
                turtleInventory.setItem(i, playerInventory.getItem(i + 1));
            }
            playerInventory.setItem(i + 1, ItemStack.EMPTY);
        }

        for (; i < largerSize; i++) {
            ItemStack remaining = playerInventory.getItem(i + 1);
            if (!remaining.isEmpty()) {
                remaining = InventoryUtil.storeItemsFromOffset(turtleInventory, remaining, 0);
                if (!remaining.isEmpty()) {
                    BlockPos position = turtle.getPosition();
                    WorldUtil.dropItemStack(turtle.getLevel(), position, turtle.getDirection().getOpposite(), remaining);
                }
            }
            playerInventory.setItem(i + 1, ItemStack.EMPTY);
        }
    }

    public static <T> T withPlayer(ITurtleAccess turtle, APFakePlayer.Action<T> action) throws LuaException {
        APFakePlayer player = getPlayer(turtle, turtle.getOwningPlayer());
        load(player, turtle);
        try {
            return action.apply(player);
        } finally {
            unload(player, turtle);
        }
    }
}
