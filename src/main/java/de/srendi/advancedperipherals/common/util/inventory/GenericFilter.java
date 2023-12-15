package de.srendi.advancedperipherals.common.util.inventory;

import de.srendi.advancedperipherals.common.util.Pair;

import java.util.Map;

public abstract class GenericFilter {

    // TODO: Imagine we want to filter for an object which can either a item, a fluid or maybe a chemical from mekanism
    // This function should first check if the `name` key can be found in any of the registries and then return the
    // right filter according to the registry type which could be an item or a chemical
    public static Pair<GenericFilter, String> parseGeneric(Map<?, ?> rawFilter) {
        return Pair.of(null, null);
    }

}
