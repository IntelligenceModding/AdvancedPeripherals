package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.*;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRegistration {

    public static List<AbstractTurtleUpgrade> turtleUpgrades;
    public static List<AbstractPocketUpgrade> pocketUpgrades;

    public static IntegrationPeripheralProvider integrationPeripheralProvider;

    public static void register() {
        registerPocketUpgrades();
        registerTurtleUpgrades();
        integrationPeripheralProvider = new IntegrationPeripheralProvider();
        integrationPeripheralProvider.register();
        ComputerCraftAPI.registerPeripheralProvider(integrationPeripheralProvider);
    }

    private static void registerPocketUpgrades() {
        pocketUpgrades = new ArrayList<AbstractPocketUpgrade>() {{
            add(new PocketEnvironmentUpgrade());
            add(new PocketChatBoxUpgrade());
            add(new PocketPlayerDetectorUpgrade());
            add(new PocketGeoScannerUpgrade());
            add(new PocketColonyIntegratorUpgrade());
        }};
        pocketUpgrades.forEach(ComputerCraftAPI::registerPocketUpgrade);
    }

    private static void registerTurtleUpgrades() {
        turtleUpgrades = new ArrayList<AbstractTurtleUpgrade>() {{
            add(new TurtleChatBoxUpgrade());
            add(new TurtlePlayerDetectorUpgrade());
            add(new TurtleEnvironmentDetectorUpgrade());
            add(new TurtleChunkyUpgrade());
            add(new TurtleGeoScannerUpgrade());
            add(new WeakAutomata());
            add(new EndAutomata());
            add(new HusbandryAutomata());
            add(new OverpoweredWeakAutomata());
            add(new OverpoweredEndAutomata());
            add(new OverpoweredHusbandryAutomata());
        }};
        turtleUpgrades.forEach(ComputerCraftAPI::registerTurtleUpgrade);
    }

}