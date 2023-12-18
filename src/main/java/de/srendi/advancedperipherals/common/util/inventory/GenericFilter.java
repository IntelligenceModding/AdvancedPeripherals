package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.GenericStack;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public abstract class GenericFilter {

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
    public static Pair<? extends GenericFilter, String> parseGeneric(Map<?, ?> rawFilter) {
        // TODO: Add chemical filter support

        if (!rawFilter.containsKey("name")) {
            if (rawFilter.containsKey("type") && rawFilter.get("type") instanceof String type) {
                switch (type) {
                    case "item":
                        return ItemFilter.parse(rawFilter);
                    case "fluid":
                        return FluidFilter.parse(rawFilter);
                }
            }
            // If the filter does not contain a name or a type, which should never happen, but players are players, we will just
            // give the ItemFilter the task to parse the filter
            return ItemFilter.parse(rawFilter);
        }
        String name = rawFilter.get("name").toString();

        // Let's check in which registry this thing is
        if (ItemUtil.getRegistryEntry(name, ForgeRegistries.ITEMS) != null) {
            return ItemFilter.parse(rawFilter);
        } else if (ItemUtil.getRegistryEntry(name, ForgeRegistries.FLUIDS) != null) {
            return FluidFilter.parse(rawFilter);
        } else {
            // If the name is in neither of the registries, we will just return an empty filter
            return Pair.of(empty(), "NO_VALID_FILTER_TYPE");
        }

    }

    public abstract boolean isEmpty();

    public abstract boolean test(GenericStack genericStack);

    public static GenericFilter empty() {
        return new GenericFilter() {
            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public boolean test(GenericStack genericStack) {
                return false;
            }
        };
    }

}
