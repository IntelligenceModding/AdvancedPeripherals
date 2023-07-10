package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RsFluidHandler implements IStorageSystemFluidHandler {

    @NotNull
    private final INetwork network;

    public RsFluidHandler(@NotNull INetwork network) {
        this.network = network;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return 0;
        return resource.getAmount() - network.insertFluid(resource, resource.getAmount(), action == FluidAction.SIMULATE ? Action.SIMULATE : Action.PERFORM).getAmount();
    }

    @NotNull
    @Override
    public FluidStack drain(FluidFilter filter, FluidAction simulate) {
        FluidStack fluid = RefinedStorage.findFluidFromFilter(network, null, filter);
        if (fluid == null)
            return FluidStack.EMPTY;
        return network.extractFluid(fluid, filter.getCount(), IComparer.COMPARE_QUANTITY, simulate == FluidAction.SIMULATE ? Action.SIMULATE : Action.PERFORM);
    }
}
