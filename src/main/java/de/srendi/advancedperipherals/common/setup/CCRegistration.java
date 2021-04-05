package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketEnvironment;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChatBox;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChunky;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleEnvironmentDetector;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtlePlayerDetector;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRegistration {

    public static TurtleChatBox chatBox;
    public static TurtleEnvironmentDetector environmentDetector;
    public static TurtlePlayerDetector playerDetector;
    public static TurtleChunky chunky;

    public static PocketEnvironment environmentPocket;

    public static void register() {
        chatBox = new TurtleChatBox();
        ComputerCraftAPI.registerTurtleUpgrade(chatBox);
        playerDetector = new TurtlePlayerDetector();
        ComputerCraftAPI.registerTurtleUpgrade(playerDetector);
        environmentDetector = new TurtleEnvironmentDetector();
        ComputerCraftAPI.registerTurtleUpgrade(environmentDetector);
        chunky = new TurtleChunky();
        ComputerCraftAPI.registerTurtleUpgrade(chunky);
        environmentPocket = new PocketEnvironment();
        ComputerCraftAPI.registerPocketUpgrade(environmentPocket);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        register();
    }
}