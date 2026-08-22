package de.srendi.advancedperipherals.common.addons;

import net.minecraftforge.fml.ModList;

import java.util.Arrays;

public enum APAddon {

    AE2("ae2"),
    AE2THINGS("ae2things"),
    CREATE("create"),
    CURIOS("curios"),
    DIMSTORAGE("dimstorage"),
    MEKANISM("mekanism"),
    MINECOLONIES("minecolonies"),
    PATCHOULI("patchouli"),
    POWAH("powah"),
    REFINEDSTORAGE("refinedstorage"),
    REFINEDSTORAGE_MEKANISM("refinedstorage_mekanism_integration"),
    VALKYRIENSKIES("valkyrienskies");

    private final String modId;
    private boolean loaded;

    APAddon(String modId) {
        this.modId = modId;
        this.loaded = false; // Default to false will be updated by setup()
    }

    public String getModId() {
        return modId;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public static void setup() {
        ModList modList = ModList.get();
        for (APAddon addon : values()) {
            addon.loaded = modList.isLoaded(addon.getModId());
        }
    }

    public static String[] getAllModIds() {
        return Arrays.stream(values())
                .map(APAddon::getModId)
                .toArray(String[]::new);
    }
}
