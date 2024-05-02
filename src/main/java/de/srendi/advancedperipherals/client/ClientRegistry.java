package de.srendi.advancedperipherals.client;

import dan200.computercraft.api.client.ComputerCraftAPIClient;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.InventoryManagerScreen;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.ContainerTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistry {

    private static final String[] TURTLE_MODELS = new String[]{"turtle_chat_box_upgrade_left", "turtle_chat_box_upgrade_right", "turtle_environment_upgrade_left", "turtle_environment_upgrade_right", "turtle_player_upgrade_left", "turtle_player_upgrade_right", "turtle_geoscanner_upgrade_left", "turtle_geoscanner_upgrade_right"};

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (String model : TURTLE_MODELS) {
            event.register(new ModelResourceLocation(new ResourceLocation(AdvancedPeripherals.MOD_ID, model), "inventory"));
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ContainerTypes.INVENTORY_MANAGER_CONTAINER.get(), InventoryManagerScreen::new);

        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.CHUNKY_TURTLE.get(), TurtleUpgradeModeller.flatItem());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.COMPASS_TURTLE.get(), TurtleUpgradeModeller.flatItem());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.CHAT_BOX_TURTLE.get(), TurtleUpgradeModeller.sided(new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_chat_box_upgrade_left"), "inventory"), new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_chat_box_upgrade_right"), "inventory")));
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.ENVIRONMENT_TURTLE.get(), TurtleUpgradeModeller.sided(new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_environment_upgrade_left"), "inventory"), new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_environment_upgrade_right"), "inventory")));
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.GEO_SCANNER_TURTLE.get(), TurtleUpgradeModeller.sided(new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_geoscanner_upgrade_left"), "inventory"), new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_geoscanner_upgrade_right"), "inventory")));
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.PLAYER_DETECTOR_TURTLE.get(), TurtleUpgradeModeller.sided(new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_player_upgrade_left"), "inventory"), new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_player_upgrade_right"), "inventory")));
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.OP_END_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.OP_HUSBANDRY_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.OP_WEAK_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.HUSBANDRY_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.END_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.WEAK_TURTLE.get(), new MetaTurtleUpgradeModeller<>());
    }

    @SubscribeEvent
    public static void onClientSetup(RegisterKeyMappingsEvent event) {
        KeyBindings.register(event);
    }

    //TODO change the icon of the curio icon
    /*@SubscribeEvent
    public static void onTextureStitching(TextureStitchEvent.Pre event) {
        event.addSprite(new ResourceLocation(AdvancedPeripherals.MOD_ID, "item/empty_glasses_slot"));
    }*/
}
