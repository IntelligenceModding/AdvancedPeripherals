package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.StorageProcessor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    public ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) {
        long insertedAmount = component.insert(ItemResource.ofItemStack(stack), stack.getCount(), simulate ? Action.SIMULATE : Action.EXECUTE, Actor.EMPTY);
        ItemStack remain = stack.copyWithCount((int) (stack.getCount() - insertedAmount));
        return remain;
    }

    @Override
    public int extractItems(ItemFilter filter, StorageProcessor<ItemStack> processor, boolean simulate) {
        List<ItemResource> items = RSApi.getItems(network, filter);
        if (items.isEmpty()) {
            return 0;
        }
        int needs = filter.getCount();
        for (ItemResource itemResource : items) {
            int amount = (int) component.extract(itemResource, needs, Action.SIMULATE, Actor.EMPTY);
            if (amount == 0) {
                continue;
            }
            int extracted = processor.process(itemResource.toItemStack(amount));
            if (extracted == 0) {
                continue;
            }
            needs -= extracted;
            if (!simulate) {
                component.extract(itemResource, extracted, Action.EXECUTE, Actor.EMPTY);
            }
            if (needs <= 0) {
                break;
            }
        }
        return filter.getCount() - needs;
    }
}
