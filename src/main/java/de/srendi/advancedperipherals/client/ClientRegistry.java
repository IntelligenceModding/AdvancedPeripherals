package de.srendi.advancedperipherals.client;

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.renderer.DistanceDetectorRenderer;
import de.srendi.advancedperipherals.client.screens.InventoryManagerScreen;
import de.srendi.advancedperipherals.client.screens.KeyboardScreen;
import de.srendi.advancedperipherals.client.screens.SaddleTurtleOverlay;
import de.srendi.advancedperipherals.client.screens.SmartGlassesScreen;
import de.srendi.advancedperipherals.client.smartglasses.OverlayModuleOverlay;
import de.srendi.advancedperipherals.client.smartglasses.OverlayObjectHolder;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.setup.APContainerTypes;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT)
public class ClientRegistry {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemPropertiesRegistry.register();
        OverlayObjectHolder.registerDecodeObjects();
    }

    @SubscribeEvent
    public static void menuRegister(RegisterMenuScreensEvent event) {
        event.register(APContainerTypes.INVENTORY_MANAGER_CONTAINER.get(), InventoryManagerScreen::new);
        event.register(APContainerTypes.KEYBOARD_CONTAINER.get(), KeyboardScreen::new);
        event.register(APContainerTypes.SMART_GLASSES_CONTAINER.get(), SmartGlassesScreen::new);
    }

    @SubscribeEvent
    public static void onUpgradeModeller(RegisterTurtleModellersEvent event) {
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
    public static void onUpgradeModeller(RegisterTurtleModellersEvent event) {
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
    public static void registeringOverlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(SaddleTurtleOverlay.ID, SADDLE_TURTLE_OVERLAY);
        event.registerAboveAll(OverlayModuleOverlay.ID, OVERLAY_MODULE_OVERLAY);
    }
}
