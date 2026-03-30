package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.Platform;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.Optional;

public class IntegrationPeripheralProvider {

    private static final String[] SUPPORTED_MODS = new String[]{"mekanism", "powah"};

    public static void load() {
        ComputerCraftAPI.registerGenericSource(new BeaconIntegration());

        for (String mod : SUPPORTED_MODS) {
            Optional<Object> integration = Platform.maybeLoadIntegration(mod, mod + ".Integration");
            if (integration.isEmpty()) {
                AdvancedPeripherals.LOGGER.warn("Failed to load integration for {}", mod);
                continue;
            }
            Runnable runnable = (Runnable) integration.get();
            AdvancedPeripherals.LOGGER.info("Successfully loaded integration for {}", mod);
            runnable.run();
        }
    }

    public static void registerBlockIntegrations(RegisterCapabilitiesEvent event) {
        event.registerBlock(
            PeripheralCapability.get(),
            (level, pos, state, blockEntity, side) -> new NoteBlockIntegration(level, pos),
            Blocks.NOTE_BLOCK
        );
    }
}
