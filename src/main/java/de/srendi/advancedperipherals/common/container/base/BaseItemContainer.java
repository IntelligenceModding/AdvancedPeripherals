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
package de.srendi.advancedperipherals.common.container.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class BaseItemContainer extends AbstractContainerMenu {

    protected final IItemHandler inventory;
    protected final ItemStack item;

    protected BaseItemContainer(int id, @Nullable MenuType<?> type, Inventory inventory, ItemStack item) {
        super(type, id);
        this.inventory = new InvWrapper(inventory);
        this.item = item;
    }

    /*
     * @Override public ItemStack transferStackInSlot(PlayerEntity playerIn, int
     * index) { ItemStack itemstack = ItemStack.EMPTY; Slot slot =
     * this.inventorySlots.get(index); if (slot != null && slot.getHasStack()) {
     * ItemStack itemstack1 = slot.getStack(); itemstack = itemstack1.copy(); if
     * (index >= 36) { if (!this.mergeItemStack(itemstack, 0, 36, false) &&
     * !tileEntity.canExtractItem(index, itemstack, Direction.NORTH)) { return
     * ItemStack.EMPTY; } slot.onSlotChange(itemstack, itemstack1); } else { if
     * (!this.mergeItemStack(itemstack, 36, getInventory().size(), false) &&
     * !tileEntity.canInsertItem(index, itemstack, Direction.NORTH)) { return
     * ItemStack.EMPTY; } }
     * 
     * if (itemstack.getCount() == 0) { slot.putStack(ItemStack.EMPTY); } else {
     * slot.onSlotChanged(); }
     * 
     * slot.onTake(playerIn, itemstack); }
     * 
     * return itemstack; }
     */

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount,
            int dy) {
        for (int i = 0; i < verAmount; i++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public void layoutPlayerInventorySlots(int leftCol, int topRow) {
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);

        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
    }

}
