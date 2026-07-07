package de.srendi.advancedperipherals.common.util.inventory;


import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public interface IStorageSystemFluidHandler extends IFluidHandler {

    /**
     * Used to extract an item from the system via a peripheral.
     * Uses a filter to find the right item.
     *
     * @param filter The parsed filter
     * @param processor The extractation processor
     * @param action Should this action be simulated
     * @return extracted fluid amount
     */
    int extractFluids(FluidFilter filter, StorageProcessor<FluidStack> processor, FluidAction action);

    @Override
    default int getTanks() {
        return 1;
    }

    /**
     * These 5 methods below are ignored in our transferring logic.
     * Storage Systems do not respect tanks directly and to extract we need a filter.
     */

    @Override
    @NotNull
    default FluidStack drain(int maxDrain, FluidAction action) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    default FluidStack drain(FluidStack resource, FluidAction action) {
        throw new UnsupportedOperationException();

    }

    @Override
    @NotNull
    default FluidStack getFluidInTank(int tank) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        throw new UnsupportedOperationException();

    }

    @Override
    default int getTankCapacity(int tank) {
        throw new UnsupportedOperationException();
    }
}
