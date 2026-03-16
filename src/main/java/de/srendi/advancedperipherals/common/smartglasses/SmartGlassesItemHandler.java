package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.impl.PocketUpgrades;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SmartGlassesItemHandler implements IItemHandlerModifiable {

    public static final int SLOTS = 11;
    public static final int PERIPHERAL_SLOTS = 5;

    private final ItemStack glasses;
    private final SmartGlassesComputer computer;

    public SmartGlassesItemHandler(ItemStack glasses, SmartGlassesComputer computer) {
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
        RegistryAccess registryAccess = this.computer.getLevel().registryAccess();
        List<ItemStack> items = this.loadItems();
        if (slot < PERIPHERAL_SLOTS) {
            UpgradeData<IPocketUpgrade> upgradeData = PocketUpgrades.instance().get(registryAccess, stack);
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
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
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
                setStackInSlot(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }

            setChanged();
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
        return loadItems().get(slot);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        NonNullList<ItemStack> items = loadItems();
        if (ItemStack.isSameItemSameComponents(stack, items.get(slot))) {
            return;
        }
        items.set(slot, stack);
        saveItems(items);
        setChanged();
    }

    public void setChanged() {
        if (this.computer != null) {
            this.computer.invalidatePeripheral();
        }
    }

    public void saveItems(NonNullList<ItemStack> items) {
        RegistryAccess registryAccess = this.computer.getLevel().registryAccess();
        this.glasses.set(APDataComponents.ITEMS, ContainerHelper.saveAllItems(new CompoundTag(), items, registryAccess));
    }

    public NonNullList<ItemStack> loadItems() {
        RegistryAccess registryAccess = this.computer.getLevel().registryAccess();
        NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(this.glasses.getOrDefault(APDataComponents.ITEMS, new CompoundTag()), items, registryAccess);
        return items;
    }
}
