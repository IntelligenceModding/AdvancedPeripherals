package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemChemicalHandler;
import mekanism.api.chemical.ChemicalStack;
import org.jetbrains.annotations.NotNull;

public class RsChemicalHandler implements IStorageSystemChemicalHandler {

    @NotNull
    private final Network network;
    private final StorageNetworkComponent component;

    public RsChemicalHandler(@NotNull Network network) {
        this.network = network;
        this.component = network.getComponent(StorageNetworkComponent.class);
    }

    @NotNull
    @Override
    public ChemicalStack insertChemical(int tank, ChemicalStack resource, mekanism.api.Action action) {
        if (resource.isEmpty())
            return resource;

        ChemicalStack chemical = resource.copy();
        chemical.setAmount(resource.getAmount() - component.insert(ChemicalResource.ofChemicalStack(chemical), resource.getAmount(), action == mekanism.api.Action.SIMULATE ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY));
        return chemical;
    }

    @Override
    public ChemicalStack extractChemical(ChemicalFilter filter, long count, mekanism.api.Action simulate) {
        AdvancedPeripherals.debug("Trying to extract fluid from filter: " + filter);
        ChemicalResource fluid = RsApi.getChemical(network, filter);
        if (fluid == null)
            return ChemicalStack.EMPTY;

        ChemicalStack extracted = new ChemicalStack(fluid.chemical(), 1);
        extracted.setAmount((int) component.extract(fluid, filter.getCount(), simulate == mekanism.api.Action.SIMULATE ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY));

        AdvancedPeripherals.debug("Extracted fluid: " + extracted + " from filter: " + filter);
        return extracted;
    }

}
