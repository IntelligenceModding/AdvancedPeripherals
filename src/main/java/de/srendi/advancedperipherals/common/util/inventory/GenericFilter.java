package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.mekanism.Mekanism;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Map;

public abstract class GenericFilter<T> {

    private static final GenericFilter<?> EMPTY = new GenericFilter<>() {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean testAE(GenericStack genericStack) {
            return false;
        }

        @Override
        public boolean testRS(ResourceAmount resourceAmount) {
            return false;
        }

        @Override
        public boolean test(Object toTest) {
            return false;
        }
    };

    /**
     * Try to parse a raw filter table to any existing filter type. Could be a fluid filter, an item filter, maybe something else
     * in the future.
     * <p>
     * If the function can't find a valid type for the given name/resource location, it will return an empty filter with
     * a proper error message.
     *
     * @param rawFilter The raw filter, which is a map of strings and objects
     * @return A pair of the parsed filter and an error message, if there is one
     */
    public static Pair<? extends GenericFilter<?>, String> parseGeneric(Map<?, ?> rawFilter) {

        if (rawFilter.containsKey("type") && rawFilter.get("type") instanceof String type) {
            if (type.equals("item"))
                return ItemFilter.parse(rawFilter);
            if (type.equals("fluid"))
                return FluidFilter.parse(rawFilter);
            if (type.equals("chemical") && APAddon.MEKANISM.isLoaded())
                return ChemicalFilter.parse(rawFilter);
        }
        if (!rawFilter.containsKey("name"))
            return Pair.of(empty(), "NO_NAME_OR_TYPE");

        String name = rawFilter.get("name").toString();

        // Let's check in which registry this thing is
        if (ItemUtil.getRegistryEntry(name, BuiltInRegistries.ITEM) != null) {
            return ItemFilter.parse(rawFilter);
        } else if (ItemUtil.getRegistryEntry(name, BuiltInRegistries.FLUID) != null) {
            return FluidFilter.parse(rawFilter);
        } else if (APAddon.MEKANISM.isLoaded() && ItemUtil.getRegistryEntry(name, Mekanism.getChemicalRegistry()) != null) {
            return ChemicalFilter.parse(rawFilter);
        } else {
            // If the name is in neither of the registries, we will just return an empty filter
            return Pair.of(empty(), "NO_VALID_FILTER_TYPE");
        }
    }

    public abstract boolean isEmpty();

    // AE2 stuff
    public abstract boolean testAE(GenericStack genericStack);

    // RS stuff
    public abstract boolean testRS(ResourceAmount resourceAmount);

    public abstract boolean test(T toTest);

    public static GenericFilter<?> empty() {
        return EMPTY;
    }

}
