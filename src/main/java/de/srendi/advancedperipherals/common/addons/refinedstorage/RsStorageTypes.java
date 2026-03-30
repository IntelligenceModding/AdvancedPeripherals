package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.common.api.storage.StorageType;
import com.refinedmods.refinedstorage.common.storage.StorageTypes;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;
import de.srendi.advancedperipherals.common.addons.APAddon;

/**
 * To better support third party RS addons and to prevent any jvm loading issues when third party addons are not loaded
 */
public enum RsStorageTypes {
    ITEM {
        @Override
        public StorageType getStorageType() {
            return StorageTypes.ITEM;
        }
    },
    FLUID {
        @Override
        public StorageType getStorageType() {
            return StorageTypes.FLUID;
        }
    },
    CHEMICAL {
        @Override
        public StorageType getStorageType() {
            return APAddon.REFINEDSTORAGE_MEKANISM.isLoaded() ? ChemicalResourceType.STORAGE_TYPE : null;
        }
    };

    public abstract StorageType getStorageType();
}
