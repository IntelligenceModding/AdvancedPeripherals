package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.APCreativeTab;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CreativeTabs {

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AP_CREATIVE_MODE_TAB = Registration.CREATIVE_MODE_TABS.register(AdvancedPeripherals.MOD_ID, CreativeTabs::createCreativeTab);

    private static CreativeModeTab createCreativeTab() {
        CreativeModeTab.Builder builder = new CreativeModeTab.Builder(CreativeModeTab.Row.BOTTOM, -1);
        APCreativeTab.populateCreativeTabBuilder(builder);
        return builder.build();
    }
    public static void register() {

    }

}
