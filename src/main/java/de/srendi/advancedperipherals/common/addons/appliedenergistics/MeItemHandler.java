package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Used to transfer item between an inventory and the ME system.
 *
 * @see de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral
 */
public class MeItemHandler implements IStorageSystemItemHandler {

    @NotNull
    private final MEStorage storageMonitor;
    @NotNull
    private final IActionSource actionSource;

    public MeItemHandler(@NotNull MEStorage storageMonitor, @NotNull IActionSource actionSource) {
        this.storageMonitor = storageMonitor;
        this.actionSource = actionSource;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        AEItemKey itemKey = AEItemKey.of(stack.getItem());
        long inserted = storageMonitor.insert(itemKey, stack.getCount(), simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        ItemStack insertedStack = stack.copy();
        // Safe to cast here, the amount will never be higher than 64
        insertedStack.setCount(insertedStack.getCount() - (int) inserted);
        return insertedStack;
    }

    @Override
    public ItemStack extractItem(ItemFilter filter, int count, boolean simulate) {
        Pair<Long, AEItemKey> itemKey = AppEngApi.findAEStackFromFilter(storageMonitor, null, filter);
        if (itemKey.getRight() == null)
            return ItemStack.EMPTY;
        long extracted = storageMonitor.extract(itemKey.getRight(), count, simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        // Safe to cast here, the amount will never be higher than 64
        ItemStack stack = itemKey.getRight().toStack();
        stack.setCount((int) extracted);
        return stack;
    }

}
