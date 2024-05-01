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
package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RsFluidHandler implements IStorageSystemFluidHandler {

    @NotNull private final INetwork network;

    public RsFluidHandler(@NotNull INetwork network) {
        this.network = network;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return 0;
        return resource.getAmount() - network.insertFluid(resource, resource.getAmount(),
                action == FluidAction.SIMULATE ? Action.SIMULATE : Action.PERFORM).getAmount();
    }

    @NotNull @Override
    public FluidStack drain(FluidFilter filter, FluidAction simulate) {
        FluidStack fluid = RefinedStorage.findFluidFromFilter(network, null, filter);
        if (fluid == null)
            return FluidStack.EMPTY;
        return network.extractFluid(fluid, filter.getCount(), IComparer.COMPARE_QUANTITY,
                simulate == FluidAction.SIMULATE ? Action.SIMULATE : Action.PERFORM);
    }
}
