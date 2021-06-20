package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.BeaconIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania.ManaFlowerIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania.ManaPoolIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.botania.SpreaderIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering.CapacitorIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering.RedstoneConnectorIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.immersiveengineering.RedstoneProbeIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism.*;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketChatBox;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketEnvironment;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketPlayerDetector;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChatBox;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChunky;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleEnvironmentDetector;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtlePlayerDetector;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRegistration {

    public static TurtleChatBox chatBox;
    public static TurtleEnvironmentDetector environmentDetector;
    public static TurtlePlayerDetector playerDetector;
    public static TurtleChunky chunky;

    public static PocketEnvironment environmentPocket;
    public static PocketChatBox chatPocket;
    public static PocketPlayerDetector playerPocket;

    public static void register() {
        registerPocketUpgrades();
        registerTurtleUpgrades();
        registerGenericSources();
    }

    private static void registerPocketUpgrades() {
        environmentPocket = new PocketEnvironment();
        ComputerCraftAPI.registerPocketUpgrade(environmentPocket);
        chatPocket = new PocketChatBox();
        ComputerCraftAPI.registerPocketUpgrade(chatPocket);
        playerPocket = new PocketPlayerDetector();
        ComputerCraftAPI.registerPocketUpgrade(playerPocket);
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
    }

    private static void registerGenericSources() {
        ComputerCraftAPI.registerGenericSource(new BeaconIntegration());
        if (ModList.get().isLoaded("mekanismgenerators")) {
            ComputerCraftAPI.registerGenericSource(new FissionIntegration());
            ComputerCraftAPI.registerGenericSource(new FusionIntegration());
            ComputerCraftAPI.registerGenericSource(new TurbineIntegration());
        }
        if (ModList.get().isLoaded("mekanism")) {
            ComputerCraftAPI.registerGenericSource(new InductionPortIntegration());
            ComputerCraftAPI.registerGenericSource(new BoilerIntegration());
            ComputerCraftAPI.registerGenericSource(new DigitalMinerIntegration());
            ComputerCraftAPI.registerGenericSource(new ChemicalTankIntegration());
            ComputerCraftAPI.registerGenericSource(new GenericMekanismIntegration());
        }
        if (ModList.get().isLoaded("botania")) {
            ComputerCraftAPI.registerGenericSource(new ManaPoolIntegration());
            ComputerCraftAPI.registerGenericSource(new SpreaderIntegration());
            ComputerCraftAPI.registerGenericSource(new ManaFlowerIntegration());
        }
        if (ModList.get().isLoaded("immersiveengineering")) {
            ComputerCraftAPI.registerGenericSource(new RedstoneProbeIntegration());
            ComputerCraftAPI.registerGenericSource(new RedstoneConnectorIntegration());
            ComputerCraftAPI.registerGenericSource(new CapacitorIntegration());
        }
    }

}