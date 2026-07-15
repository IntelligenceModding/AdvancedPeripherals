package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
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
import net.neoforged.neoforge.registries.DeferredHolder;

public class CCRegistration {
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleChatBoxUpgrade>> CHAT_BOX_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.CHATTY.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleChatBoxUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleEnvironmentDetectorUpgrade>> ENVIRONMENT_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.ENVIRONMENT.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleEnvironmentDetectorUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleGeoScannerUpgrade>> GEO_SCANNER_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.GEOSCANNER.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleGeoScannerUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtlePlayerDetectorUpgrade>> PLAYER_DETECTOR_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.PLAYER.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtlePlayerDetectorUpgrade::new));

    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleCompassUpgrade>> COMPASS_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.COMPASS.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleCompassUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleSaddleUpgrade>> SADDLE_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.SADDLE.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleSaddleUpgrade::new));

    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleChunkyUpgrade>> CHUNKY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.CHUNKY.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleChunkyUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<WeakAutomata>> WEAK_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.WEAK_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(WeakAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<OverpoweredWeakAutomata>> OP_WEAK_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.OP_WEAK_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(OverpoweredWeakAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<HusbandryAutomata>> HUSBANDRY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.HUSBANDRY_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(HusbandryAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<OverpoweredHusbandryAutomata>> OP_HUSBANDRY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.OP_HUSBANDRY_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(OverpoweredHusbandryAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<EndAutomata>> END_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.END_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(EndAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<OverpoweredEndAutomata>> OP_END_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.Turtle.OP_END_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(OverpoweredEndAutomata::new));

    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketChatBoxUpgrade>> CHAT_BOX_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.CHATTY.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketChatBoxUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketDistanceDetectorUpgrade>> DISTANCE_DETECTOR_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.DISTANCE.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketDistanceDetectorUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketEnvironmentUpgrade>> ENVIRONMENT_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.ENVIRONMENT.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketEnvironmentUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketGeoScannerUpgrade>> GEO_SCANNER_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.GEOSCANNER.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketGeoScannerUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketInventoryManagerUpgrade>> INVENTORY_MANAGER_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.INVENTORY_MANAGER.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketInventoryManagerUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketPlayerDetectorUpgrade>> PLAYER_DETECTOR_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.PLAYER.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketPlayerDetectorUpgrade::new));

    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketColonyIntegratorUpgrade>> COLONY_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.Pocket.COLONY.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketColonyIntegratorUpgrade::new));

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

    public static class ID {
        public static class Turtle {
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
