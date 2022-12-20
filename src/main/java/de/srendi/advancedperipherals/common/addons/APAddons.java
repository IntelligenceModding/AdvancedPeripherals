package de.srendi.advancedperipherals.common.addons;

import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class APAddons {

    public static final String CURIOS_MODID = "curios";
    public static final String REFINEDSTORAGE_MODID = "refinedstorage";

    public boolean curiosLoaded;
    public boolean refinedStorageLoaded;

    public void commonSetup() {
        ModList modList = ModList.get();
        curiosLoaded = modList.isLoaded(CURIOS_MODID);
        refinedStorageLoaded = modList.isLoaded(REFINEDSTORAGE_MODID);

        if (refinedStorageLoaded) {
            RefinedStorage.instance = new RefinedStorage();
        }
    }

    public void interModComms(InterModEnqueueEvent event) {
        if (!curiosLoaded)
            return;

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("glasses").size(1).build());
    }
}
