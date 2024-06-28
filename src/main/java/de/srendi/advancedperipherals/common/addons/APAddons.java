package de.srendi.advancedperipherals.common.addons;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.List;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class APAddons {

    public static final String CURIOS_MODID = "curios";
    public static final String REFINEDSTORAGE_MODID = "refinedstorage";
    public static final String AE_THINGS_MODID = "ae2things";
    public static final String APPLIEDENERGISTICS_MODID = "ae2";
    public static final String MEKANISM_MODID = "mekanism";
    public static final String AE_ADDITIONS_MODID = "ae2additions";
    public static final String APP_MEKANISTICS_MODID = "appmek";

    public static boolean curiosLoaded;
    public static boolean refinedStorageLoaded;
    public static boolean aeThingsLoaded;
    public static boolean appliedEnergisticsLoaded;
    public static boolean mekanismLoaded;
    public static boolean aeAdditionsLoaded;
    public static boolean appMekLoaded;

    // Use static so these checks run as early as possible, so we can use them for our registries
    static {
        ModList modList = ModList.get();
        curiosLoaded = modList.isLoaded(CURIOS_MODID);
        refinedStorageLoaded = modList.isLoaded(REFINEDSTORAGE_MODID);
        appliedEnergisticsLoaded = modList.isLoaded(APPLIEDENERGISTICS_MODID);
        mekanismLoaded = modList.isLoaded(MEKANISM_MODID);
        aeThingsLoaded = modList.isLoaded(AE_THINGS_MODID);
        aeAdditionsLoaded = modList.isLoaded(AE_ADDITIONS_MODID);
        appMekLoaded = modList.isLoaded(APP_MEKANISTICS_MODID);

        if (refinedStorageLoaded)
            RefinedStorage.instance = new RefinedStorage();
    }

    @SubscribeEvent
    public static void interModComms(InterModEnqueueEvent event) {
        if (!curiosLoaded)
            return;

        InterModComms.sendTo(CURIOS_MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("glasses")
                        .size(1)
                        .icon(new ResourceLocation(AdvancedPeripherals.MOD_ID, "slot/empty_glasses_slot"))
                        .build());
    }

    public static ItemStack getCurioGlasses(Player player) {
        if (!curiosLoaded)
            return ItemStack.EMPTY;
        List<SlotResult> curioSlots = CuriosApi.getCuriosHelper().findCurios(player, "glasses");
        if (curioSlots.isEmpty())
            return ItemStack.EMPTY;

        return curioSlots.get(0).stack();
    }
}
