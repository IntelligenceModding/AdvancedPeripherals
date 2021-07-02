package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketChatBox;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketEnvironment;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketGeoScanner;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketPlayerDetector;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.*;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRegistration {

    public static TurtleChatBox chatBox;
    public static TurtleEnvironmentDetector environmentDetector;
    public static TurtlePlayerDetector playerDetector;
    public static TurtleChunky chunky;
    public static TurtleGeoScanner geoScanner;
    public static TurtleWeakMechanicSoul kinetic;

    public static PocketEnvironment environmentPocket;
    public static PocketChatBox chatPocket;
    public static PocketPlayerDetector playerPocket;
    public static PocketGeoScanner geoScannerPocket;

    public static IntegrationPeripheralProvider integrationPeripheralProvider;

    public static void register() {
        registerPocketUpgrades();
        registerTurtleUpgrades();
        integrationPeripheralProvider = new IntegrationPeripheralProvider();
        integrationPeripheralProvider.register();
        ComputerCraftAPI.registerPeripheralProvider(integrationPeripheralProvider);
    }

    private static void registerPocketUpgrades() {
        environmentPocket = new PocketEnvironment();
        ComputerCraftAPI.registerPocketUpgrade(environmentPocket);
        chatPocket = new PocketChatBox();
        ComputerCraftAPI.registerPocketUpgrade(chatPocket);
        playerPocket = new PocketPlayerDetector();
        ComputerCraftAPI.registerPocketUpgrade(playerPocket);
        geoScannerPocket = new PocketGeoScanner();
        ComputerCraftAPI.registerPocketUpgrade(geoScannerPocket);
    }

    private static void registerTurtleUpgrades() {
        chatBox = new TurtleChatBox();
        ComputerCraftAPI.registerTurtleUpgrade(chatBox);
        playerDetector = new TurtlePlayerDetector();
        ComputerCraftAPI.registerTurtleUpgrade(playerDetector);
        environmentDetector = new TurtleEnvironmentDetector();
        ComputerCraftAPI.registerTurtleUpgrade(environmentDetector);
        chunky = new TurtleChunky();
        ComputerCraftAPI.registerTurtleUpgrade(chunky);
        geoScanner = new TurtleGeoScanner();
        ComputerCraftAPI.registerTurtleUpgrade(geoScanner);
        kinetic = new TurtleWeakMechanicSoul();
        ComputerCraftAPI.registerTurtleUpgrade(kinetic);
    }

}