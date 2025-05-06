package de.srendi.advancedperipherals.common.addons.refinedstorage;

import com.refinedmods.refinedstorage.common.api.storage.StorageType;
import com.refinedmods.refinedstorage.common.storage.StorageTypes;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;
import de.srendi.advancedperipherals.common.addons.APAddons;

import java.util.function.Supplier;

/**
 * To better support third party RS addons and to prevent any jvm loading issues when third party addons are not loaded
 */
public enum RsStorageTypes {

    ITEM(() -> StorageTypes.ITEM),
    FLUID(() -> StorageTypes.FLUID),
    CHEMICAL(() -> {
        if (APAddons.refinedStorageMekanismLoaded)
            return ChemicalResourceType.STORAGE_TYPE;
        return null;
    });

    private final Supplier<StorageType> storageType;

    RsStorageTypes(Supplier<StorageType> storageType) {
        this.storageType = storageType;
    }

    public StorageType getStorageType() {
        return storageType.get();
    }
}
