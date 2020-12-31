package de.srendi.advancedperipherals.common.configuration;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {

    @SubscribeEvent
    public static void configEvent(final ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == ConfigHolder.COMMON_SPEC) {
            ConfigHandler.bakeCommon();
        }
    }

    private static void bakeCommon() {
        AdvancedPeripheralsConfig.enableChatBox = ConfigHolder.COMMON_CONFIG.ENABLE_CHAT_BOX.get();
        AdvancedPeripheralsConfig.chatBoxCooldown = ConfigHolder.COMMON_CONFIG.CHAT_BOX_COOLDOWN.get();
    }
}
