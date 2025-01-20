package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.shared.PocketUpgrades;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import javax.annotation.Nonnull;

public class SmartGlassesItemHandler implements IItemHandlerModifiable {

    public static final int SLOTS = 11;
    public static final int PERIPHERAL_SLOTS = 5;

    private final ItemStack glasses;
    @Nullable
    private final SmartGlassesComputer computer;

    public SmartGlassesItemHandler(ItemStack glasses, @Nullable SmartGlassesComputer computer) {
        this.glasses = glasses;
        this.computer = computer;
    }

    public ItemStack getGlasses() {
        return glasses;
    }

    @Override
    public int getSlots() {
        return SLOTS;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (stack.getItem() instanceof SmartGlassesItem) {
            return false;
        }
        List<ItemStack> items = this.loadItems();
        if (slot < PERIPHERAL_SLOTS) {
            IPocketUpgrade upgrade = PocketUpgrades.instance().get(stack);
            if (upgrade == null) {
                return false;
            }
            ResourceLocation id = upgrade.getUpgradeID();
            for (int i = 0; i < PERIPHERAL_SLOTS; i++) {
                IPocketUpgrade u = PocketUpgrades.instance().get(items.get(i));
                if (u != null && u.getUpgradeID().equals(id)) {
                    return false;
                }
            }
            return true;
        }
        Item item = stack.getItem();
        if (!(item instanceof IModuleItem module)) {
            return false;
        }
        for (int i = PERIPHERAL_SLOTS; i < SLOTS; i++) {
            if (items.get(i).getItem() == item) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!isItemValid(slot, stack)) {
            return stack;
        }
        ItemStack existing = getStackInSlot(slot);
        if (!existing.isEmpty()) {
            return stack;
        }

        int limit = getSlotLimit(slot);
        if (limit <= 0) {
            return stack;
        }

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
            setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
        }
        return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return loadItems().get(slot);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        NonNullList<ItemStack> items = loadItems();
        if (ItemStack.isSameItemSameTags(stack, items.get(slot))) {
            return;
        }
        items.set(slot, stack);
        saveItems(items);
        setChanged();
    }

    public void setChanged() {
        if (this.computer != null) {
            this.computer.markDirty();
        }
    }

    public void saveItems(NonNullList<ItemStack> items) {
        ContainerHelper.saveAllItems(this.glasses.getOrCreateTag(), items);
    }

    public NonNullList<ItemStack> loadItems() {
        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(this.glasses.getOrCreateTag(), items);
        return items;
    }
}
