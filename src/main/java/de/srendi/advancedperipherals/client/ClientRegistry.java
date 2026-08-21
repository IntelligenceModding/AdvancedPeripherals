package de.srendi.advancedperipherals.client;

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.renderer.AERendererRegister;
import de.srendi.advancedperipherals.client.renderer.DistanceDetectorRenderer;
import de.srendi.advancedperipherals.client.screens.InventoryManagerScreen;
import de.srendi.advancedperipherals.client.screens.KeyboardScreen;
import de.srendi.advancedperipherals.client.screens.SaddleTurtleOverlay;
import de.srendi.advancedperipherals.client.screens.SmartGlassesScreen;
import de.srendi.advancedperipherals.client.smartglasses.OverlayModuleOverlay;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistry {

    public static final SaddleTurtleOverlay SADDLE_TURTLE_OVERLAY = new SaddleTurtleOverlay();
    public static final OverlayModuleOverlay OVERLAY_MODULE_OVERLAY = new OverlayModuleOverlay();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        menuRegister();

        ItemPropertiesRegistry.register();
        if (APAddon.AE2.isLoaded()) {
            AERendererRegister.register();
        }
    }

    public static void menuRegister() {
        MenuScreens.register(APContainerTypes.INVENTORY_MANAGER_CONTAINER.get(), InventoryManagerScreen::new);
        MenuScreens.register(APContainerTypes.KEYBOARD_CONTAINER.get(), KeyboardScreen::new);
        MenuScreens.register(APContainerTypes.SMART_GLASSES_CONTAINER.get(), SmartGlassesScreen::new);
    }

    @SubscribeEvent
    public static void onUpgradeModeller(RegisterTurtleModellersEvent event) {
        event.register(CCRegistration.BLOCK_READER_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_block_reader_left"), AdvancedPeripherals.getRL("block/turtle_block_reader_right")));
        event.register(CCRegistration.CHAT_BOX_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_chatty_left"), AdvancedPeripherals.getRL("block/turtle_chatty_right")));
        event.register(CCRegistration.CHUNKY_TURTLE.get(), TurtleUpgradeModeller.flatItem());
        event.register(CCRegistration.COMPASS_TURTLE.get(), TurtleUpgradeModeller.flatItem());
        event.register(CCRegistration.END_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.ENVIRONMENT_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_environment_left"), AdvancedPeripherals.getRL("block/turtle_environment_right")));
        event.register(CCRegistration.GEO_SCANNER_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_geoscanner_left"), AdvancedPeripherals.getRL("block/turtle_geoscanner_right")));
        event.register(CCRegistration.HUSBANDRY_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.OP_END_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.OP_HUSBANDRY_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.OP_WEAK_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.PLAYER_DETECTOR_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_player_left"), AdvancedPeripherals.getRL("block/turtle_player_right")));
        event.register(CCRegistration.SADDLE_TURTLE.get(), TurtleUpgradeModeller.flatItem());
        event.register(CCRegistration.WEAK_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
    }

    @SubscribeEvent
    public static void registeringKeymappings(RegisterKeyMappingsEvent event) {
        KeyBindings.register(event);
    }

    @SubscribeEvent
    public static void registeringRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(APBlockEntityTypes.DISTANCE_DETECTOR.get(), DistanceDetectorRenderer::new);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registeringOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(SaddleTurtleOverlay.ID.toString(), SADDLE_TURTLE_OVERLAY);
        event.registerAboveAll(OverlayModuleOverlay.ID.toString(), OVERLAY_MODULE_OVERLAY);
    }
}
