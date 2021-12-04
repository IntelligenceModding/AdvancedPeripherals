package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRegistration {

    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleChatBoxUpgrade>> CHAT_BOX_TURTLE = Registration.TURTLE_SERIALIZER.register(TurtleChatBoxUpgrade.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleChatBoxUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtlePlayerDetectorUpgrade>> PLAYER_DETECTOR_TURTLE = Registration.TURTLE_SERIALIZER.register(TurtlePlayerDetectorUpgrade.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtlePlayerDetectorUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleEnvironmentDetectorUpgrade>> ENVIRONMENT_TURTLE = Registration.TURTLE_SERIALIZER.register(TurtleEnvironmentDetectorUpgrade.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleEnvironmentDetectorUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleChunkyUpgrade>> CHUNKY_TURTLE = Registration.TURTLE_SERIALIZER.register(TurtleChunkyUpgrade.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleChunkyUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleGeoScannerUpgrade>> GEO_SCANNER_TURTLE = Registration.TURTLE_SERIALIZER.register(TurtleGeoScannerUpgrade.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleGeoScannerUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<WeakAutomata>> WEAK_TURTLE = Registration.TURTLE_SERIALIZER.register(WeakAutomata.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(WeakAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<EndAutomata>> END_TURTLE = Registration.TURTLE_SERIALIZER.register(EndAutomata.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(EndAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<HusbandryAutomata>> HUSBANDRY_TURTLE = Registration.TURTLE_SERIALIZER.register(HusbandryAutomata.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(HusbandryAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<OverpoweredWeakAutomata>> OP_WEAK_TURTLE = Registration.TURTLE_SERIALIZER.register(OverpoweredWeakAutomata.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(OverpoweredWeakAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<OverpoweredEndAutomata>> OP_END_TURTLE = Registration.TURTLE_SERIALIZER.register(OverpoweredEndAutomata.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(OverpoweredEndAutomata::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<OverpoweredHusbandryAutomata>> OP_HUSBANDRY_TURTLE = Registration.TURTLE_SERIALIZER.register(OverpoweredHusbandryAutomata.ID.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(OverpoweredHusbandryAutomata::new));

    public static final RegistryObject<PocketUpgradeSerialiser<PocketChatBoxUpgrade>> CHAT_BOX_POCKET = Registration.POCKET_SERIALIZER.register(PocketChatBoxUpgrade.ID.getPath(), () -> PocketUpgradeSerialiser.simpleWithCustomItem(PocketChatBoxUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketPlayerDetectorUpgrade>> PLAYER_DETECTOR_POCKET = Registration.POCKET_SERIALIZER.register(PocketPlayerDetectorUpgrade.ID.getPath(), () -> PocketUpgradeSerialiser.simpleWithCustomItem(PocketPlayerDetectorUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketEnvironmentUpgrade>> ENVIRONMENT_POCKET = Registration.POCKET_SERIALIZER.register(PocketEnvironmentUpgrade.ID.getPath(), () -> PocketUpgradeSerialiser.simpleWithCustomItem(PocketEnvironmentUpgrade::new));
    public static final RegistryObject<PocketUpgradeSerialiser<PocketGeoScannerUpgrade>> GEO_SCANNER_POCKET = Registration.POCKET_SERIALIZER.register(PocketGeoScannerUpgrade.ID.getPath(), () -> PocketUpgradeSerialiser.simpleWithCustomItem(PocketGeoScannerUpgrade::new));

    public static IntegrationPeripheralProvider integrationPeripheralProvider;

    public static void register() {
        IntegrationPeripheralProvider.load();
        integrationPeripheralProvider = new IntegrationPeripheralProvider();
        ComputerCraftAPI.registerPeripheralProvider(integrationPeripheralProvider);
    }
}