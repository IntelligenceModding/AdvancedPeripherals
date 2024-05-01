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
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Used to transfer item between an inventory and the RS system.
 *
 * @see de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral
 */
public class RsItemHandler implements IStorageSystemItemHandler {

    @NotNull private final INetwork network;

    public RsItemHandler(@NotNull INetwork network) {
        this.network = network;
    }

    @NotNull @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return network.insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
    }

    @Override
    public ItemStack extractItem(ItemFilter filter, int count, boolean simulate) {
        ItemStack item = RefinedStorage.findStackFromFilter(network, network.getCraftingManager(), filter);
        if (item == null)
            AdvancedPeripherals.debug("Trying to extract item: " + item + " from filter: " + filter);
        if (item == null)
            return ItemStack.EMPTY;
        ItemStack extracted = network.extractItem(item, count, IComparer.COMPARE_NBT,
                simulate ? Action.SIMULATE : Action.PERFORM);
        AdvancedPeripherals.debug("Extracted item: " + extracted + " from filter: " + filter);
        return extracted;
    }

}
