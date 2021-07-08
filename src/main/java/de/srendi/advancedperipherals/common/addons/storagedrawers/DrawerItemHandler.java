package de.srendi.advancedperipherals.common.addons.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class DrawerItemHandler implements IItemHandler {
    private final IDrawerGroup group;

    public DrawerItemHandler(IDrawerGroup group) {
        this.group = group;
    }

    @Override
    public int getSlots() {
        return group.getDrawerCount();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return group.getDrawer(slot).getStoredItemPrototype();
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        IDrawer drawer = group.getDrawer(slot);
        int stackCount = stack.getCount();
        if (!drawer.canItemBeStored(stack))
            return stack;
        int currentCount = 0;
        if (!drawer.isEmpty())
            currentCount = drawer.getStoredItemCount();
        int storedCount = Math.min(stackCount, drawer.getMaxCapacity(stack) - currentCount);
        if (!simulate) {
            if (drawer.isEmpty())
                drawer.setStoredItem(stack);
            drawer.setStoredItemCount(storedCount + currentCount);
        }
        if (storedCount == stackCount)
            return ItemStack.EMPTY;
        ItemStack stackCopy = stack.copy();
        stackCopy.setCount(stackCount - storedCount);
        return stackCopy;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        IDrawer drawer = group.getDrawer(slot);
        if (drawer.isEmpty())
            return ItemStack.EMPTY;
        int currentCount = drawer.getStoredItemCount();
        int extractedCount = Math.min(currentCount, amount);
        ItemStack extracted = drawer.getStoredItemPrototype().copy();
        extracted.setCount(extractedCount);
        if (!simulate) {
            drawer.setStoredItemCount(currentCount - extractedCount);
        }
        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        IDrawer drawer = group.getDrawer(slot);
        if (drawer.isEmpty())
            return drawer.getMaxCapacity();
        return drawer.getMaxCapacity(drawer.getStoredItemPrototype());
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        IDrawer drawer = group.getDrawer(slot);
        if (drawer.isEmpty())
            return true;
        return stack.getItem() == drawer.getStoredItemPrototype().getItem();
    }
}
