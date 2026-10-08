package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.component.ItemStackStorage;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.entity.SmartChestHand;
import de.srendi.advancedperipherals.common.items.base.BaseArmorItem;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.setup.APEntities;
import de.srendi.advancedperipherals.common.smartchestplate.SmartChestplateItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

// Inspired by F-Tech: Equipment (https://www.curseforge.com/minecraft/mc-mods/f-tech-equipment)
public class SmartChestplateItem extends BaseArmorItem {
    private static final Tag ZERO_POS_TAG = Vec3.CODEC.encodeStart(NbtOps.INSTANCE, Vec3.ZERO).result().orElseThrow();
    private final int slots = SmartChestplateItemHandler.SLOTS;

    public SmartChestplateItem(ArmorMaterial material, Properties properties) {
        super(material, ArmorItem.Type.CHESTPLATE, properties);
    }

    public IItemHandlerModifiable createItemHandlerCap(ItemStack stack) {
        return new SmartChestplateItemHandler(stack);
    }

    // public Object createCurioCap(ItemStack stack) {
    //     return new SmartChestplateCurio(this, stack);
    // }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ITEM_HANDLER) {
                    return LazyOptional.of(() -> createItemHandlerCap(stack)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public boolean isEnabled() {
        return !APConfig.PERIPHERALS_CONFIG.disableSmartChestplate.get();
    }

    public void onActiveTick(ItemStack chestStack, ServerLevel level, LivingEntity entity, DataStorage data) {
        ItemStackStorage items = SmartChestplateItemHandler.loadItems(chestStack);
        for (int i = 0; i < this.slots; i++) {
            data.getOrCreateHand(i, entity, chestStack).setHoldingStack(items.get(i));
        }
    }

    public Vec3 getHandPosition(ItemStack chestStack, int index) {
        if (!chestStack.hasTag()) {
            return Vec3.ZERO;
        }
        ListTag positions = chestStack.getTag().getList(APDataComponents.POSITIONS, Tag.TAG_COMPOUND);
        return Vec3.CODEC.parse(NbtOps.INSTANCE, positions.getCompound(index)).result().orElse(Vec3.ZERO);
    }

    public void setHandPosition(ItemStack chestStack, int index, Vec3 pos) {
        CompoundTag tag = chestStack.getOrCreateTag();
        ListTag positions = tag.getList(APDataComponents.POSITIONS, Tag.TAG_COMPOUND);
        while (positions.size() < this.slots) {
            positions.add(ZERO_POS_TAG);
        }
        positions.setTag(index, Vec3.CODEC.encodeStart(NbtOps.INSTANCE, pos).result().orElseThrow());
        tag.put(APDataComponents.POSITIONS, positions);
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
            ServerLevel level = (ServerLevel) owner.level();
            SmartChestplateItem item = (SmartChestplateItem) chestStack.getItem();
            SmartChestHand hand = this.hands[index];
            boolean handInvalid = hand == null || hand.isRemoved();
            if (!handInvalid) {
                if (hand.level() != level) {
                    handInvalid = true;
                    hand.discard();
                }
            }
            if (handInvalid) {
                hand = new SmartChestHand(APEntities.SMART_CHEST_HAND.get(), level, chestStack, index);
                hand.setRelativePos(item.getHandPosition(chestStack, index).toVector3f());
                hand.setPos(owner.position());
                hand.startRiding(owner, true);
                this.hands[index] = hand;
                level.addFreshEntity(hand);
            }
            return hand;
        }
    }
}
