package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemChemicalHandler;
import mekanism.api.chemical.ChemicalStack;
import org.jetbrains.annotations.NotNull;

public class RSChemicalHandler implements IStorageSystemChemicalHandler {

    @NotNull
    private final Network network;
    private final StorageNetworkComponent component;

    public RSChemicalHandler(@NotNull Network network) {
        this.network = network;
        this.component = network.getComponent(StorageNetworkComponent.class);
    }

    @NotNull
    @Override
    public ChemicalStack insertChemical(int tank, ChemicalStack resource, @NotNull mekanism.api.Action action) {
        if (resource.isEmpty())
            return resource;

        long amountInserted = component.insert(ChemicalResource.ofChemicalStack(resource), resource.getAmount(), action == mekanism.api.Action.SIMULATE ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY);
        ChemicalStack remain = resource.copyWithAmount(resource.getAmount() - amountInserted);
        return remain;
    }

    @Override
    public ChemicalStack extractChemical(ChemicalFilter filter, mekanism.api.Action simulate) {
        AdvancedPeripherals.debug("Trying to extract chemical from filter: " + filter);
        ChemicalResource chemical = RSApi.getChemical(network, filter);
        if (chemical == null)
            return ChemicalStack.EMPTY;

        long amountExtracted = component.extract(chemical, filter.getAmount(), simulate == mekanism.api.Action.SIMULATE ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY);
        ChemicalStack extracted = ChemicalUtil.toChemicalStack(chemical.chemical(), amountExtracted);

        AdvancedPeripherals.debug("Extracted chemical: " + extracted + " from filter: " + filter);
        return extracted;
    }

}
