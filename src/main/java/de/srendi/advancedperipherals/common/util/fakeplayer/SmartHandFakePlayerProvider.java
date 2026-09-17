package de.srendi.advancedperipherals.common.util.fakeplayer;

import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.common.items.SmartChestMountItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.WeakHashMap;

public class SmartHandFakePlayerProvider {
    private static final WeakHashMap<LivingEntity, APFakePlayer> PLAYERS = new WeakHashMap<>();

    private SmartHandFakePlayerProvider() {}

    public static APFakePlayer getOrCreateFakePlayer(LivingEntity entity) {
        return PLAYERS.computeIfAbsent(
            entity,
            (e) -> new APFakePlayer(
                (ServerLevel) e.level(),
                e,
                e instanceof ServerPlayer player ? player.getGameProfile() : APFakePlayer.PROFILE
            )
        );
    }

    public static <T> T doAction(LivingEntity entity, int index, Vec3 position, ItemStack chestStack, APFakePlayer.Action<T> action) throws LuaException {
        IItemHandlerModifiable chestItemHandler = ((SmartChestMountItem) chestStack.getItem()).createItemHandlerCap(chestStack);
        APFakePlayer player = getOrCreateFakePlayer(entity);
        player.moveTo(position);
        Inventory inventory = player.getInventory();
        inventory.selected = 0;
        inventory.clearContent();
        ItemStack holding = chestItemHandler.getStackInSlot(index).copy();
        inventory.setItem(0, holding);
        if (!holding.isEmpty()) {
            holding.getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (holder, mod) -> {
                AttributeInstance attr = player.getAttribute(holder);
                if (attr != null) {
                    attr.addOrUpdateTransientModifier(mod);
                }
            });
        }

        try {
            return action.apply(player);
        } finally {
            chestItemHandler.setStackInSlot(index, inventory.getItem(0));
            inventory.setItem(0, ItemStack.EMPTY);
            inventory.dropAll();
            if (!holding.isEmpty()) {
                holding.getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (holder, mod) -> {
                    AttributeInstance attr = player.getAttribute(holder);
                    if (attr != null) {
                        attr.removeModifier(mod);
                    }
                });
            }
        }
    }
}
