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
    MISSING_ITEMS,
    CPU_DOES_NOT_EXIST,
    // Filters
    EMPTY_FILTER,
    FILTER_FLUID_NOT_FOUND,
    FILTER_ITEM_NOT_FOUND,
    NO_VALID_FLUID,
    NO_VALID_ITEM,
    NO_VALID_FROMSLOT,
    NO_VALID_TOSLOT,
    NO_VALID_NBT_HASH,
    NO_VALID_NBT,
    NO_VALID_FINGERPRINT,
    NO_VALID_COUNT,
    NO_VALID_FILTER_TYPE,
    // Inventory,
    INVENTORY_NOT_FOUND,
    ITEM_NOT_FOUND,
    FLUID_NOT_FOUND,
    CHEMICAL_NOT_FOUND,
    // Misc
    NOT_CONNECTED,
    UNKNOWN_ERROR;

    public String withInfo(String extraInfo) {
        return this + "_" + extraInfo;
    }

}
