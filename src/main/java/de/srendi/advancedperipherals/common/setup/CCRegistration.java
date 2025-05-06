package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChunkyUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleCompassUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRegistration {

    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleChunkyUpgrade>> CHUNKY_TURTLE = Registration.TURTLE_SERIALIZER.register(ID.CHUNKY_TURTLE.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleChunkyUpgrade::new));
    public static final RegistryObject<TurtleUpgradeSerialiser<TurtleCompassUpgrade>> COMPASS_TURTLE = Registration.TURTLE_SERIALIZER.register(ID.COMPASS_TURTLE.getPath(), () -> TurtleUpgradeSerialiser.simpleWithCustomItem(TurtleCompassUpgrade::new));

    public static IntegrationPeripheralProvider integrationPeripheralProvider;

    public static void register() {
        IntegrationPeripheralProvider.load();
        integrationPeripheralProvider = new IntegrationPeripheralProvider();
        ForgeComputerCraftAPI.registerPeripheralProvider(integrationPeripheralProvider);
    }

    public static class ID {

        public static final ResourceLocation CHUNKY_TURTLE = AdvancedPeripherals.getRL("chunky_turtle");
        public static final ResourceLocation COMPASS_TURTLE = AdvancedPeripherals.getRL("compass_turtle");

    }
}
