package de.srendi.advancedperipherals.common.addons;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Arrays;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public enum APAddon {

    AE2("ae2"),
    AE2_THINGS("ae2_things"),
    APP_MEKANISTICS("appmek"),
    CURIOS("curios"),
    MEKANISM("mekanism"),
    MINECOLONIES("minecolonies"),
    PATCHOULI("patchouli"),
    POWAH("powah"),
    REFINEDSTORAGE("refinedstorage"),
    REFINEDSTORAGE_MEKANISM("refinedstorage_mekanism_integration");

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

    /*@SubscribeEvent
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
        if (!vs2Loaded) {
            return false;
        }
        return ValkyrienSkies.isBlockOnShip(level, pos);
    }*/
}
