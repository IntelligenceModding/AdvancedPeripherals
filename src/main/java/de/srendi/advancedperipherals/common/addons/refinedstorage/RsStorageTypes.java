package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.common.api.storage.StorageType;
import com.refinedmods.refinedstorage.common.storage.StorageTypes;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;

/**
 * To better support third party RS addons and to prevent any jvm loading issues when third party addons are not loaded
 */
public enum RsStorageTypes {

    ITEM(StorageTypes.ITEM),
    FLUID(StorageTypes.FLUID),
    CHEMICAL(ChemicalResourceType.STORAGE_TYPE);

    private final StorageType storageType;

    RsStorageTypes(StorageType storageType) {
        this.storageType = storageType;
    }

    public StorageType getStorageType() {
        return storageType;
    }
}
