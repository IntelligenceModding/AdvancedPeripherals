package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.impl.PocketUpgrades;
import de.srendi.advancedperipherals.common.component.ItemStackStorage;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class SmartGlassesItemHandler implements IItemHandlerModifiable {
    private static final ItemStackStorage EMPTY_ITEMS = ItemStackStorage.ofSize(SmartGlassesSlot.SLOTS);
    private final ItemStack glasses;
    private final SmartGlassesComputer computer;

    public SmartGlassesItemHandler(@NotNull ItemStack glasses, @NotNull SmartGlassesComputer computer) {
        this.glasses = glasses;
        this.computer = computer;
    }

    public ItemStack getGlasses() {
        return glasses;
    }

    @Override
    public int getSlots() {
        return SmartGlassesSlot.SLOTS;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot < SmartGlassesSlot.PERIPHERAL_SLOTS) {
            return 1;
        }
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot >= SmartGlassesSlot.SLOTS) {
            return false;
        }
        if (stack.getItem() instanceof SmartGlassesItem) {
            return false;
        }
        ItemStackStorage items = loadItems(this.glasses);
        if (slot < SmartGlassesSlot.PERIPHERAL_SLOTS) {
            UpgradeData<IPocketUpgrade> upgradeData = PocketUpgrades.instance().get(stack);
            if (upgradeData == null) {
                return false;
            }
            IPocketUpgrade upgrade = upgradeData.upgrade();
            if (!upgrade.isItemSuitable(stack)) {
                return false;
            }
            return true;
        }
        Item item = stack.getItem();
        if (!(item instanceof IModuleItem)) {
            return false;
        }
        for (int i = SmartGlassesSlot.MODULE_SLOT_OFFSET; i < SmartGlassesSlot.SLOTS; i++) {
            if (items.getItem(i) == item) {
                return false;
            }
        }
        return true;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!isItemValid(slot, stack)) {
            return stack;
        }
        ItemStack existing = getStackInSlot(slot);
        int limit = getSlotLimit(slot) - existing.getCount();
        if (limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                setStackInSlot(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
                setStackInSlot(slot, existing);
            }
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = getStackInSlot(slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (simulate) {
                return existing.copy();
            }
            setStackInSlot(slot, ItemStack.EMPTY);
            return existing;
        }

        if (!simulate) {
            setStackInSlot(slot, existing.copyWithCount(existing.getCount() - toExtract));
        }
        return existing.copyWithCount(toExtract);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return loadItems(this.glasses).getAllUnsafe()[slot];
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        ItemStackStorage items = loadItems(this.glasses);
        if (slot < SmartGlassesSlot.PERIPHERAL_SLOTS) {
            if (items.isSameItemSameTags(slot, stack)) {
                return;
            }
            UpgradeData<IPocketUpgrade> upgradeData = PocketUpgrades.instance().get(stack);
            this.computer.setUpgrade(SmartGlassesSlot.indexToSide(slot), upgradeData);
        } else {
            this.computer.setModuleStack(slot - SmartGlassesSlot.MODULE_SLOT_OFFSET, stack);
        }
        this.computer.updateStack(this.glasses, true);
    }

    public static final void saveItems(ItemStack glasses, ItemStackStorage items) {
        Tag tag = ItemStackStorage.CODEC.encodeStart(NbtOps.INSTANCE, items).result().orElse(null);
        if (tag != null) {
            glasses.getOrCreateTag().put(APDataComponents.ITEMS, tag);
        }
    }

    public static final ItemStackStorage loadItems(ItemStack glasses) {
        CompoundTag tag = glasses.getTag();
        if (tag == null || !tag.contains(APDataComponents.ITEMS)) {
            return EMPTY_ITEMS;
        }
        return ItemStackStorage.CODEC.parse(NbtOps.INSTANCE, tag.get(APDataComponents.ITEMS)).result().orElse(EMPTY_ITEMS);
    }
}
