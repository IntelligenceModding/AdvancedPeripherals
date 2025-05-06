package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.ComputerCraftAPI;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.Platform;

import java.util.Optional;

public class IntegrationPeripheralProvider {

    private static final String[] SUPPORTED_MODS = new String[]{"mekanism", "powah"};

    public static void load() {
        ComputerCraftAPI.registerGenericSource(new BeaconIntegration());
        //TODO: See https://github.com/cc-tweaked/CC-Tweaked/discussions/2196
        //registerIntegration(new BlockIntegration(NoteBlockIntegration::new, NoteBlock.class::isInstance));

        for (String mod : SUPPORTED_MODS) {
            Optional<Object> integration = Platform.maybeLoadIntegration(mod, mod + ".Integration");
            if (integration.isEmpty()) {
                AdvancedPeripherals.LOGGER.warn("Failed to load integration for {}", mod);
                continue;
            }
            Runnable runnable = (Runnable)(integration.get());
            AdvancedPeripherals.LOGGER.info("Successfully loaded integration for {}", mod);
            runnable.run();
        }
    }
}
