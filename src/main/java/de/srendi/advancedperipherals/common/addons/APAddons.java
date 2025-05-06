package de.srendi.advancedperipherals.common.addons;

import net.neoforged.fml.ModList;

public class APAddons {

    public static final String AE2_MODID = "ae2";
    public static final String CURIOS_MODID = "curios";
    public static final String REFINEDSTORAGE_MODID = "refinedstorage";
    public static final String REFINEDSTORAGE_MEKANISM_MODID = "refinedstorage_mekanism_integration";
    public static final String MEKANISM_MODID = "mekanism";
    public static final String APP_MEKANISTICS_MODID = "appmek";
    public static final String MINECOLONIES_MODID = "minecolonies";
    public static final String PATCHOULI_MODID = "patchouli";
    public static final String POWAH_MODID = "powah";

    // Used for our global helper lua api to gather the versions of our mod integrations.
    public static final String[] MOD_IDS = new String[]{AE2_MODID, CURIOS_MODID, REFINEDSTORAGE_MODID, REFINEDSTORAGE_MEKANISM_MODID, MEKANISM_MODID, APP_MEKANISTICS_MODID, MINECOLONIES_MODID, PATCHOULI_MODID, POWAH_MODID};

    public static boolean ae2Loaded;
    public static boolean curiosLoaded;
    public static boolean refinedStorageLoaded;
    public static boolean refinedStorageMekanismLoaded;
    public static boolean mekanismLoaded;
    public static boolean appMekLoaded;
    public static boolean patchouliLoaded;
    public static boolean powahLoaded;
    public static boolean minecoloniesLoaded;

    private APAddons() {
    }

    public static void setup() {
        ModList modList = ModList.get();
        ae2Loaded = modList.isLoaded(AE2_MODID);
        curiosLoaded = modList.isLoaded(CURIOS_MODID);
        refinedStorageLoaded = modList.isLoaded(REFINEDSTORAGE_MODID);
        refinedStorageMekanismLoaded = modList.isLoaded(REFINEDSTORAGE_MEKANISM_MODID);
        mekanismLoaded = modList.isLoaded(MEKANISM_MODID);
        appMekLoaded = modList.isLoaded(APP_MEKANISTICS_MODID);
        curiosLoaded = modList.isLoaded(CURIOS_MODID);
        minecoloniesLoaded = modList.isLoaded(MINECOLONIES_MODID);
        patchouliLoaded = modList.isLoaded(PATCHOULI_MODID);
        powahLoaded = modList.isLoaded(POWAH_MODID);
    }
}
