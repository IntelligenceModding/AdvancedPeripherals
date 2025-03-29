package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MEBridgePeripheral;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemChemicalHandler;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import org.jetbrains.annotations.NotNull;

/**
 * Used to transfer a chemical between a tank and the ME system.
 * @see MEBridgePeripheral
 */
public class MeChemicalHandler implements IStorageSystemChemicalHandler {

    @NotNull
    private final MEStorage storageMonitor;
    @NotNull
    private final IActionSource actionSource;

    public MeChemicalHandler(@NotNull MEStorage storageMonitor, @NotNull IActionSource actionSource) {
        this.storageMonitor = storageMonitor;
        this.actionSource = actionSource;
    }

    @NotNull
    @Override
    public ChemicalStack insertChemical(int tank, ChemicalStack resource, @NotNull Action action) {
        if(resource.isEmpty())
            return resource;

        ChemicalStack inserted = resource.copy();
        long amountInserted = storageMonitor.insert(MekanismKey.of(resource), resource.getAmount(), action == Action.SIMULATE ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        inserted.setAmount(resource.getAmount() - amountInserted);
        return inserted;
    }

    @NotNull
    @Override
    public ChemicalStack extractChemical(ChemicalFilter filter, long count, Action simulate) {
        Pair<Long, MekanismKey> itemKey = AppEngApi.findAEChemicalFromFilter(storageMonitor, null, filter);
        if(itemKey == null)
            return ChemicalStack.EMPTY;

        ChemicalStack extracted = itemKey.getRight().getStack();

        long amountExtracted = storageMonitor.extract(itemKey.getRight(), filter.getCount(), simulate == Action.SIMULATE ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        extracted.setAmount(amountExtracted);
        AdvancedPeripherals.debug("Extracted chemical: " + extracted + " from filter: " + filter);
        return extracted;
    }
}
