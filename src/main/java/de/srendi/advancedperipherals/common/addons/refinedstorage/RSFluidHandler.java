package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import de.srendi.advancedperipherals.common.util.inventory.StorageProcessor;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RSFluidHandler implements IStorageSystemFluidHandler {

    @NotNull
    private final Network network;
    private final StorageNetworkComponent component;

    public RSFluidHandler(@NotNull Network network) {
        this.network = network;
        this.component = network.getComponent(StorageNetworkComponent.class);
    }

    @Override
    public int fill(FluidStack resource, @NotNull FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        // should never overflow
        return (int) component.insert(VariantUtil.ofFluidStack(resource), resource.getAmount(), action.simulate() ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY);
    }

    @Override
    public int extractFluids(FluidFilter filter, StorageProcessor<FluidStack> processor, FluidAction action) {
        List<FluidResource> fluids = RSApi.getFluids(network, filter);
        if (fluids.isEmpty()) {
            return 0;
        }
        int needs = filter.getAmount();
        for (FluidResource fluid : fluids) {
            int amount = (int) component.extract(fluid, needs, Action.SIMULATE, Actor.EMPTY);
            if (amount == 0) {
                continue;
            }
            int extracted = processor.process(VariantUtil.toFluidStack(fluid, amount));
            if (extracted == 0) {
                continue;
            }
            needs -= extracted;
            if (action.execute()) {
                component.extract(fluid, extracted, Action.EXECUTE, Actor.EMPTY);
            }
            if (needs <= 0) {
                break;
            }
        }
        return filter.getAmount() - needs;
    }
}
