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
 *
 * @see MEBridgePeripheral
 */
public class MEChemicalHandler implements IStorageSystemChemicalHandler {

    @NotNull
    private final MEStorage storageMonitor;
    @NotNull
    private final IActionSource actionSource;

    public MEChemicalHandler(@NotNull MEStorage storageMonitor, @NotNull IActionSource actionSource) {
        this.storageMonitor = storageMonitor;
        this.actionSource = actionSource;
    }

    @NotNull
    @Override
    public ChemicalStack insertChemical(int tank, ChemicalStack resource, @NotNull Action action) {
        if (resource.isEmpty())
            return resource;

        ChemicalStack remain = resource.copy();
        long amountInserted = storageMonitor.insert(MekanismKey.of(resource), resource.getAmount(), action == Action.SIMULATE ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        remain.setAmount(resource.getAmount() - amountInserted);
        return remain;
    }

    @NotNull
    @Override
    public ChemicalStack extractChemical(ChemicalFilter filter, Action simulate) {
        Pair<Long, MekanismKey> chemicalKey = AEApi.findAEChemicalFromFilter(storageMonitor, null, filter);
        if (chemicalKey.getRight() == null)
            return ChemicalStack.EMPTY;

        ChemicalStack extracted = chemicalKey.getRight().getStack();

        long amountExtracted = storageMonitor.extract(chemicalKey.getRight(), filter.getAmount(), simulate == Action.SIMULATE ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        extracted.setAmount(amountExtracted);
        AdvancedPeripherals.debug("Extracted chemical: " + extracted + " from filter: " + filter);
        return extracted;
    }
}
