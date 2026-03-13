package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.luaapi.GlobalVersionsLuaAPI;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketChatBoxUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketColonyIntegratorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketDistanceDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketEnvironmentUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketGeoScannerUpgrade;
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
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CCRegistration {
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleChatBoxUpgrade>> CHAT_BOX_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.CHATTY_TURTLE.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleChatBoxUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleChunkyUpgrade>> CHUNKY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.CHUNKY_TURTLE.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleChunkyUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleCompassUpgrade>> COMPASS_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.COMPASS_TURTLE.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleCompassUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<EndAutomata>> END_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.END_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(EndAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleEnvironmentDetectorUpgrade>> ENVIRONMENT_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.ENVIRONMENT_TURTLE.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleEnvironmentDetectorUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleGeoScannerUpgrade>> GEO_SCANNER_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.GEOSCANNER_TURTLE.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleGeoScannerUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<HusbandryAutomata>> HUSBANDRY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.HUSBANDRY_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(HusbandryAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<OverpoweredEndAutomata>> OP_END_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.OP_END_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(OverpoweredEndAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<OverpoweredHusbandryAutomata>> OP_HUSBANDRY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.OP_HUSBANDRY_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(OverpoweredHusbandryAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<OverpoweredWeakAutomata>> OP_WEAK_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.OP_WEAK_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(OverpoweredWeakAutomata::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtlePlayerDetectorUpgrade>> PLAYER_DETECTOR_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.PLAYER_TURTLE.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtlePlayerDetectorUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<TurtleSaddleUpgrade>> SADDLE_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.SADDLE_TURTLE.getPath(), () -> UpgradeType.simpleWithCustomItem(TurtleSaddleUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<WeakAutomata>> WEAK_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.WEAK_AUTOMATA.getPath(), () -> UpgradeType.simpleWithCustomItem(WeakAutomata::new));

    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketChatBoxUpgrade>> CHAT_BOX_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.CHATTY_POCKET.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketChatBoxUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketDistanceDetectorUpgrade>> DISTANCE_DETECTOR_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.DISTANCE_POCKET.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketDistanceDetectorUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketColonyIntegratorUpgrade>> COLONY_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.COLONY_POCKET.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketColonyIntegratorUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketEnvironmentUpgrade>> ENVIRONMENT_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.ENVIRONMENT_POCKET.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketEnvironmentUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketGeoScannerUpgrade>> GEO_SCANNER_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.GEOSCANNER_POCKET.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketGeoScannerUpgrade::new));
    public static final DeferredHolder<UpgradeType<? extends IPocketUpgrade>, UpgradeType<PocketPlayerDetectorUpgrade>> PLAYER_DETECTOR_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.PLAYER_POCKET.getPath(), () -> UpgradeType.simpleWithCustomItem(PocketPlayerDetectorUpgrade::new));

    public static void register() {
        IntegrationPeripheralProvider.load();
        ComputerCraftAPI.registerAPIFactory(system -> GlobalVersionsLuaAPI.INSTANCE);
        ComputerCraftAPI.registerAPIFactory(SmartGlassesAPI::create);
    }

    public static class ID {
        public static final ResourceLocation CHATTY_TURTLE = AdvancedPeripherals.getRL("chatty_turtle");
        public static final ResourceLocation CHUNKY_TURTLE = AdvancedPeripherals.getRL("chunky_turtle");
        public static final ResourceLocation COMPASS_TURTLE = AdvancedPeripherals.getRL("compass_turtle");
        public static final ResourceLocation END_AUTOMATA = AdvancedPeripherals.getRL("end_automata");
        public static final ResourceLocation ENVIRONMENT_TURTLE = AdvancedPeripherals.getRL("environment_turtle");
        public static final ResourceLocation GEOSCANNER_TURTLE = AdvancedPeripherals.getRL("geoscanner_turtle");
        public static final ResourceLocation HUSBANDRY_AUTOMATA = AdvancedPeripherals.getRL("husbandry_automata");
        public static final ResourceLocation OP_END_AUTOMATA = AdvancedPeripherals.getRL("overpowered_end_automata");
        public static final ResourceLocation OP_HUSBANDRY_AUTOMATA = AdvancedPeripherals.getRL("overpowered_husbandry_automata");
        public static final ResourceLocation OP_WEAK_AUTOMATA = AdvancedPeripherals.getRL("overpowered_weak_automata");
        public static final ResourceLocation PLAYER_TURTLE = AdvancedPeripherals.getRL("player_turtle");
        public static final ResourceLocation SADDLE_TURTLE = AdvancedPeripherals.getRL("saddle_turtle");
        public static final ResourceLocation WEAK_AUTOMATA = AdvancedPeripherals.getRL("weak_automata");

        public static final ResourceLocation CHATTY_POCKET = AdvancedPeripherals.getRL("chatty_pocket");
        public static final ResourceLocation COLONY_POCKET = AdvancedPeripherals.getRL("colony_pocket");
        public static final ResourceLocation DISTANCE_POCKET = AdvancedPeripherals.getRL("distance_pocket");
        public static final ResourceLocation ENVIRONMENT_POCKET = AdvancedPeripherals.getRL("environment_pocket");
        public static final ResourceLocation GEOSCANNER_POCKET = AdvancedPeripherals.getRL("geoscanner_pocket");
        public static final ResourceLocation PLAYER_POCKET = AdvancedPeripherals.getRL("player_pocket");
    }
}
