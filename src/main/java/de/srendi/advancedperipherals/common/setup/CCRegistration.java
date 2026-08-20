package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.luaapi.APLuaAPI;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketChatBoxUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketColonyIntegratorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketDistanceDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketEnvironmentUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketGeoScannerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketInventoryManagerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketPlayerDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleBlockReaderUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChatBoxUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChunkyUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleCompassUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleEnvironmentDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleGeoScannerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtlePlayerDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleSaddleUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.EndAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.HusbandryAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.OverpoweredEndAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.OverpoweredHusbandryAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.OverpoweredWeakAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.WeakAutomata;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAPI;
import de.srendi.advancedperipherals.lib.annotation.DefaultTranslation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class CCRegistration {
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleBlockReaderUpgrade>> BLOCK_READER_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.BLOCK_READER.getPath(), () -> simpleTurtleUpgradeWithCustomItem(TurtleBlockReaderUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleChatBoxUpgrade>> CHAT_BOX_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.CHATTY.getPath(), () -> simpleTurtleUpgradeWithCustomItem(TurtleChatBoxUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleEnvironmentDetectorUpgrade>> ENVIRONMENT_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.ENVIRONMENT.getPath(), () -> simpleTurtleUpgradeWithCustomItem(TurtleEnvironmentDetectorUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleGeoScannerUpgrade>> GEO_SCANNER_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.GEOSCANNER.getPath(), () -> simpleTurtleUpgradeWithCustomItem(TurtleGeoScannerUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtlePlayerDetectorUpgrade>> PLAYER_DETECTOR_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.PLAYER.getPath(), () -> simpleTurtleUpgradeWithCustomItem(TurtlePlayerDetectorUpgrade::new));

    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleCompassUpgrade>> COMPASS_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.COMPASS.getPath(), () -> simpleTurtleUpgradeWithCustomItem(TurtleCompassUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleSaddleUpgrade>> SADDLE_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.SADDLE.getPath(), () -> simpleTurtleUpgradeWithCustomItem(TurtleSaddleUpgrade::new));

    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleChunkyUpgrade>> CHUNKY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.CHUNKY.getPath(), () -> simpleTurtleUpgradeWithCustomItem(TurtleChunkyUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<WeakAutomata>> WEAK_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.WEAK_AUTOMATA.getPath(), () -> simpleTurtleUpgradeWithCustomItem(WeakAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<OverpoweredWeakAutomata>> OP_WEAK_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.OP_WEAK_AUTOMATA.getPath(), () -> simpleTurtleUpgradeWithCustomItem(OverpoweredWeakAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<HusbandryAutomata>> HUSBANDRY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.HUSBANDRY_AUTOMATA.getPath(), () -> simpleTurtleUpgradeWithCustomItem(HusbandryAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<OverpoweredHusbandryAutomata>> OP_HUSBANDRY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.OP_HUSBANDRY_AUTOMATA.getPath(), () -> simpleTurtleUpgradeWithCustomItem(OverpoweredHusbandryAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<EndAutomata>> END_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.END_AUTOMATA.getPath(), () -> simpleTurtleUpgradeWithCustomItem(EndAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<OverpoweredEndAutomata>> OP_END_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.OP_END_AUTOMATA.getPath(), () -> simpleTurtleUpgradeWithCustomItem(OverpoweredEndAutomata::new));

    public static final RegistryObject<PocketUpgradeSerialiser<PocketChatBoxUpgrade>> CHAT_BOX_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.CHATTY.getPath(), () -> simplePocketUpgradeWithCustomItem(PocketChatBoxUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketDistanceDetectorUpgrade>> DISTANCE_DETECTOR_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.DISTANCE.getPath(), () -> simplePocketUpgradeWithCustomItem(PocketDistanceDetectorUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketEnvironmentUpgrade>> ENVIRONMENT_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.ENVIRONMENT.getPath(), () -> simplePocketUpgradeWithCustomItem(PocketEnvironmentUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketGeoScannerUpgrade>> GEO_SCANNER_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.GEOSCANNER.getPath(), () -> simplePocketUpgradeWithCustomItem(PocketGeoScannerUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketInventoryManagerUpgrade>> INVENTORY_MANAGER_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.INVENTORY_MANAGER.getPath(), () -> simplePocketUpgradeWithCustomItem(PocketInventoryManagerUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketPlayerDetectorUpgrade>> PLAYER_DETECTOR_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.PLAYER.getPath(), () -> simplePocketUpgradeWithCustomItem(PocketPlayerDetectorUpgrade::new));

    public static final RegistryObject<PocketUpgradeSerialiser<PocketColonyIntegratorUpgrade>> COLONY_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.COLONY.getPath(), () -> simplePocketUpgradeWithCustomItem(PocketColonyIntegratorUpgrade::new));

    public static void register() {
    }

    /**
     * register main should only be invoked on main thread.
     */
    public static void registerMain() {
        IntegrationPeripheralProvider.load();
        ComputerCraftAPI.registerAPIFactory(system -> APLuaAPI.INSTANCE);
        ComputerCraftAPI.registerAPIFactory(SmartGlassesAPI::create);
    }

    private static <T extends ITurtleUpgrade> TurtleUpgradeSerialiser<T> simpleTurtleUpgradeWithCustomItem(Function<ItemStack, T> factory) {
        return TurtleUpgradeSerialiser.simpleWithCustomItem((id, stack) -> factory.apply(stack));
    }

    private static <T extends IPocketUpgrade> PocketUpgradeSerialiser<T> simplePocketUpgradeWithCustomItem(Function<ItemStack, T> factory) {
        return PocketUpgradeSerialiser.simpleWithCustomItem((id, stack) -> factory.apply(stack));
    }

    public static class ID {
        public static class Turtle {
            @DefaultTranslation("Block Reader")
            public static final ResourceLocation BLOCK_READER = AdvancedPeripherals.getRL("block_reader_turtle");
            @DefaultTranslation("Chatty")
            public static final ResourceLocation CHATTY = AdvancedPeripherals.getRL("chatty_turtle");
            @DefaultTranslation("Chunky")
            public static final ResourceLocation CHUNKY = AdvancedPeripherals.getRL("chunky_turtle");
            @DefaultTranslation("Compass")
            public static final ResourceLocation COMPASS = AdvancedPeripherals.getRL("compass_turtle");
            @DefaultTranslation("End Automata")
            public static final ResourceLocation END_AUTOMATA = AdvancedPeripherals.getRL("end_automata");
            @DefaultTranslation("Environment")
            public static final ResourceLocation ENVIRONMENT = AdvancedPeripherals.getRL("environment_turtle");
            @DefaultTranslation("Geo")
            public static final ResourceLocation GEOSCANNER = AdvancedPeripherals.getRL("geoscanner_turtle");
            @DefaultTranslation("Husbandry Automata")
            public static final ResourceLocation HUSBANDRY_AUTOMATA = AdvancedPeripherals.getRL("husbandry_automata");
            @DefaultTranslation("Overpowered End Automata")
            public static final ResourceLocation OP_END_AUTOMATA = AdvancedPeripherals.getRL("overpowered_end_automata");
            @DefaultTranslation("Overpowered Husbandry Automata")
            public static final ResourceLocation OP_HUSBANDRY_AUTOMATA = AdvancedPeripherals.getRL("overpowered_husbandry_automata");
            @DefaultTranslation("Overpowered Weak Automata")
            public static final ResourceLocation OP_WEAK_AUTOMATA = AdvancedPeripherals.getRL("overpowered_weak_automata");
            @DefaultTranslation("Player Detector")
            public static final ResourceLocation PLAYER = AdvancedPeripherals.getRL("player_turtle");
            @DefaultTranslation("Saddle")
            public static final ResourceLocation SADDLE = AdvancedPeripherals.getRL("saddle_turtle");
            @DefaultTranslation("Weak Automata")
            public static final ResourceLocation WEAK_AUTOMATA = AdvancedPeripherals.getRL("weak_automata");
        }

        public static class Pocket {
            @DefaultTranslation("Chatty")
            public static final ResourceLocation CHATTY = AdvancedPeripherals.getRL("chatty_pocket");
            @DefaultTranslation("Colony")
            public static final ResourceLocation COLONY = AdvancedPeripherals.getRL("colony_pocket");
            @DefaultTranslation("Distance Detector")
            public static final ResourceLocation DISTANCE = AdvancedPeripherals.getRL("distance_pocket");
            @DefaultTranslation("Environment")
            public static final ResourceLocation ENVIRONMENT = AdvancedPeripherals.getRL("environment_pocket");
            @DefaultTranslation("Geo")
            public static final ResourceLocation GEOSCANNER = AdvancedPeripherals.getRL("geoscanner_pocket");
            @DefaultTranslation("Inventory Manager")
            public static final ResourceLocation INVENTORY_MANAGER = AdvancedPeripherals.getRL("inventory_manager_pocket");
            @DefaultTranslation("Player Detector")
            public static final ResourceLocation PLAYER = AdvancedPeripherals.getRL("player_pocket");
        }
    }
}
