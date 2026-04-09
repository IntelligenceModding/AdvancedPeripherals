package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MEBridgePeripheral;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.StorageProcessor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Used to transfer item between an inventory and the ME system.
 *
 * @see MEBridgePeripheral
 */
public class MEItemHandler implements IStorageSystemItemHandler {

    @NotNull
    private final MEStorage storageMonitor;
    @NotNull
    private final IActionSource actionSource;

    public MEItemHandler(@NotNull MEStorage storageMonitor, @NotNull IActionSource actionSource) {
        this.storageMonitor = storageMonitor;
        this.actionSource = actionSource;
    }

    @Override
    @NotNull
    public ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) {
        AEItemKey itemKey = AEItemKey.of(stack);
        long inserted = storageMonitor.insert(itemKey, stack.getCount(), simulate ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        // Safe to cast here, the amount will never be higher than 64
        return stack.copyWithCount(stack.getCount() - (int) inserted);
    }

    @Override
    public int extractItems(ItemFilter filter, StorageProcessor<ItemStack> processor, boolean simulate) {
        List<Pair<Long, AEItemKey>> itemKeys = AEApi.findAEStacksFromFilter(storageMonitor, filter);
        if (itemKeys.isEmpty()) {
            return 0;
        }
        int needs = filter.getCount();
        for (Pair<Long, AEItemKey> pair : itemKeys) {
            AEItemKey itemKey = pair.right();
            int count = (int) storageMonitor.extract(itemKey, needs, Actionable.SIMULATE, actionSource);
            if (count == 0) {
                continue;
            }
            int extracted = processor.process(itemKey.toStack().copyWithCount(count));
            if (extracted == 0) {
                continue;
            }
            needs -= extracted;
            if (!simulate) {
                storageMonitor.extract(itemKey, extracted, Actionable.MODULATE, actionSource);
            }
            if (needs <= 0) {
                break;
            }
        }
        return filter.getCount() - needs;
    }
}
