package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.component.ItemStackStorage;
import de.srendi.advancedperipherals.common.entity.SmartChestHand;
import de.srendi.advancedperipherals.common.setup.APEntities;
import de.srendi.advancedperipherals.common.smartchestmount.SmartChestMountItemHandler;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class SmartChestMountItem extends ArmorItem {
    public SmartChestMountItem(Holder<ArmorMaterial> material) {
        super(material, ArmorItem.Type.CHESTPLATE, new Properties().stacksTo(1));
    }

    public IItemHandlerModifiable createItemHandlerCap(ItemStack stack) {
        return new SmartChestMountItemHandler(stack);
    }

    // public Object createCurioCap(ItemStack stack) {
    //     return new SmartChestMountCurio(this, stack);
    // }

    public void onActiveTick(ItemStack chestStack, ServerLevel level, LivingEntity entity, DataStorage data) {
        ItemStackStorage items = SmartChestMountItemHandler.loadItems(chestStack);
        for (int i = 0; i < SmartChestMountItemHandler.SLOTS; i++) {
            data.getOrCreateHand(i, entity, chestStack).setHoldingStack(items.get(i));
        }
    }

    public static ItemStack getEquipped(final LivingEntity entity) {
        final ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof SmartChestMountItem) {
            return chest;
        }
        if (APAddon.CURIOS.isLoaded()) {
            return getEquippedCurios(entity);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getEquippedCurios(final LivingEntity entity) {
        if (!APAddon.CURIOS.isLoaded()) {
            return ItemStack.EMPTY;
        }
        final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (curiosInv == null) {
            return ItemStack.EMPTY;
        }
        final SlotResult glassesSlot = curiosInv.findFirstCurio((stack) -> stack.getItem() instanceof SmartChestMountItem).orElse(null);
        if (glassesSlot == null) {
            return ItemStack.EMPTY;
        }
        return glassesSlot.stack();
    }

    public static final class DataStorage {
        private final SmartChestHand[] hands = new SmartChestHand[SmartChestMountItemHandler.SLOTS];

        public SmartChestHand getOrCreateHand(int index, LivingEntity owner, ItemStack chestStack) {
            SmartChestHand hand = this.hands[index];
            if (hand == null || hand.isRemoved()) {
                hand = new SmartChestHand(APEntities.SMART_CHEST_HAND.get(), (ServerLevel) owner.level(), chestStack, index);
                hand.startRiding(owner, true);
                this.hands[index] = hand;
                owner.level().addFreshEntity(hand);
            }
            return hand;
        }
    }
}
