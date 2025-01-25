package de.srendi.advancedperipherals.common.addons;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.List;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class APAddons {

    public static final String AE_ADDITIONS_MODID = "ae2additions";
    public static final String AE_THINGS_MODID = "ae2things";
    public static final String APPLIEDENERGISTICS_MODID = "ae2";
    public static final String APP_MEKANISTICS_MODID = "appmek";
    public static final String BOTANIA_MODID = "botania";
    public static final String CREATE_MODID = "create";
    public static final String CURIOS_MODID = "curios";
    public static final String DIMSTORAGE_MODID = "dimstorage";
    public static final String MEKANISM_MODID = "mekanism";
    public static final String POWAH_MODID = "powah";
    public static final String REFINEDSTORAGE_MODID = "refinedstorage";
    public static final String VALKYRIEN_SKIES_MODID = "valkyrienskies";

    public static boolean aeAdditionsLoaded;
    public static boolean aeThingsLoaded;
    public static boolean appMekLoaded;
    public static boolean appliedEnergisticsLoaded;
    public static boolean botaniaLoaded;
    public static boolean createLoaded;
    public static boolean curiosLoaded;
    public static boolean dimstorageLoaded;
    public static boolean mekanismLoaded;
    public static boolean powahLoaded;
    public static boolean refinedStorageLoaded;
    public static boolean vs2Loaded;

    // Use static so these checks run as early as possible, so we can use them for our registries
    static {
        ModList modList = ModList.get();
        aeAdditionsLoaded = modList.isLoaded(AE_ADDITIONS_MODID);
        aeThingsLoaded = modList.isLoaded(AE_THINGS_MODID);
        appMekLoaded = modList.isLoaded(APP_MEKANISTICS_MODID);
        appliedEnergisticsLoaded = modList.isLoaded(APPLIEDENERGISTICS_MODID);
        botaniaLoaded = modList.isLoaded(BOTANIA_MODID);
        createLoaded = modList.isLoaded(CREATE_MODID);
        curiosLoaded = modList.isLoaded(CURIOS_MODID);
        dimstorageLoaded = modList.isLoaded(DIMSTORAGE_MODID);
        mekanismLoaded = modList.isLoaded(MEKANISM_MODID);
        powahLoaded = modList.isLoaded(POWAH_MODID);
        refinedStorageLoaded = modList.isLoaded(REFINEDSTORAGE_MODID);
        vs2Loaded = modList.isLoaded(VALKYRIEN_SKIES_MODID);

        if (refinedStorageLoaded) {
            RefinedStorage.instance = new RefinedStorage();
        }
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

    public static boolean isBlockOnShip(Level level, BlockPos pos) {
        if (level == null || !vs2Loaded) {
            return false;
        }
        return VSGameUtilsKt.isBlockInShipyard(level, pos);
    }

    public static Ship getVS2Ship(Level level, BlockPos pos) {
        if (level == null || !vs2Loaded) {
            return null;
        }
        return VSGameUtilsKt.getShipObjectManagingPos(level, pos);
    }
}
