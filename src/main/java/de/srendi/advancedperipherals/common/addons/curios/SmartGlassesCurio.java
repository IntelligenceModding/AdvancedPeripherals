package de.srendi.advancedperipherals.common.addons.curios;

import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
    public void curioTick(SlotContext context) {
        final LivingEntity owner = context.entity();
        if (owner.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof SmartGlassesItem) {
            return;
        }
        this.item.onEquippedTick(this.stack, owner.level(), owner);
    }
}
