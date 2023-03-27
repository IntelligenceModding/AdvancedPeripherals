package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.storage.MEStorage;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * Used to transfer item between an inventory and the ME system.
 *
 * @see de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral
 */
public class MeFluidHandler implements IStorageSystemFluidHandler {

    @NotNull
    private final MEStorage storageMonitor;
    @NotNull
    private final IActionSource actionSource;

    public MeFluidHandler(@NotNull MEStorage storageMonitor, @NotNull IActionSource actionSource) {
        this.storageMonitor = storageMonitor;
        this.actionSource = actionSource;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return 0;
        AEFluidKey itemKey = AEFluidKey.of(resource.getFluid());
        long inserted = storageMonitor.insert(itemKey, resource.getAmount(), action == FluidAction.SIMULATE ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);

        return (int) Math.min(inserted, Integer.MAX_VALUE);
    }

    @NotNull
    @Override
    public FluidStack drain(FluidFilter filter, FluidAction simulate) {
        Pair<Long, AEFluidKey> itemKey = AppEngApi.findAEFluidFromFilter(storageMonitor, null, filter);
        if (itemKey == null)
            return FluidStack.EMPTY;
        long extracted = storageMonitor.extract(itemKey.getRight(), filter.getCount(), simulate == FluidAction.SIMULATE ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        return new FluidStack(itemKey.getRight().getFluid(), (int) Math.min(extracted, Integer.MAX_VALUE));
    }
}
