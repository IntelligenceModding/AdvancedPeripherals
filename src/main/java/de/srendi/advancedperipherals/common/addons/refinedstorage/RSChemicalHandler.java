package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemChemicalHandler;
import de.srendi.advancedperipherals.common.util.inventory.StorageProcessor;
import mekanism.api.chemical.ChemicalStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RSChemicalHandler implements IStorageSystemChemicalHandler {

    @NotNull
    private final Network network;
    private final StorageNetworkComponent component;

    public RSChemicalHandler(@NotNull Network network) {
        this.network = network;
        this.component = network.getComponent(StorageNetworkComponent.class);
    }

    @Override
    @NotNull
    public ChemicalStack insertChemical(ChemicalStack resource, @NotNull mekanism.api.Action action) {
        if (resource.isEmpty())
            return resource;

        long amountInserted = component.insert(ChemicalResource.ofChemicalStack(resource), resource.getAmount(), action.simulate() ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY);
        ChemicalStack remain = resource.copyWithAmount(resource.getAmount() - amountInserted);
        return remain;
    }

    @Override
    public long extractChemicals(ChemicalFilter filter, StorageProcessor.Large<ChemicalStack> processor, mekanism.api.Action action) {
        List<ChemicalResource> chemicals = RSApi.getChemicals(network, filter);
        if (chemicals.isEmpty()) {
            return 0;
        }

        long needs = filter.getAmount();
        for (ChemicalResource chemical : chemicals) {
            long amount = component.extract(chemical, needs, Action.SIMULATE, Actor.EMPTY);
            if (amount == 0) {
                continue;
            }
            long extracted = processor.process(ChemicalUtil.toChemicalStack(chemical.chemical(), amount));
            if (extracted == 0) {
                continue;
            }
            needs -= extracted;
            if (action.execute()) {
                component.extract(chemical, extracted, Action.EXECUTE, Actor.EMPTY);
            }
            if (needs <= 0) {
                break;
            }
        }
        return filter.getAmount() - needs;
    }
}
