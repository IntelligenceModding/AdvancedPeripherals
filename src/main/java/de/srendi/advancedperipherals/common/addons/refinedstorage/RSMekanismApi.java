package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RSMekanismApi {

    /**
     * Returns the first chemical parsed to a lua object which fits to the filter
     *
     * @param network refined storage network
     * @param filter  fluid filter instance - can be an empty filter to get the first fluid of the system see {@link FluidFilter#createEmpty()}
     * @return the first fluid in the system that fits the fluid filter or null
     */
    @Nullable
    public static ChemicalResource getChemical(Network network, ChemicalFilter filter) {
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (ResourceAmount resourceAmount : storage.getAll()) {
            if (resourceAmount.resource() instanceof ChemicalResource chemicalResource && filter.test(ChemicalUtil.toChemicalStack(chemicalResource.chemical(), resourceAmount.amount()))) {
                return chemicalResource;
            }
        }
        return null;
    }

    public static List<ChemicalResource> getChemicals(Network network, ChemicalFilter filter) {
        List<ChemicalResource> chemicals = new ArrayList<>();
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (ResourceAmount resourceAmount : storage.getAll()) {
            if (resourceAmount.resource() instanceof ChemicalResource chemicalResource && filter.test(ChemicalUtil.toChemicalStack(chemicalResource.chemical(), resourceAmount.amount()))) {
                chemicals.add(chemicalResource);
            }
        }
        return chemicals;
    }

    /**
     * Returns the first mekanism chemical parsed to a lua object which fits to the filter
     *
     * @param network refined storage network
     * @param filter  chemical filter instance - can be an empty filter to get the first chemical of the system see {@link ChemicalFilter#createEmpty()}
     * @return the first chemical in the system that fits the chemical filter or null
     */
    @Nullable
    public static Map<String, Object> getParsedChemical(Network network, ChemicalFilter filter) {
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        AutocraftingNetworkComponent autocrafting = network.getComponent(AutocraftingNetworkComponent.class);

        for (ResourceAmount resourceAmount : storage.getAll()) {
            if (resourceAmount.resource() instanceof ChemicalResource chemicalResource && filter.test(ChemicalUtil.toChemicalStack(chemicalResource.chemical(), resourceAmount.amount()))) {
                return getObjectFromChemicalResource(resourceAmount, autocrafting);
            }
        }
        return null;
    }

    /**
     * Returns every fluid from the system while also checking if the filter test passes for the fluids
     * The filter can be empty, see {@link FluidFilter#createEmpty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of fluid stacks
     */
    public static List<Map<String, Object>> getParsedChemicals(Network network, ChemicalFilter filter) {
        List<Map<String, Object>> items = new ArrayList<>();
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        AutocraftingNetworkComponent autocrafting = network.getComponent(AutocraftingNetworkComponent.class);

        for (ResourceAmount resourceAmount : storage.getAll()) {
            if (resourceAmount.resource() instanceof ChemicalResource chemicalResource && filter.test(ChemicalUtil.toChemicalStack(chemicalResource.chemical(), resourceAmount.amount()))) {
                items.add(getObjectFromChemicalResource(resourceAmount, autocrafting));
            }
        }

        return items;
    }

    /**
     * Returns every craftable mekanism chemical from the system while also checking if the filter test passes for the chemicals
     * The filter can be empty, see {@link ChemicalFilter#createEmpty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of parsed chemical stacks
     */
    public static List<Map<String, Object>> getCraftableChemicals(Network network, ChemicalFilter filter) {
        List<Map<String, Object>> items = new ArrayList<>();
        AutocraftingNetworkComponent autocrafting = network.getComponent(AutocraftingNetworkComponent.class);
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (ResourceKey key : autocrafting.getOutputs()) {
            long amount = storage.get(key);
            if (key instanceof ChemicalResource chemicalResource && filter.test(ChemicalUtil.toChemicalStack(chemicalResource.chemical(), amount))) {
                items.add(RSApi.getObjectFromResourceKey(key, amount, autocrafting));
            }
        }
        return items;
    }

    /**
     * Parses an RS TrackedResourceAmount to a lua object
     * This method assumes you did an instanceof check before that the {@link ResourceKey} is an {@link ChemicalResource}
     *
     * @param trackedResourceAmount the tracked resource amount containing a ChemicalResource
     * @return a Map containing the properties which CC can parse to a lua table
     */
    public static Map<String, Object> getObjectFromChemicalResource(ResourceAmount trackedResourceAmount, @Nullable AutocraftingNetworkComponent autocraftingComponent) {
        ChemicalResource resource = (ChemicalResource) trackedResourceAmount.resource();
        long count = trackedResourceAmount.amount();
        ChemicalStack stack = resourceToChemicalStack(resource, count);
        Map<String, Object> properties = LuaConverter.chemicalStackToLua(stack, count);
        properties.put("isCraftable", autocraftingComponent != null && !autocraftingComponent.getPatternsByOutput(trackedResourceAmount.resource()).isEmpty());
        return properties;
    }

    /**
     * Parses an RS TrackedResourceAmount to a lua object
     * This method assumes you did an instanceof check before that the {@link ResourceKey} is an {@link ChemicalResource}
     *
     * @param trackedResourceAmount the tracked resource amount containing a ChemicalResource
     * @param alternateCount        a count can be passed to overwrite the count of the object. Useful for patterns and craftable stacks
     * @return a Map containing the properties which CC can parse to a lua table
     */
    public static Map<String, Object> getObjectFromChemicalResource(ResourceAmount trackedResourceAmount, long alternateCount, @Nullable AutocraftingNetworkComponent autocraftingComponent) {
        Map<String, Object> properties = getObjectFromChemicalResource(trackedResourceAmount, autocraftingComponent);
        properties.put("count", alternateCount);
        return properties;
    }

    public static ChemicalStack resourceToChemicalStack(ResourceAmount resourceAmount) {
        if (resourceAmount.resource() instanceof ChemicalResource chemicalResource)
            return new ChemicalStack(MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(chemicalResource.chemical()), resourceAmount.amount());

        return ChemicalStack.EMPTY;
    }

    public static ChemicalStack resourceToChemicalStack(ChemicalResource resource, long alternateCount) {
        return new ChemicalStack(MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(resource.chemical()), alternateCount);
    }

    public static ChemicalStack resourceToChemicalStack(ChemicalResource resource) {
        return resourceToChemicalStack(resource, 1);
    }
}
