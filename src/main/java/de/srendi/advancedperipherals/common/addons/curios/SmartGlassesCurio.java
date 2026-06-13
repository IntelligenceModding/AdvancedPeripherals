package de.srendi.advancedperipherals.common.addons.curios;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class SmartGlassesCurio implements ICurio {
    private final SmartGlassesItem item;
    private final ItemStack stack;

    public SmartGlassesCurio(final SmartGlassesItem item, final ItemStack stack) {
        this.item = item;
        this.stack = stack;
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public boolean canEquipFromUse(SlotContext context) {
        final LivingEntity owner = context.entity();
        if (owner instanceof Player player && player.isSecondaryUseActive()) {
            // open glasses interface
            return false;
        }
        if (owner.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            if (isSmartGlasses(context)) {
                // allow swap
                return true;
            }
            // try equip glasses to equipment slot first
            return false;
        }
        return true;
    }

    @Override
    public boolean canEquip(SlotContext context) {
        if (context.cosmetic()) {
            return true;
        }

        final LivingEntity owner = context.entity();
        if (owner.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof SmartGlassesItem) {
            return false;
        }
        final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(owner).orElse(null);
        if (curiosInv == null) {
            return false;
        }
        final SlotResult otherSlot = curiosInv.findFirstCurio((stack) -> stack.getItem() instanceof SmartGlassesItem).orElse(null);
        if (otherSlot != null) {
            final SlotContext otherContext = otherSlot.slotContext();
            // allow swap
            if (otherContext.index() != context.index() || !otherContext.identifier().equals(context.identifier())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSmartGlasses(SlotContext context) {
        final LivingEntity owner = context.entity();
        if (owner.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof SmartGlassesItem) {
            return false;
        }
        final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(owner).orElse(null);
        if (curiosInv == null) {
            return false;
        }
        final SlotResult slot = curiosInv.findCurio(context.identifier(), context.index()).orElse(null);
        if (slot == null) {
            return false;
        }
        return slot.stack().getItem() instanceof SmartGlassesItem;
    }

    @Override
    public void onUnequip(SlotContext context, ItemStack newStack) {
        final LivingEntity owner = context.entity();
        if (owner.level() instanceof ServerLevel serverLevel) {
            this.item.onUnequip(this.stack, serverLevel, owner);
        }
    }

    @Override
    public void curioTick(SlotContext context) {
        final LivingEntity owner = context.entity();
        if (owner.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof SmartGlassesItem) {
            return;
        }
        this.item.onEquippedTick(this.stack, owner.level(), owner, true);
    }
}
