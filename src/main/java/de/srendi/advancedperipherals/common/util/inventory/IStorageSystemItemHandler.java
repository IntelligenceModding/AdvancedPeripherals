/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.util.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public interface IStorageSystemItemHandler extends IItemHandler {

    /**
     * Used to extract an item from the system via a peripheral. Uses a filter to
     * find the right item. The amount should never be greater than 64 stack sizes
     * greater than 64.
     *
     * @param filter
     *            The parsed filter
     * @param filter
     *            The parsed filter
     * @param count
     *            The amount to extract
     * @param simulate
     *            Should this action be simulated
     * @return extracted from the slot, must be empty if nothing can be extracted.
     *         The returned ItemStack can be safely modified after, so item handlers
     *         should return a new or copied stack.
     */
    ItemStack extractItem(ItemFilter filter, int count, boolean simulate);

    /*
     * These 5 methods are ignored in our transferring logic. Storage Systems do not
     * respect slots and to extract we need a filter
     */

    @Override
    default int getSlots() {
        return 0;
    }

    @Override
    default int getSlotLimit(int slot) {
        return 0;
    }

    @NotNull @Override
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @NotNull @Override
    default ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }
}
