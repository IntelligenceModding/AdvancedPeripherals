package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRegistration {

    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleChatBoxUpgrade>> CHAT_BOX_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.CHATTY_TURTLE.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleChatBoxUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtlePlayerDetectorUpgrade>> PLAYER_DETECTOR_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.PLAYER_TURTLE.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtlePlayerDetectorUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleEnvironmentDetectorUpgrade>> ENVIRONMENT_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.ENVIRONMENT_TURTLE.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleEnvironmentDetectorUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleChunkyUpgrade>> CHUNKY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.CHUNKY_TURTLE.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleChunkyUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleGeoScannerUpgrade>> GEO_SCANNER_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.GEOSCANNER_TURTLE.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleGeoScannerUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleCompassUpgrade>> COMPASS_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.COMPASS_TURTLE.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleCompassUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<WeakAutomata>> WEAK_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.WEAK_AUTOMATA.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(WeakAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<EndAutomata>> END_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.END_AUTOMATA.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(EndAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<HusbandryAutomata>> HUSBANDRY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.HUSBANDRY_AUTOMATA.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(HusbandryAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<OverpoweredWeakAutomata>> OP_WEAK_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.OP_WEAK_AUTOMATA.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(OverpoweredWeakAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<OverpoweredEndAutomata>> OP_END_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.OP_END_AUTOMATA.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(OverpoweredEndAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<OverpoweredHusbandryAutomata>> OP_HUSBANDRY_TURTLE = APRegistration.TURTLE_SERIALIZER.register(ID.OP_HUSBANDRY_AUTOMATA.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(OverpoweredHusbandryAutomata::new));

    public static final RegistryObject<PocketUpgradeSerialiser<PocketChatBoxUpgrade>> CHAT_BOX_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.CHATTY_POCKET.getPath(), () -> PocketUpgradeSerialiser.simpleWithCustomItem(PocketChatBoxUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketPlayerDetectorUpgrade>> PLAYER_DETECTOR_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.PLAYER_POCKET.getPath(), () -> PocketUpgradeSerialiser.simpleWithCustomItem(PocketPlayerDetectorUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketEnvironmentUpgrade>> ENVIRONMENT_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.ENVIRONMENT_POCKET.getPath(), () -> PocketUpgradeSerialiser.simpleWithCustomItem(PocketEnvironmentUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketGeoScannerUpgrade>> GEO_SCANNER_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.GEOSCANNER_POCKET.getPath(), () -> PocketUpgradeSerialiser.simpleWithCustomItem(PocketGeoScannerUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketColonyIntegratorUpgrade>> COLONY_POCKET = APRegistration.POCKET_SERIALIZER.register(ID.COLONY_POCKET.getPath(), () -> PocketUpgradeSerialiser.simpleWithCustomItem(PocketColonyIntegratorUpgrade::new));

    public static IntegrationPeripheralProvider integrationPeripheralProvider;

    protected static void register() {
        IntegrationPeripheralProvider.load();
        integrationPeripheralProvider = new IntegrationPeripheralProvider();
        ForgeComputerCraftAPI.registerPeripheralProvider(integrationPeripheralProvider);
    }

    public static class ID {

        public static final ResourceLocation CHATTY_TURTLE = new ResourceLocation(AdvancedPeripherals.MOD_ID, "chatty_turtle");
        public static final ResourceLocation PLAYER_TURTLE = new ResourceLocation(AdvancedPeripherals.MOD_ID, "player_turtle");
        public static final ResourceLocation ENVIRONMENT_TURTLE = new ResourceLocation(AdvancedPeripherals.MOD_ID, "environment_turtle");
        public static final ResourceLocation CHUNKY_TURTLE = new ResourceLocation(AdvancedPeripherals.MOD_ID, "chunky_turtle");
        public static final ResourceLocation GEOSCANNER_TURTLE = new ResourceLocation(AdvancedPeripherals.MOD_ID, "geoscanner_turtle");
        public static final ResourceLocation COMPASS_TURTLE = new ResourceLocation(AdvancedPeripherals.MOD_ID, "compass_turtle");
        public static final ResourceLocation WEAK_AUTOMATA = new ResourceLocation(AdvancedPeripherals.MOD_ID, "weak_automata");
        public static final ResourceLocation END_AUTOMATA = new ResourceLocation(AdvancedPeripherals.MOD_ID, "end_automata");
        public static final ResourceLocation HUSBANDRY_AUTOMATA = new ResourceLocation(AdvancedPeripherals.MOD_ID, "husbandry_automata");
        public static final ResourceLocation OP_WEAK_AUTOMATA = new ResourceLocation(AdvancedPeripherals.MOD_ID, "overpowered_weak_automata");
        public static final ResourceLocation OP_END_AUTOMATA = new ResourceLocation(AdvancedPeripherals.MOD_ID, "overpowered_end_automata");
        public static final ResourceLocation OP_HUSBANDRY_AUTOMATA = new ResourceLocation(AdvancedPeripherals.MOD_ID, "overpowered_husbandry_automata");

        public static final ResourceLocation CHATTY_POCKET = new ResourceLocation(AdvancedPeripherals.MOD_ID, "chatty_pocket");
        public static final ResourceLocation PLAYER_POCKET = new ResourceLocation(AdvancedPeripherals.MOD_ID, "player_pocket");
        public static final ResourceLocation ENVIRONMENT_POCKET = new ResourceLocation(AdvancedPeripherals.MOD_ID, "environment_pocket");
        public static final ResourceLocation GEOSCANNER_POCKET = new ResourceLocation(AdvancedPeripherals.MOD_ID, "geoscanner_pocket");
        public static final ResourceLocation COLONY_POCKET = new ResourceLocation(AdvancedPeripherals.MOD_ID, "colony_pocket");

    }
}
