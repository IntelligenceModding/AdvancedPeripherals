package de.srendi.advancedperipherals.common.smartglasses;

import de.srendi.advancedperipherals.common.setup.APItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class SmartGlassesItemHandler implements IItemHandlerModifiable, INBTSerializable<CompoundTag> {

    private static final int SLOTS = 12;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final ItemStack stack;
    @Nullable
    private final SmartGlassesComputer computer;

    public SmartGlassesItemHandler(ItemStack stack, @Nullable SmartGlassesComputer computer) {
        this.stack = stack;
        this.computer = computer;
        deserializeNBT(stack.getOrCreateTagElement("Items"));

        if(computer != null)
            computer.setItemHandler(this);
    }

    @Override
    public int getSlots() {
        return items.size();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return items.get(slot);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        ItemStack existing = getStackInSlot(slot);

        int limit = getSlotLimit(slot);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }

            setChanged();
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack existing = getStackInSlot(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                setStackInSlot(slot, ItemStack.EMPTY);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                setChanged();
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }

    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !stack.is(APItems.SMART_GLASSES.get());
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    public void setChanged() {
        stack.getOrCreateTag().put("Items", serializeNBT());
        if (computer != null)
            computer.markDirty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag itemNBT = new CompoundTag();
        ContainerHelper.saveAllItems(itemNBT, items);
        return itemNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, items);
    }
}
