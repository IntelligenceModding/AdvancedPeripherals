package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import de.srendi.advancedperipherals.common.util.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.ItemFilter;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MeItemHandler implements IStorageSystemItemHandler {

    @NotNull
    private MEStorage storageMonitor;
    @NotNull
    private ICraftingService craftingService;
    @NotNull
    private IActionSource actionSource;

    public MeItemHandler(@NotNull MEStorage storageMonitor, @NotNull ICraftingService craftingService, @NotNull IActionSource actionSource) {
        this.storageMonitor = storageMonitor;
        this.craftingService = craftingService;
        this.actionSource = actionSource;
    }

    @Override
    public int getSlots() {
        return 0;
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return null;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return null;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    public ItemStack extractItem(ItemFilter filter, boolean simulate) {
        Pair<Long, AEItemKey> itemKey = AppEngApi.findAEStackFromItemStack(storageMonitor, craftingService, filter);
        long extracted = storageMonitor.extract(itemKey.getRight(), Math.max(64, filter.getCount()), simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        // Safe to cast here, the amount will never be higher than 64
        return new ItemStack(itemKey.getRight().getItem(), (int) extracted);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }

}
