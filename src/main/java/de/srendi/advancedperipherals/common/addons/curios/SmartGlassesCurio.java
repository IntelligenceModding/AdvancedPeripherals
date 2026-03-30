package de.srendi.advancedperipherals.common.addons.curios;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

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
        if (context.entity() instanceof Player player && player.isSecondaryUseActive()) {
            // open glasses directly
            return false;
        }
        if (context.entity().getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            // try equip glasses to equipment slot first
            return false;
        }
        return true;
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
