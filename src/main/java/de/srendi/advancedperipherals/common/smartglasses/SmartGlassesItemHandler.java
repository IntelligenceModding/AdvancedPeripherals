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
    private final ItemStack glasses;
    private final SmartGlassesComputer computer;
    private final RegistryAccess registryAccess;

    public SmartGlassesItemHandler(@NotNull ItemStack glasses, @NotNull SmartGlassesComputer computer, @NotNull RegistryAccess registryAccess) {
        this.glasses = glasses;
        this.computer = computer;
        this.registryAccess = registryAccess;
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
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot >= SmartGlassesSlot.SLOTS) {
            return false;
        }
        if (stack.getItem() instanceof SmartGlassesItem) {
            return false;
        }
        List<ItemStack> items = this.loadItems();
        if (slot < SmartGlassesSlot.PERIPHERAL_SLOTS) {
            UpgradeData<IPocketUpgrade> upgradeData = PocketUpgrades.instance().get(this.registryAccess, stack);
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
            if (items.get(i).getItem() == item) {
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
        return this.loadItems().get(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        NonNullList<ItemStack> items = this.loadItems();
        if (ItemStack.isSameItemSameComponents(stack, items.get(slot))) {
            return;
        }
        items.set(slot, stack);
        if (slot < SmartGlassesSlot.PERIPHERAL_SLOTS) {
            UpgradeData<IPocketUpgrade> upgradeData = PocketUpgrades.instance().get(this.registryAccess, stack);
            this.computer.setUpgrade(SmartGlassesSlot.indexToSide(slot), upgradeData);
        } else {
            this.computer.setModule(slot - SmartGlassesSlot.MODULE_SLOT_OFFSET, stack);
        }
        this.saveItems(items);
    }

    private void saveItems(NonNullList<ItemStack> items) {
        saveItems(this.glasses, items, this.registryAccess);
    }

    private NonNullList<ItemStack> loadItems() {
        return loadItems(this.glasses, this.registryAccess);
    }

    public static final void saveItems(ItemStack glasses, NonNullList<ItemStack> items, RegistryAccess registryAccess) {
        glasses.set(APDataComponents.ITEMS, ContainerHelper.saveAllItems(new CompoundTag(), items, registryAccess));
    }

    public static final NonNullList<ItemStack> loadItems(ItemStack glasses, RegistryAccess registryAccess) {
        NonNullList<ItemStack> items = NonNullList.withSize(SmartGlassesSlot.SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(glasses.getOrDefault(APDataComponents.ITEMS, new CompoundTag()), items, registryAccess);
        return items;
    }
}
