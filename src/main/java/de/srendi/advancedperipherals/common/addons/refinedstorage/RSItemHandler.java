package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.refinedmods.refinedstorage.api.core.Action;

/**
 * Used to transfer item between an inventory and the RS system.
 *
 * @see de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RSBridgePeripheral
 */
public class RSItemHandler implements IStorageSystemItemHandler {

    @NotNull
    private final Network network;
    private final StorageNetworkComponent component;

    public RSItemHandler(@NotNull Network network) {
        this.network = network;
        this.component = network.getComponent(StorageNetworkComponent.class);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        long insertedAmount = component.insert(ItemResource.ofItemStack(stack), stack.getCount(), simulate ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY);
        ItemStack remain = stack.copyWithCount((int) (stack.getCount() - insertedAmount));
        return remain;
    }

    @Override
    public ItemStack extractItem(ItemFilter filter, boolean simulate) {
        AdvancedPeripherals.debug("Trying to extract item from filter: " + filter);
        ItemResource itemResource = RSApi.getItem(network, filter);
        if (itemResource == null)
            return ItemStack.EMPTY;

        long extractedAmount = component.extract(itemResource, filter.getCount(), simulate ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY);
        ItemStack extracted = itemResource.toItemStack(extractedAmount);

        AdvancedPeripherals.debug("Extracted item: " + extracted + " from filter: " + filter);
        return extracted;
    }

}
