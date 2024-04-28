package de.srendi.advancedperipherals.client;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT)
public class ClientEventSubscriber {
    @SubscribeEvent
    public static void renderingHuds(RenderGuiOverlayEvent.Pre event) {
        if (ClientRegistry.SADDLE_TURTLE_HUD.shouldRender() && event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) {
            event.setCanceled(true);
            return;
        }
    }
}
