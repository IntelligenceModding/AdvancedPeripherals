package de.srendi.advancedperipherals.common.smartchestplate;

import de.srendi.advancedperipherals.common.component.ItemStackStorage;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class SmartChestplateItemHandler implements IItemHandlerModifiable {
    public static final int SLOTS = 5;
    private static final ItemStackStorage EMPTY_ITEMS = ItemStackStorage.ofSize(SLOTS);

    private final ItemStack stack;

    public SmartChestplateItemHandler(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getChest() {
        return this.stack;
    }

    @Override
    public int getSlots() {
        return SLOTS;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (stack == this.stack) {
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return loadItems(this.stack).getAllUnsafe()[slot];
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        ItemStackStorage items = loadItems(this.stack);
        items = items.set(slot, stack);
        saveItems(this.stack, items);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!this.isItemValid(slot, stack)) {
            return stack;
        }
        ItemStack existing = this.getStackInSlot(slot);
        if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, stack)) {
            return stack;
        }
        int limit = Math.min(this.getSlotLimit(slot), existing.getMaxStackSize()) - existing.getCount();
        if (limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.getCount() >= limit;

        if (!simulate) {
            this.setStackInSlot(slot, stack.copyWithCount(existing.getCount() + (reachedLimit ? limit : stack.getCount())));
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = this.getStackInSlot(slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (existing.getCount() <= amount) {
            if (!simulate) {
                this.setStackInSlot(slot, ItemStack.EMPTY);
            }
            return existing.copy();
        }

        if (!simulate) {
            this.setStackInSlot(slot, existing.copyWithCount(existing.getCount() - amount));
        }
        return existing.copyWithCount(amount);
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
