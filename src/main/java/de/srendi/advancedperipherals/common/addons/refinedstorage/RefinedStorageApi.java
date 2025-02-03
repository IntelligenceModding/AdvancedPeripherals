package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.TrackedResourceAmount;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Refined Storage Api helper methods and parsers
 *
 * TODO use PlayerActor where possible
 */
public class RefinedStorageApi {

    public static void registerCapabilities(@NotNull RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                BlockEntityTypes.RS_BRIDGE.get(),
                (blockEntity, side) -> blockEntity);
    }

    public static Map<?, ?> getItem(Network network, ItemFilter filter) {
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if(trackedResource.resourceAmount().resource() instanceof ItemResource itemResource && filter.test(itemResource.toItemStack())) {
                return getObjectFromItemResource(trackedResource);
            }
        }
        return null;
    }

    public static Map<?, ?> getFluid(Network network, FluidFilter filter) {
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if(trackedResource.resourceAmount().resource() instanceof FluidResource fluidResource && filter.test(VariantUtil.toFluidStack(fluidResource, trackedResource.resourceAmount().amount()))) {
                return getObjectFromItemResource(trackedResource);
            }
        }
        return null;
    }

    /**
     * Returns every item from the system while also checking if the filter test passes for the items
     * The filter can be empty, see {@link ItemFilter#empty()}
     *
     * @param network the rs network
     * @param filter The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of items
     */
    public static Set<Map<?, ?>> listItems(Network network, ItemFilter filter) {
        Set<Map<?, ?>> items = new HashSet<>();
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if(trackedResource.resourceAmount().resource() instanceof ItemResource itemResource && filter.test(itemResource.toItemStack())) {
                items.add(getObjectFromItemResource(trackedResource));
            }
        }
        return items;
    }

    /**
     * Returns every fluid from the system while also checking if the filter test passes for the fluids
     * The filter can be empty, see {@link FluidFilter#empty()}
     *
     * @param network the rs network
     * @param filter The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of fluid stacks
     */
    public static Set<Map<?, ?>> listFluids(Network network, FluidFilter filter) {
        Set<Map<?, ?>> items = new HashSet<>();
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if(trackedResource.resourceAmount().resource() instanceof FluidResource fluidResource && filter.test(VariantUtil.toFluidStack(fluidResource, trackedResource.resourceAmount().amount()))) {
                items.add(getObjectFromItemResource(trackedResource));
            }
        }
        return items;
    }

    public static Map<?, ?> getObjectFromResource(@NotNull TrackedResourceAmount trackedResource) {
        if (trackedResource.resourceAmount().resource() instanceof ItemResource) {
            return getObjectFromItemResource(trackedResource);
        }
        if (trackedResource.resourceAmount().resource() instanceof FluidResource) {
            return getObjectFromFluidResource(trackedResource);
        }
        AdvancedPeripherals.debug("Could not create table from unknown resource " + trackedResource.resourceAmount().resource().getClass() + " - Report this to the maintainer of ap", Level.WARN);
        return Collections.emptyMap();
    }

    /**
     * Parses an RS TrackedResourceAmount to a lua object
     * This method assumes you did an instanceof check before that the {@link ResourceKey} is an {@link ItemResource}
     *
     * @param trackedResourceAmount the tracked resource amount containing an ItemResource
     * @return a Map containing the properties which CC can parse to a lua table
     */
    public static Map<?, ?> getObjectFromItemResource(TrackedResourceAmount trackedResourceAmount) {
        ItemResource resource = (ItemResource) trackedResourceAmount.resourceAmount().resource();
        long count = trackedResourceAmount.resourceAmount().amount();
        ItemStack stack = resource.toItemStack();
        Map<String, Object> properties = LuaConverter.itemStackToObject(stack, count);
        properties.put("fingerprint", ItemUtil.getFingerprint(stack));
        return properties;
    }

    /**
     * Parses an RS TrackedResourceAmount to a lua object
     * This method assumes you did an instanceof check before that the {@link ResourceKey} is an {@link ItemResource}
     *
     * @param trackedResourceAmount the tracked resource amount containing an ItemResource
     * @return a Map containing the properties which CC can parse to a lua table
     */
    public static Map<?, ?> getObjectFromFluidResource(TrackedResourceAmount trackedResourceAmount) {
        FluidResource resource = (FluidResource) trackedResourceAmount.resourceAmount().resource();
        long count = trackedResourceAmount.resourceAmount().amount();
        FluidStack stack = VariantUtil.toFluidStack(resource, count);
        Map<String, Object> properties = LuaConverter.fluidStackToObject(stack, count);
        properties.put("fingerprint", FluidUtil.getFingerprint(stack));
        return properties;
    }


}
