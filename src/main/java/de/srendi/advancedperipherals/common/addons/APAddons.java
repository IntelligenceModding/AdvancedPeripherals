package de.srendi.advancedperipherals.common.addons;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class APAddons {

    public static final String AE2_MODID = "ae2";
    public static final String CURIOS_MODID = "curios";
    public static final String REFINEDSTORAGE_MODID = "refinedstorage";
    public static final String APP_MEKANISTICS_MODID = "appmek";
    public static final String PATCHOULI_MODID = "patchouli";

    private APAddons() {

    }

    public static void commonSetup() {
        if (refinedStorageLoaded())
            RefinedStorage.instance = new RefinedStorage();
    }

    public static boolean ae2Loaded() {
        return ModList.get().isLoaded(AE2_MODID);
    }

    public static boolean curiosLoaded() {
        return ModList.get().isLoaded(CURIOS_MODID);
    }

    public static boolean refinedStorageLoaded() {
        return ModList.get().isLoaded(REFINEDSTORAGE_MODID);
    }

    public static boolean appMekLoaded() {
        return ModList.get().isLoaded(APP_MEKANISTICS_MODID);
    }

    public static boolean patchouliLoaded() {
        return ModList.get().isLoaded(PATCHOULI_MODID);
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
