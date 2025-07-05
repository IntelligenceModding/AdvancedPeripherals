package de.srendi.advancedperipherals.client;

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.InventoryManagerScreen;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.ContainerTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT)
public class ClientRegistry {

    @SubscribeEvent
    public static void menuRegister(RegisterMenuScreensEvent event) {
        event.register(ContainerTypes.INVENTORY_MANAGER_CONTAINER.get(), InventoryManagerScreen::new);
    }

    @SubscribeEvent
    public static void onUpgradeModeller(RegisterTurtleModellersEvent event) {
        event.register(CCRegistration.CHUNKY_TURTLE.get(), TurtleUpgradeModeller.flatItem());
        event.register(CCRegistration.COMPASS_TURTLE.get(), TurtleUpgradeModeller.flatItem());
        event.register(CCRegistration.CHAT_BOX_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_chatty_left"), AdvancedPeripherals.getRL("block/turtle_chatty_right")));
        event.register(CCRegistration.ENVIRONMENT_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_environment_left"), AdvancedPeripherals.getRL("block/turtle_environment_right")));
        event.register(CCRegistration.GEO_SCANNER_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_geoscanner_left"), AdvancedPeripherals.getRL("block/turtle_geoscanner_right")));
        event.register(CCRegistration.PLAYER_DETECTOR_TURTLE.get(), TurtleUpgradeModeller.sided(AdvancedPeripherals.getRL("block/turtle_player_left"), AdvancedPeripherals.getRL("block/turtle_player_right")));
        event.register(CCRegistration.OP_END_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.OP_HUSBANDRY_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.OP_WEAK_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.HUSBANDRY_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.END_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        event.register(CCRegistration.WEAK_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
    }

    @SubscribeEvent
    public static void onClientSetup(RegisterKeyMappingsEvent event) {
        KeyBindings.register(event);
    }
}
