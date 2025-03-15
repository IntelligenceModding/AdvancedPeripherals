package de.srendi.advancedperipherals.common.addons;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class APAddons {

    public static final String AE2_MODID = "ae2";
    public static final String APP_MEKANISTICS_MODID = "appmek";
    public static final String CURIOS_MODID = "curios";
    public static final String MINECOLONIES_MODID = "minecolonies";
    public static final String PATCHOULI_MODID = "patchouli";
    public static final String POWAH_MODID = "powah";
    public static final String REFINEDSTORAGETWO_MODID = "refinedstorage2";

    public static boolean ae2Loaded;
    public static boolean curiosLoaded;
    public static boolean refinedStorageLoaded;
    public static boolean appMekLoaded;
    public static boolean patchouliLoaded;
    public static boolean powahLoaded;
    public static boolean minecoloniesLoaded;

    private APAddons() {
    }

    public static void setup() {
        ModList modList = ModList.get();
        ae2Loaded = modList.isLoaded(AE2_MODID);
        appMekLoaded = modList.isLoaded(APP_MEKANISTICS_MODID);
        curiosLoaded = modList.isLoaded(CURIOS_MODID);
        minecoloniesLoaded = modList.isLoaded(MINECOLONIES_MODID);
        patchouliLoaded = modList.isLoaded(PATCHOULI_MODID);
        powahLoaded = modList.isLoaded(POWAH_MODID);
        refinedStorageLoaded = modList.isLoaded(REFINEDSTORAGETWO_MODID);
    }

    @SubscribeEvent
    public static void interModComms(InterModEnqueueEvent event) {
        /*
        if (!curiosLoaded) {
        }

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("glasses").size(1).build());
        */
    }
}
