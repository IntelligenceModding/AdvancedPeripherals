package de.srendi.advancedperipherals.common.util.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public interface IStorageSystemItemHandler extends IItemHandler {

    ItemStack insertItem(ItemStack stack, boolean simulate);

    /**
     * Used to extract an item from the system via a peripheral.
     * Uses a filter to find the right item. The amount should never be greater than 64
     * stack sizes greater than 64.
     *
     * @param filter The parsed filter
     * @param processor The extractation processor
     * @param simulate Should this action be simulated
     * @return extracted item count
     */
    int extractItems(ItemFilter filter, StorageProcessor<ItemStack> processor, boolean simulate);

    /**
     * These 6 methods below are ignored in our transferring logic.
     * Storage Systems do not respect slots and to extract we need a filter
     */

    @Override
    default int getSlots() {
        throw new UnsupportedOperationException();
    }

    @Override
    default int getSlotLimit(int slot) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    default ItemStack getStackInSlot(int slot) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isItemValid(int slot, @NotNull ItemStack stack) {
        throw new UnsupportedOperationException();
    }
}
