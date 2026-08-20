package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.util.Platform;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class IntegrationPeripheralProvider {

    public static void load() {
        ComputerCraftAPI.registerGenericSource(new BeaconIntegration());

        for (APAddon addon : APAddon.values()) {
            if (!addon.isLoaded()) {
                continue;
            }
            String modid = addon.getModId();
            Runnable integration = Platform.maybeLoadIntegration(modid + ".Integration");
            if (integration == null) {
                AdvancedPeripherals.debug("Integration does not exist for {}", modid);
                continue;
            }
            AdvancedPeripherals.LOGGER.info("Loading integration for {}", modid);
            integration.run();
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
