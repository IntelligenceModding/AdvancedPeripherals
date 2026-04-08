package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MEBridgePeripheral;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemChemicalHandler;
import de.srendi.advancedperipherals.common.util.inventory.StorageProcessor;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    @Override
    @NotNull
    public ChemicalStack insertChemical(ChemicalStack resource, @NotNull Action action) {
        if (resource.isEmpty()) {
            return resource;
        }

        ChemicalStack remain = resource.copy();
        long amountInserted = storageMonitor.insert(MekanismKey.of(resource), resource.getAmount(), action.simulate() ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        remain.setAmount(resource.getAmount() - amountInserted);
        return remain;
    }

    @Override
    public long extractChemicals(ChemicalFilter filter, StorageProcessor.Large<ChemicalStack> processor, Action action) {
        List<Pair<Long, MekanismKey>> chemicalKeys = AEMekanismApi.findAEChemicalsFromFilter(storageMonitor, filter);
        if (chemicalKeys.isEmpty()) {
            return 0;
        }

        long needs = filter.getAmount();
        for (Pair<Long, MekanismKey> pair : chemicalKeys) {
            MekanismKey chemicalKey = pair.right();
            long amount = storageMonitor.extract(chemicalKey, needs, Actionable.SIMULATE, actionSource);
            if (amount == 0) {
                continue;
            }
            long extracted = processor.process(chemicalKey.getStack().copyWithAmount(amount));
            if (extracted == 0) {
                continue;
            }
            needs -= extracted;
            if (action.execute()) {
                storageMonitor.extract(chemicalKey, extracted, Actionable.MODULATE, actionSource);
            }
            if (needs <= 0) {
                break;
            }
        }
        return filter.getAmount() - needs;
    }
}
