/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.util.inventory;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public interface IStorageSystemFluidHandler extends IFluidHandler {

    /**
     * Used to extract an item from the system via a peripheral. Uses a filter to
     * find the right item. The amount should never be greater than 64 stack sizes
     * greater than 64.
     *
     * @param filter
     *            The parsed filter
     * @param simulate
     *            Should this action be simulated
     * @return extracted from the slot, must be empty if nothing can be extracted.
     *         The returned ItemStack can be safely modified after, so item handlers
     *         should return a new or copied stack.
     */
    @NotNull FluidStack drain(FluidFilter filter, FluidAction simulate);

    @Override
    default int getTanks() {
        return 1;
    }

    /*
     * These 5 methods are ignored in our transferring logic. Storage Systems do not
     * respect tanks directly and to extract we need a filter
     */

    @NotNull @Override
    default FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;

    }

    @NotNull @Override
    default FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;

    }

    @NotNull @Override
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
