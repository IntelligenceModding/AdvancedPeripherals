package de.srendi.advancedperipherals.common.util;

/**
 * A collection of constants used as return types for several peripherals
 */
public enum StatusConstants {

    // Crafting Jobs
    CALCULATION_STARTED,
    CRAFTING_STARTED,
    JOB_CANCELED,
    JOB_DONE,
    NOT_CRAFTABLE,
    MISSING_ITEMS, // If there are missing items for a crafting recipe/job after the calculation is done
    CPU_NOT_FOUND,
    // Filters
    EMPTY_FILTER,
    FILTER_ITEM_NOT_FOUND, // Item could not be found in registry
    FILTER_FLUID_NOT_FOUND, // Fluid could not be found in registry
    FILTER_CHEMICAL_NOT_FOUND, // Chemical could not be found in registry
    MISSING_FILTER,
    NO_VALID_FLUID, // Fluid property of filter is not a string
    NO_VALID_ITEM, // Item property of filter is not a string
    NO_VALID_FROMSLOT,
    NO_VALID_TOSLOT,
    NO_VALID_NBT_HASH,
    NO_VALID_NBT,
    NO_VALID_FINGERPRINT,
    NO_VALID_COUNT,
    NO_VALID_FILTER_TYPE,
    // Inventory,
    INVENTORY_NOT_FOUND,
    TARGET_NOT_FOUND,
    ITEM_NOT_FOUND, // Debug message when an item couldn't be found in an/the target inventory
    FLUID_NOT_FOUND,
    CHEMICAL_NOT_FOUND,
    // Misc
    NOT_CONNECTED,
    NOT_FOUND, // Generic not found state
    ADDON_NOT_LOADED,
    UNKNOWN_ERROR;
}
