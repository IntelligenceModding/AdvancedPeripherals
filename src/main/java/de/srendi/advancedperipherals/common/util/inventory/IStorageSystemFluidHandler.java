package de.srendi.advancedperipherals.common.util.inventory;


import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public interface IStorageSystemFluidHandler extends IFluidHandler {

    /**
     * Used to extract an item from the system via a peripheral.
     * Uses a filter to find the right item. The amount should never be greater than 64
     * stack sizes greater than 64.
     *
     * @param filter The parsed filter
     * @param simulate Should this action be simulated
     * @return extracted from the slot, must be empty if nothing can be extracted. The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     */
    @NotNull
    FluidStack drain(FluidFilter filter, FluidAction simulate);

    @Override
    default int getTanks() {
        return 1;
    }

    /*
    These 5 methods are ignored in our transferring logic. Storage Systems do not respect tanks directly and to extract we need a filter
     */

    @NotNull
    @Override
    default FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;

    }

    @NotNull
    @Override
    default FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;

    }

    @NotNull
    @Override
    default FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return false;

    }

    @Override
    default int getTankCapacity(int tank) {
        return 0;
    }
}
