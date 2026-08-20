package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.mekanism.Mekanism;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.RegistryUtil;
import net.minecraft.core.registries.BuiltInRegistries;

public abstract class GenericFilter<T> {

    private static final GenericFilter EMPTY = new GenericFilter() {
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

        @Override
        public GenericFilter copy() {
            return this;
        }
    };

    public static <T> GenericFilter<T> empty() {
        return (GenericFilter<T>) EMPTY;
    }

    /**
     * Try to parse a raw filter table to any existing filter type. Could be a fluid filter, an item filter, maybe something else
     * in the future.
     * <p>
     * If the function can't find a valid type for the given name/resource location, it will return an empty filter with
     * a proper error message.
     *
     * @param rawFilter The raw filter, which is a map of strings and objects
     * @return A pair of the parsed filter and an error message, if there is one
     * @throws LuaException If the filter table has incorrect format
     */
    public static Pair<? extends GenericFilter<?>, String> parseGeneric(LuaTable<?, ?> rawFilter) throws LuaException {
        if (rawFilter.containsKey("type")) {
            String type = rawFilter.getString("type");
            return switch (type) {
                case "item" -> ItemFilter.parse(rawFilter);
                case "fluid" -> FluidFilter.parse(rawFilter);
                default -> throw new LuaException("unexpected filter type " + type);
            };
        }
        if (!rawFilter.containsKey("name"))
            throw new LuaException("Generic filter requires either field \"type\" or \"name\"");

        String name = rawFilter.getString("name");

        // Let's check in which registry this thing is
        if (RegistryUtil.getRegistryEntry(name, BuiltInRegistries.ITEM) != null) {
            return ItemFilter.parse(rawFilter);
        } else if (RegistryUtil.getRegistryEntry(name, BuiltInRegistries.FLUID) != null) {
            return FluidFilter.parse(rawFilter);
        }
        // If the name is in neither of the registries, we will just return an empty filter
        return Pair.of(empty(), "NO_VALID_FILTER_TYPE");
    }

    public abstract boolean isEmpty();

    // AE2 stuff
    public abstract boolean testAE(GenericStack genericStack);

    // RS stuff
    public abstract boolean testRS(ResourceAmount resourceAmount);

    public abstract boolean test(T toTest);

    public abstract GenericFilter<T> copy();
}
