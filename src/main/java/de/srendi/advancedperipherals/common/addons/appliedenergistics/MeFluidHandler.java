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
package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.storage.MEStorage;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * Used to transfer item between an inventory and the ME system.
 *
 * @see de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MeBridgePeripheral
 */
public class MeFluidHandler implements IStorageSystemFluidHandler {

    @NotNull private final MEStorage storageMonitor;
    @NotNull private final IActionSource actionSource;

    public MeFluidHandler(@NotNull MEStorage storageMonitor, @NotNull IActionSource actionSource) {
        this.storageMonitor = storageMonitor;
        this.actionSource = actionSource;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return 0;
        AEFluidKey itemKey = AEFluidKey.of(resource.getFluid());
        long inserted = storageMonitor.insert(itemKey, resource.getAmount(),
                action == FluidAction.SIMULATE ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);

        return (int) Math.min(inserted, Integer.MAX_VALUE);
    }

    @NotNull @Override
    public FluidStack drain(FluidFilter filter, FluidAction simulate) {
        Pair<Long, AEFluidKey> itemKey = AppEngApi.findAEFluidFromFilter(storageMonitor, null, filter);
        if (itemKey == null)
            return FluidStack.EMPTY;
        long extracted = storageMonitor.extract(itemKey.getRight(), filter.getCount(),
                simulate == FluidAction.SIMULATE ? Actionable.SIMULATE : Actionable.MODULATE, actionSource);
        return new FluidStack(itemKey.getRight().getFluid(), (int) Math.min(extracted, Integer.MAX_VALUE));
    }
}
