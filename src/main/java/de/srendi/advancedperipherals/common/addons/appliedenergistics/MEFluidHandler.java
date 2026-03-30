package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.storage.MEStorage;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MEBridgePeripheral;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import de.srendi.advancedperipherals.common.util.inventory.StorageProcessor;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Used to transfer item between an inventory and the ME system.
 *
 * @see MEBridgePeripheral
 */
public class MEFluidHandler implements IStorageSystemFluidHandler {

    @NotNull
    private final MEStorage storageMonitor;
    @NotNull
    private final IActionSource actionSource;

    public MEFluidHandler(@NotNull MEStorage storageMonitor, @NotNull IActionSource actionSource) {
        this.storageMonitor = storageMonitor;
        this.actionSource = actionSource;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        AEFluidKey itemKey = AEFluidKey.of(resource.getFluid());
        // should never overflow
        return (int) storageMonitor.insert(itemKey, resource.getAmount(), action.simulate() ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
    }

    @Override
    public int extractFluids(FluidFilter filter, StorageProcessor<FluidStack> processor, FluidAction action) {
        List<Pair<Long, AEFluidKey>> fluidKeys = AEApi.findAEFluidsFromFilter(storageMonitor, filter);
        if (fluidKeys.isEmpty()) {
            return 0;
        }
        int needs = filter.getAmount();
        for (Pair<Long, AEFluidKey> pair : fluidKeys) {
            AEFluidKey fluidKey = pair.right();
            int amount = (int) storageMonitor.extract(fluidKey, needs, Actionable.SIMULATE, actionSource);
            if (amount == 0) {
                continue;
            }
            int extracted = processor.process(new FluidStack(fluidKey.getFluid(), amount));
            if (extracted == 0) {
                continue;
            }
            needs -= extracted;
            if (action.execute()) {
                storageMonitor.extract(fluidKey, extracted, Actionable.MODULATE, actionSource);
            }
            if (needs <= 0) {
                break;
            }
        }
        return filter.getAmount() - needs;
    }
}
