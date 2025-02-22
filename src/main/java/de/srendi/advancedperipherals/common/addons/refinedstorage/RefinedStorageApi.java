package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.autocrafting.Ingredient;
import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
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
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Refined Storage Api helper methods and parsers
 */
public class RefinedStorageApi {

    public static void registerCapabilities(@NotNull RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                BlockEntityTypes.RS_BRIDGE.get(),
                (blockEntity, side) -> blockEntity);
    }

    /**
     * Returns the first item parsed to a lua object which fits to the filter
     *
     * @param network refined storage network
     * @param filter  item filter instance - can be an empty filter to get the first item of the system see {@link ItemFilter#empty()}
     * @return the first item in the system that fits the item filter
     */
    public static Map<String, Object> getItem(Network network, ItemFilter filter) {
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof ItemResource itemResource && filter.test(itemResource.toItemStack())) {
                return getObjectFromItemResource(trackedResource.resourceAmount());
            }
        }
        return null;
    }

    /**
     * Returns the first fluid parsed to a lua object which fits to the filter
     *
     * @param network refined storage network
     * @param filter  fluid filter instance - can be an empty filter to get the first fluid of the system see {@link FluidFilter#empty()}
     * @return the first fluid in the system that fits the fluid filter
     */
    public static Map<String, Object> getFluid(Network network, FluidFilter filter) {
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof FluidResource fluidResource && filter.test(VariantUtil.toFluidStack(fluidResource, trackedResource.resourceAmount().amount()))) {
                return getObjectFromItemResource(trackedResource.resourceAmount());
            }
        }
        return null;
    }

    /**
     * Returns every item from the system while also checking if the filter test passes for the items
     * The filter can be empty, see {@link ItemFilter#empty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of items
     */
    public static Set<Map<String, Object>> getItems(Network network, ItemFilter filter) {
        Set<Map<String, Object>> items = new HashSet<>();
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof ItemResource itemResource && filter.test(itemResource.toItemStack())) {
                items.add(getObjectFromItemResource(trackedResource.resourceAmount()));

            }
        }
        return items;
    }

    /**
     * Returns every fluid from the system while also checking if the filter test passes for the fluids
     * The filter can be empty, see {@link FluidFilter#empty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of fluid stacks
     */
    public static Set<Map<String, Object>> getFluids(Network network, FluidFilter filter) {
        Set<Map<String, Object>> items = new HashSet<>();
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof FluidResource fluidResource && filter.test(VariantUtil.toFluidStack(fluidResource, trackedResource.resourceAmount().amount()))) {
                items.add(getObjectFromFluidResource(trackedResource.resourceAmount()));
            }
        }

        return items;
    }

    /**
     * Returns every craftable item from the system while also checking if the filter test passes for the items
     * The filter can be empty, see {@link ItemFilter#empty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of parsed item stacks
     */
    public static Set<Map<String, Object>> getCraftableItems(Network network, ItemFilter filter) {
        Set<Map<String, Object>> items = new HashSet<>();
        AutocraftingNetworkComponent autocrafting = network.getComponent(AutocraftingNetworkComponent.class);
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (ResourceKey key : autocrafting.getOutputs()) {
            if (key instanceof ItemResource itemResource && filter.test(itemResource.toItemStack())) {
                items.add(getObjectFromResourceKey(key, storage.get(key)));
            }
        }
        return items;
    }

    /**
     * Returns every craftable fluid from the system while also checking if the filter test passes for the fluids
     * The filter can be empty, see {@link FluidFilter#empty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of parsed fluids stacks
     */
    public static Set<Map<String, Object>> getCraftableFluids(Network network, FluidFilter filter) {
        Set<Map<String, Object>> items = new HashSet<>();
        AutocraftingNetworkComponent autocrafting = network.getComponent(AutocraftingNetworkComponent.class);
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (ResourceKey key : autocrafting.getOutputs()) {
            long amount = storage.get(key);
            if (key instanceof FluidResource fluidResource && filter.test(VariantUtil.toFluidStack(fluidResource, amount))) {
                items.add(getObjectFromResourceKey(key, amount));
            }
        }
        return items;
    }

    public static Set<Object> getPatterns(Network network) {
        Set<Object> patterns = new HashSet<>();
        AutocraftingNetworkComponent autocrafting = network.getComponent(AutocraftingNetworkComponent.class);

        for (Pattern pattern : autocrafting.getPatterns()) {
            patterns.add(parsePattern(pattern));
        }

        return patterns;
    }

    /**
     * Finds a pattern from filters.
     *
     * @param network      The network to search patterns from.
     * @param inputFilter  The input filter to apply, can be null to ignore input filter.
     * @param outputFilter The output filter to apply, can be null to ignore output filter.
     * @return A Pair object containing the matched pattern and an error message if no pattern is found.
     * The pattern can be null if no pattern is found.
     * The error message is "NO_PATTERN_FOUND" if no pattern is found.
     */
    public static Pair<Pattern, String> findPatternFromFilters(Network network, @Nullable GenericFilter<?> inputFilter, @Nullable GenericFilter<?> outputFilter) {
        AutocraftingNetworkComponent autocrafting = network.getComponent(AutocraftingNetworkComponent.class);
        for (Pattern pattern : autocrafting.getPatterns()) {
            if (pattern.layout().ingredients().isEmpty())
                continue;
            if (pattern.layout().outputs().isEmpty())
                continue;

            boolean inputMatch = false;
            boolean outputMatch = false;

            if (inputFilter != null) {
                outerLoop:
                for (Ingredient input : pattern.layout().ingredients()) {
                    for (ResourceKey possibleInput : input.inputs()) {
                        if (inputFilter.testRS(new ResourceAmount(possibleInput, 1))) {
                            inputMatch = true;
                            break outerLoop;
                        }
                    }
                }
            } else {
                inputMatch = true;
            }

            if (outputFilter != null) {
                for (ResourceAmount output : pattern.layout().outputs()) {
                    if (outputFilter.testRS(output)) {
                        outputMatch = true;
                        break;
                    }
                }
            } else {
                outputMatch = true;
            }

            if (inputMatch && outputMatch)
                return Pair.of(pattern, null);
        }

        return Pair.of(null, "NO_PATTERN_FOUND");
    }


    private static Map<String, Object> getObjectFromResourceKey(@NotNull ResourceKey resource) {
        return getObjectFromResourceKey(resource, 0);
    }

    /**
     * Helper method to get a parsed lua resource key. Currently only suppors item and fluid resources
     *
     * @param resource the resource
     * @param count    count of the resource - can be 0 and lower
     * @return the parsed key to a lua properties map
     */
    private static Map<String, Object> getObjectFromResourceKey(@NotNull ResourceKey resource, long count) {
        boolean countZeroOrLower = count <= 0;
        if (resource instanceof ItemResource) {
            if (countZeroOrLower) {
                return getObjectFromItemResource(new ResourceAmount(resource, 1), count);
            }
            return getObjectFromItemResource(new ResourceAmount(resource, count));
        }
        if (resource instanceof FluidResource) {
            if (countZeroOrLower) {
                return getObjectFromFluidResource(new ResourceAmount(resource, 1), count);
            }
            return getObjectFromFluidResource(new ResourceAmount(resource, count));
        }
        AdvancedPeripherals.debug("Could not create table from unknown resource " + resource.getClass() + " - Report this to the maintainer of ap", Level.WARN);
        return Collections.emptyMap();
    }

    /**
     * Helper method to get a parsed lua resourceAmount key. Currently only suppors item and fluid resources
     *
     * @param resourceAmount the resourceAmount
     * @return the parsed key to a lua properties map
     */
    private static Map<String, Object> getObjectFromResourceAmount(@NotNull ResourceAmount resourceAmount) {
        if (resourceAmount.resource() instanceof ItemResource) {
            return getObjectFromItemResource(resourceAmount);
        }
        if (resourceAmount.resource() instanceof FluidResource) {
            return getObjectFromFluidResource(resourceAmount);
        }
        AdvancedPeripherals.debug("Could not create table from unknown resourceAmount " + resourceAmount.getClass() + " - Report this to the maintainer of ap", Level.WARN);
        return Collections.emptyMap();
    }

    public static Object parsePattern(Pattern pattern) {
        if (pattern == null)
            return null;

        Map<String, Object> map = new HashMap<>();
        map.put("outputs", pattern.layout().outputs().stream().map(RefinedStorageApi::getObjectFromResourceAmount).toList());

        List<List<Map<String, Object>>> inputs = pattern.layout().ingredients().stream()
                .map(ingredient -> ingredient.inputs().stream()
                        .map(key -> getObjectFromResourceKey(key, ingredient.amount()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        map.put("inputs", inputs);
        map.put("patterntype", pattern.layout().type().toString());
        map.put("id", pattern.id().toString());
        return map;
    }

    /**
     * Parses an RS TrackedResourceAmount to a lua object
     * This method assumes you did an instanceof check before that the {@link ResourceKey} is an {@link ItemResource}
     *
     * @param trackedResourceAmount the tracked resource amount containing an ItemResource
     * @return a Map containing the properties which CC can parse to a lua table
     */
    public static Map<String, Object> getObjectFromItemResource(ResourceAmount trackedResourceAmount) {
        ItemResource resource = (ItemResource) trackedResourceAmount.resource();
        long count = trackedResourceAmount.amount();
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
    public static Map<String, Object> getObjectFromItemResource(ResourceAmount trackedResourceAmount, long alternateCount) {
        Map<String, Object> properties = getObjectFromItemResource(trackedResourceAmount);
        properties.put("count", alternateCount);
        return properties;
    }

    /**
     * Parses an RS TrackedResourceAmount to a lua object
     * This method assumes you did an instanceof check before that the {@link ResourceKey} is an {@link ItemResource}
     *
     * @param trackedResourceAmount the tracked resource amount containing an ItemResource
     * @return a Map containing the properties which CC can parse to a lua table
     */
    public static Map<String, Object> getObjectFromFluidResource(ResourceAmount trackedResourceAmount) {
        FluidResource resource = (FluidResource) trackedResourceAmount.resource();
        long count = trackedResourceAmount.amount();
        FluidStack stack = VariantUtil.toFluidStack(resource, count);
        Map<String, Object> properties = LuaConverter.fluidStackToObject(stack, count);
        properties.put("fingerprint", FluidUtil.getFingerprint(stack));
        return properties;
    }

    /**
     * Parses an RS TrackedResourceAmount to a lua object
     * This method assumes you did an instanceof check before that the {@link ResourceKey} is an {@link ItemResource}
     *
     * @param trackedResourceAmount the tracked resource amount containing an ItemResource
     * @param alternateCount        a count can be passed to overwrite the count of the object. Useful for patterns and craftable stacks
     * @return a Map containing the properties which CC can parse to a lua table
     */
    public static Map<String, Object> getObjectFromFluidResource(ResourceAmount trackedResourceAmount, long alternateCount) {
        Map<String, Object> properties = getObjectFromFluidResource(trackedResourceAmount);
        properties.put("count", alternateCount);
        return properties;
    }

}
