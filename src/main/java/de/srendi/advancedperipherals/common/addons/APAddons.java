package de.srendi.advancedperipherals.common.addons;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class APAddons {

    public static final String CURIOS_MODID = "curios";
    public static final String REFINEDSTORAGE_MODID = "refinedstorage";
    public static final String AE_THINGS_MODID = "ae2things";

    public static boolean curiosLoaded;
    public static boolean refinedStorageLoaded;
    public static boolean aeThingsLoaded;

    private APAddons() {
    }

    public static void commonSetup() {
        ModList modList = ModList.get();
        curiosLoaded = modList.isLoaded(CURIOS_MODID);
        refinedStorageLoaded = modList.isLoaded(REFINEDSTORAGE_MODID);
        aeThingsLoaded = modList.isLoaded(AE_THINGS_MODID);

        if (refinedStorageLoaded) {
            RefinedStorage.instance = new RefinedStorage();
        }
    }

    @SubscribeEvent
    public static void interModComms(InterModEnqueueEvent event) {
        if (!curiosLoaded)
            return;

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("glasses").size(1).build());
    }
}
