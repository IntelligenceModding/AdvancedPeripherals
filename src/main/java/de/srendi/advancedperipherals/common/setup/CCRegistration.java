package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.*;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRegistration {

    public static TurtleChatBox chatBox;
    public static TurtleEnvironmentDetector environmentDetector;
    public static TurtlePlayerDetector playerDetector;
    public static TurtleChunky chunky;
    public static TurtleGeoScanner geoScanner;
    public static WeakAutomata weakAutomata;
    public static EndAutomata endAutomata;
    public static HusbandryAutomata husbandryAutomata;
    public static OverpoweredWeakAutomata overpoweredWeakAutomata;
    public static OverpoweredEndAutomata overpoweredEndAutomata;
    public static OverpoweredHusbandryAutomata overpoweredHusbandryAutomata;

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
        weakAutomata = new WeakAutomata();
        ComputerCraftAPI.registerTurtleUpgrade(weakAutomata);
        endAutomata = new EndAutomata();
        ComputerCraftAPI.registerTurtleUpgrade(endAutomata);
        husbandryAutomata = new HusbandryAutomata();
        ComputerCraftAPI.registerTurtleUpgrade(husbandryAutomata);
        overpoweredWeakAutomata = new OverpoweredWeakAutomata();
        ComputerCraftAPI.registerTurtleUpgrade(overpoweredWeakAutomata);
        overpoweredEndAutomata = new OverpoweredEndAutomata();
        ComputerCraftAPI.registerTurtleUpgrade(overpoweredEndAutomata);
        overpoweredHusbandryAutomata = new OverpoweredHusbandryAutomata();
        ComputerCraftAPI.registerTurtleUpgrade(overpoweredHusbandryAutomata);
    }

}