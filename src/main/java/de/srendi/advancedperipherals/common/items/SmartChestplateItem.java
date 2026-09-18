package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.component.ItemStackStorage;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.entity.SmartChestHand;
import de.srendi.advancedperipherals.common.items.base.BaseArmorItem;
import de.srendi.advancedperipherals.common.setup.APEntities;
import de.srendi.advancedperipherals.common.smartchestplate.SmartChestplateItemHandler;
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

// Inspired by F-Tech: Equipment (https://www.curseforge.com/minecraft/mc-mods/f-tech-equipment)
public class SmartChestplateItem extends BaseArmorItem {
    public SmartChestplateItem(Holder<ArmorMaterial> material) {
        super(material, ArmorItem.Type.CHESTPLATE, new Properties().stacksTo(1));
    }

    public IItemHandlerModifiable createItemHandlerCap(ItemStack stack) {
        return new SmartChestplateItemHandler(stack);
    }

    // public Object createCurioCap(ItemStack stack) {
    //     return new SmartChestplateCurio(this, stack);
    // }

    @Override
    public boolean isEnabled() {
        return !APConfig.PERIPHERALS_CONFIG.disableSmartChestplate.get();
    }

    public void onActiveTick(ItemStack chestStack, ServerLevel level, LivingEntity entity, DataStorage data) {
        ItemStackStorage items = SmartChestplateItemHandler.loadItems(chestStack);
        for (int i = 0; i < SmartChestplateItemHandler.SLOTS; i++) {
            data.getOrCreateHand(i, entity, chestStack).setHoldingStack(items.get(i));
        }
    }

    public static ItemStack getEquipped(final LivingEntity entity) {
        if (APConfig.PERIPHERALS_CONFIG.disableSmartChestplate.get()) {
            return ItemStack.EMPTY;
        }
        final ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof SmartChestplateItem) {
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
        if (APConfig.PERIPHERALS_CONFIG.disableSmartChestplate.get()) {
            return ItemStack.EMPTY;
        }
        final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (curiosInv == null) {
            return ItemStack.EMPTY;
        }
        final SlotResult glassesSlot = curiosInv.findFirstCurio((stack) -> stack.getItem() instanceof SmartChestplateItem).orElse(null);
        if (glassesSlot == null) {
            return ItemStack.EMPTY;
        }
        return glassesSlot.stack();
    }

    public static final class DataStorage {
        private final SmartChestHand[] hands = new SmartChestHand[SmartChestplateItemHandler.SLOTS];

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
