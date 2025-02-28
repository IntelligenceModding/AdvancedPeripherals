package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RsFluidHandler implements IStorageSystemFluidHandler {

    @NotNull
    private final Network network;
    private final StorageNetworkComponent component;

    public RsFluidHandler(@NotNull Network network) {
        this.network = network;
        this.component = network.getComponent(StorageNetworkComponent.class);

    }

    @Override
    public int fill(FluidStack resource, @NotNull FluidAction action) {
        if (resource.isEmpty())
            return 0;
        return (int) (resource.getAmount() - component.insert(VariantUtil.ofFluidStack(resource), resource.getAmount(), action == FluidAction.SIMULATE ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY));
    }

    @NotNull
    @Override
    public FluidStack drain(FluidFilter filter, FluidAction simulate) {
        AdvancedPeripherals.debug("Trying to extract fluid from filter: " + filter);
        FluidResource fluid = RefinedStorageApi.getFluid(network, filter);
        if (fluid == null)
            return FluidStack.EMPTY;

        FluidStack extracted = VariantUtil.toFluidStack(fluid, 1);
        extracted.setAmount((int) component.extract(fluid, filter.getCount(), simulate == FluidAction.SIMULATE ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY));

        AdvancedPeripherals.debug("Extracted fluid: " + extracted + " from filter: " + filter);
        return extracted;
    }
}
