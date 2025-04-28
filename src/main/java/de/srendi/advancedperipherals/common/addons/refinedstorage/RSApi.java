package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.api.autocrafting.Ingredient;
import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.network.impl.node.externalstorage.ExposedExternalStorage;
import com.refinedmods.refinedstorage.api.network.impl.node.externalstorage.ExternalStorageNetworkNode;
import com.refinedmods.refinedstorage.api.network.impl.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.api.network.impl.storage.StorageConfiguration;
import com.refinedmods.refinedstorage.api.network.node.GraphNetworkComponent;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.StateTrackedStorage;
import com.refinedmods.refinedstorage.api.storage.Storage;
import com.refinedmods.refinedstorage.api.storage.TrackedResourceAmount;
import com.refinedmods.refinedstorage.api.storage.composite.CompositeStorage;
import com.refinedmods.refinedstorage.common.api.storage.SerializableStorage;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.storage.StorageTypes;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.blocks.blockentities.RSBridgeEntity;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.FluidUtil;
import de.srendi.advancedperipherals.common.util.inventory.GenericFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
public class RSApi {

    public static void registerCapabilities(@NotNull RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                BlockEntityTypes.RS_BRIDGE.get(),
                (blockEntity, side) -> blockEntity);
    }

    /**
     * Returns the first item resource that fits to the filter
     *
     * @param network refined storage network
     * @param filter  item filter instance - can be an empty filter to get the first item of the system see {@link ItemFilter#createEmpty()}
     * @return the first item in the system that fits the item filter or null
     */
    @Nullable
    public static ItemResource getItem(Network network, ItemFilter filter) {
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof ItemResource itemResource && filter.test(itemResource.toItemStack())) {
                return itemResource;
            }
        }
        return null;
    }

    /**
     * Returns the first fluid parsed to a lua object which fits to the filter
     *
     * @param network refined storage network
     * @param filter  fluid filter instance - can be an empty filter to get the first fluid of the system see {@link FluidFilter#createEmpty()}
     * @return the first fluid in the system that fits the fluid filter or null
     */
    @Nullable
    public static FluidResource getFluid(Network network, FluidFilter filter) {
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof FluidResource fluidResource && filter.test(VariantUtil.toFluidStack(fluidResource, trackedResource.resourceAmount().amount()))) {
                return fluidResource;
            }
        }
        return null;
    }

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
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof ChemicalResource chemicalResource && filter.test(ChemicalUtil.toChemicalStack(chemicalResource.chemical(), trackedResource.resourceAmount().amount()))) {
                return chemicalResource;
            }
        }
        return null;
    }

    /**
     * Returns the first item parsed to a lua object which fits to the filter
     *
     * @param network refined storage network
     * @param filter  item filter instance - can be an empty filter to get the first item of the system see {@link ItemFilter#createEmpty()}
     * @return the first item in the system that fits the item filter or null
     */
    @Nullable
    public static Map<String, Object> getParsedItem(Network network, ItemFilter filter) {
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
     * @param filter  fluid filter instance - can be an empty filter to get the first fluid of the system see {@link FluidFilter#createEmpty()}
     * @return the first fluid in the system that fits the fluid filter or null
     */
    @Nullable
    public static Map<String, Object> getParsedFluid(Network network, FluidFilter filter) {
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof FluidResource fluidResource && filter.test(VariantUtil.toFluidStack(fluidResource, trackedResource.resourceAmount().amount()))) {
                return getObjectFromItemResource(trackedResource.resourceAmount());
            }
        }
        return null;
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
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof ChemicalResource chemicalResource && filter.test(ChemicalUtil.toChemicalStack(chemicalResource.chemical(), trackedResource.resourceAmount().amount()))) {
                return getObjectFromItemResource(trackedResource.resourceAmount());
            }
        }
        return null;
    }

    /**
     * Returns every item from the system while also checking if the filter test passes for the items
     * The filter can be empty, see {@link ItemFilter#createEmpty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of items
     */
    public static Set<Map<String, Object>> getParsedItems(Network network, ItemFilter filter) {
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
     * The filter can be empty, see {@link FluidFilter#createEmpty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of fluid stacks
     */
    public static Set<Map<String, Object>> getParsedFluids(Network network, FluidFilter filter) {
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
     * Returns every fluid from the system while also checking if the filter test passes for the fluids
     * The filter can be empty, see {@link FluidFilter#createEmpty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of fluid stacks
     */
    public static Set<Map<String, Object>> getParsedChemicals(Network network, ChemicalFilter filter) {
        Set<Map<String, Object>> items = new HashSet<>();
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (TrackedResourceAmount trackedResource : storage.getResources(Actor.EMPTY.getClass())) {
            if (trackedResource.resourceAmount().resource() instanceof ChemicalResource fluidResource && filter.test(ChemicalUtil.toChemicalStack(fluidResource.chemical(), trackedResource.resourceAmount().amount()))) {
                items.add(getObjectFromFluidResource(trackedResource.resourceAmount()));
            }
        }

        return items;
    }

    /**
     * Returns every craftable item from the system while also checking if the filter test passes for the items
     * The filter can be empty, see {@link ItemFilter#createEmpty()}
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
     * The filter can be empty, see {@link FluidFilter#createEmpty()}
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

    /**
     * Returns every craftable mekanism chemical from the system while also checking if the filter test passes for the chemicals
     * The filter can be empty, see {@link ChemicalFilter#createEmpty()}
     *
     * @param network the rs network
     * @param filter  The filter here is optional, if an empty filter is provided, the method will return every resource
     * @return a set of parsed chemical stacks
     */
    public static Set<Map<String, Object>> getCraftableChemicals(Network network, ChemicalFilter filter) {
        Set<Map<String, Object>> items = new HashSet<>();
        AutocraftingNetworkComponent autocrafting = network.getComponent(AutocraftingNetworkComponent.class);
        StorageNetworkComponent storage = network.getComponent(StorageNetworkComponent.class);
        for (ResourceKey key : autocrafting.getOutputs()) {
            long amount = storage.get(key);
            if (key instanceof ChemicalResource chemicalResource && filter.test(ChemicalUtil.toChemicalStack(chemicalResource.chemical(), amount))) {
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

    public static Set<Object> listCells(Network network) {
        Set<Object> disks = new HashSet<>();

        GraphNetworkComponent graphNetworkComponent = network.getComponent(GraphNetworkComponent.class);
        for (InWorldNetworkNodeContainer nodeContainer : graphNetworkComponent.getContainers(InWorldNetworkNodeContainer.class)) {
            if (!(nodeContainer.getNode() instanceof StorageNetworkNode storageNetworkNode)) {
                continue;
            }
            CompositeStorage storage = (CompositeStorage) storageNetworkNode.getStorage();
            for (Storage disk : storage.getSources()) {
                if (disk instanceof StateTrackedStorage stateTrackedStorage) {
                    disks.add(parseStorageDisk(stateTrackedStorage));
                }
            }
        }
        return disks;
    }

    public static Set<Object> listDrives(Network network) {
        Set<Object> drives = new HashSet<>();

        GraphNetworkComponent graphNetworkComponent = network.getComponent(GraphNetworkComponent.class);
        for (InWorldNetworkNodeContainer nodeContainer : graphNetworkComponent.getContainers(InWorldNetworkNodeContainer.class)) {
            if (nodeContainer.getNode() instanceof StorageNetworkNode storageNetworkNode) {
                drives.add(parseDiskDrive(storageNetworkNode, nodeContainer));
            }
        }
        return drives;
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
        AutocraftingNetworkComponent autocraftingComponent = network.getComponent(AutocraftingNetworkComponent.class);
        for (Pattern pattern : autocraftingComponent.getPatterns()) {
            if (pattern.layout().ingredients().isEmpty())
                continue;
            if (pattern.layout().outputs().isEmpty())
                continue;

            boolean inputMatch = false;
            boolean outputMatch = false;

            if (inputFilter != null) {
                OUTERLOOP:
                for (Ingredient input : pattern.layout().ingredients()) {
                    for (ResourceKey possibleInput : input.inputs()) {
                        if (inputFilter.testRS(new ResourceAmount(possibleInput, 1))) {
                            inputMatch = true;
                            break OUTERLOOP;
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

    public static long getTotalStorage(Network network, RsStorageTypes type) {
        long total = 0;

        GraphNetworkComponent graphNetworkComponent = network.getComponent(GraphNetworkComponent.class);
        for (InWorldNetworkNodeContainer nodeContainer : graphNetworkComponent.getContainers(InWorldNetworkNodeContainer.class)) {
            if (nodeContainer.getNode() instanceof StorageNetworkNode storageNetworkNode) {
                CompositeStorage storage = (CompositeStorage) storageNetworkNode.getStorage();
                for (Storage disk : storage.getSources()) {
                    if (!(disk instanceof StateTrackedStorage stateTrackedStorage))
                        continue;

                    if (stateTrackedStorage.getDelegate() instanceof SerializableStorage serializableStorage) {
                        if (type.getStorageType() != null && serializableStorage.getType() != type.getStorageType())
                            continue;
                    }

                    total += stateTrackedStorage.getCapacity();
                }
            }
        }
        return total;
    }

    public static long getUsedStorage(Network network, RsStorageTypes type) {
        long used = 0;

        GraphNetworkComponent graphNetworkComponent = network.getComponent(GraphNetworkComponent.class);
        for (InWorldNetworkNodeContainer nodeContainer : graphNetworkComponent.getContainers(InWorldNetworkNodeContainer.class)) {
            if (nodeContainer.getNode() instanceof StorageNetworkNode storageNetworkNode) {
                CompositeStorage storage = (CompositeStorage) storageNetworkNode.getStorage();
                for (Storage disk : storage.getSources()) {
                    if (!(disk instanceof StateTrackedStorage stateTrackedStorage))
                        continue;

                    if (stateTrackedStorage.getDelegate() instanceof SerializableStorage serializableStorage) {
                        if (type.getStorageType() != null && serializableStorage.getType() != type.getStorageType())
                            continue;
                    }

                    used += stateTrackedStorage.getStored();
                }
            }
        }
        return used;
    }

    public static long getTotalExternalStorage(Network network, RsStorageTypes type) {
        long total = 0;

        GraphNetworkComponent graphNetworkComponent = network.getComponent(GraphNetworkComponent.class);
        for (InWorldNetworkNodeContainer nodeContainer : graphNetworkComponent.getContainers(InWorldNetworkNodeContainer.class)) {
            if (nodeContainer.getNode() instanceof ExternalStorageNetworkNode storageNetworkNode) {
                ExposedExternalStorage storage = (ExposedExternalStorage) storageNetworkNode.getStorage();
                // TODO - Waiting for next rs beta/milestone patch
            }

        }
        return total;
    }

    public static long getUsedExternalStorage(Network network, RsStorageTypes type) {
        long used = 0;

        GraphNetworkComponent graphNetworkComponent = network.getComponent(GraphNetworkComponent.class);
        for (InWorldNetworkNodeContainer nodeContainer : graphNetworkComponent.getContainers(InWorldNetworkNodeContainer.class)) {
            if (nodeContainer.getNode() instanceof ExternalStorageNetworkNode storageNetworkNode) {
                ExposedExternalStorage storage = (ExposedExternalStorage) storageNetworkNode.getStorage();

                used += storage.getAll().stream().filter(amount -> type.getStorageType() != null && type.getStorageType().isAllowed(amount.resource())).mapToLong(ResourceAmount::amount).sum();
            }

        }
        return used;
    }

    public static List<Object> getCraftingTasks(RSBridgeEntity entity) {
        List<Object> tasks = new ArrayList<>();

        for (RSCraftJob task : entity.getJobs()) {
            tasks.add(parseCraftingTask(task));
        }

        return tasks;
    }

    public static long getEnergyUsage(Network network) {
        long energyUsage = 0;

        GraphNetworkComponent graphNetworkComponent = network.getComponent(GraphNetworkComponent.class);
        for (InWorldNetworkNodeContainer nodeContainer : graphNetworkComponent.getContainers(InWorldNetworkNodeContainer.class)) {
            if (nodeContainer.getNode() instanceof AbstractNetworkNode abstractNetworkNode) {
                energyUsage += abstractNetworkNode.getEnergyUsage();
            }
        }

        return energyUsage;
    }

    public static Map<String, Object> getObjectFromResourceKey(@NotNull ResourceKey resource) {
        return getObjectFromResourceKey(resource, 0);
    }

    /**
     * Helper method to get a parsed lua resource key.
     *
     * @param resource the resource
     * @param count    count of the resource - can be 0 and lower
     * @return the parsed key to a lua properties map
     */
    public static Map<String, Object> getObjectFromResourceKey(@NotNull ResourceKey resource, long count) {
        // Resource amounts can't be zero or the client will just crash,
        // So we need to check that and set it to one
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
        if (APAddons.refinedStorageMekanismLoaded && resource instanceof ChemicalResource) {
            if (countZeroOrLower) {
                return getObjectFromChemicalResource(new ResourceAmount(resource, 1), count);
            }
            return getObjectFromChemicalResource(new ResourceAmount(resource, count));
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
    public static Map<String, Object> getObjectFromResourceAmount(@NotNull ResourceAmount resourceAmount) {
        if (resourceAmount.resource() instanceof ItemResource) {
            return getObjectFromItemResource(resourceAmount);
        }
        if (resourceAmount.resource() instanceof FluidResource) {
            return getObjectFromFluidResource(resourceAmount);
        }
        if (APAddons.refinedStorageMekanismLoaded && resourceAmount.resource() instanceof ChemicalResource) {
            return getObjectFromChemicalResource(resourceAmount);
        }
        AdvancedPeripherals.debug("Could not create table from unknown resourceAmount " + resourceAmount.getClass() + " - Report this to the maintainer of ap", Level.WARN);
        return Collections.emptyMap();
    }

    public static Object parseDiskDrive(StorageNetworkNode diskDrive, InWorldNetworkNodeContainer nodeContainer) {
        Map<String, Object> properties = new HashMap<>();

        StorageConfiguration storageConfiguration = diskDrive.getStorageConfiguration();
        CompositeStorage storage = (CompositeStorage) diskDrive.getStorage();

        Set<Object> disks = new HashSet<>();
        for (Storage disk : storage.getSources()) {
            if (!(disk instanceof StateTrackedStorage stateTrackedStorage))
                continue;

            disks.add(parseStorageDisk(stateTrackedStorage));
        }

        properties.put("used", diskDrive.getStored());
        properties.put("total", diskDrive.getCapacity());
        properties.put("disks", disks);
        properties.put("mode", storageConfiguration.getFilterMode().toString());
        properties.put("access_type", storageConfiguration.getAccessMode().toString());
        properties.put("position", LuaConverter.posToObject(nodeContainer.getLocalPosition()));
        properties.put("priority", nodeContainer.getPriority());

        return properties;
    }

    public static Object parseStorageDisk(StateTrackedStorage disk) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("used", disk.getStored());
        properties.put("capacity", disk.getCapacity());
        properties.put("state", disk.getState().toString());
        if (disk.getDelegate() instanceof SerializableStorage serializableStorage) {
            String type;
            if (serializableStorage.getType() == StorageTypes.ITEM) {
                type = "item";
            } else if (serializableStorage.getType() == StorageTypes.FLUID) {
                type = "fluid";
            } else if (APAddons.refinedStorageMekanismLoaded && serializableStorage.getType() == ChemicalResourceType.STORAGE_TYPE) {
                type = "chemical";
            } else {
                type = "unknown";
            }
            properties.put("type", type);
        }

        return properties;
    }

    public static Object parseCraftingTask(RSCraftJob task) {
        Map<String, Object> properties = new HashMap<>();

        TaskStatus status = task.getCraftingTask();

        properties.put("bridge_id", task.getId());
        properties.put("id", status.info().id().toString());
        properties.put("quantity", status.info().amount());
        properties.put("completion", status.percentageCompleted());

        return properties;
    }

    public static Object parsePattern(Pattern pattern) {
        if (pattern == null)
            return null;

        Map<String, Object> propeties = new HashMap<>();
        propeties.put("outputs", pattern.layout().outputs().stream().map(RSApi::getObjectFromResourceAmount).toList());

        List<List<Map<String, Object>>> inputs = pattern.layout().ingredients().stream()
                .map(ingredient -> ingredient.inputs().stream()
                        .map(key -> getObjectFromResourceKey(key, ingredient.amount()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        propeties.put("inputs", inputs);
        propeties.put("patterntype", pattern.layout().type().toString());
        propeties.put("id", pattern.id().toString());
        return propeties;
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


    /**
     * Parses an RS TrackedResourceAmount to a lua object
     * This method assumes you did an instanceof check before that the {@link ResourceKey} is an {@link ChemicalResource}
     *
     * @param trackedResourceAmount the tracked resource amount containing a ChemicalResource
     * @return a Map containing the properties which CC can parse to a lua table
     */
    public static Map<String, Object> getObjectFromChemicalResource(ResourceAmount trackedResourceAmount) {
        ChemicalResource resource = (ChemicalResource) trackedResourceAmount.resource();
        long count = trackedResourceAmount.amount();
        ChemicalStack stack = resourceToChemicalStack(resource, count);
        Map<String, Object> properties = LuaConverter.chemicalStackToObject(stack, count);
        properties.put("fingerprint", ChemicalUtil.getFingerprint(stack));
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
    public static Map<String, Object> getObjectFromChemicalResource(ResourceAmount trackedResourceAmount, long alternateCount) {
        Map<String, Object> properties = getObjectFromChemicalResource(trackedResourceAmount);
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
