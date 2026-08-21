package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.util.Platform;
import de.srendi.advancedperipherals.lib.integrations.IPeripheralIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.PriorityQueue;

public final class IntegrationPeripheralProvider implements IPeripheralProvider {
    private static final PriorityQueue<IPeripheralIntegration> INTEGRATIONS = new PriorityQueue<>(Comparator.comparingInt(IPeripheralIntegration::getPriority));

    private IntegrationPeripheralProvider() {}

    public static void load() {
        ComputerCraftAPI.registerGenericSource(new BeaconIntegration());
        registerBlockIntegrations();

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

        ForgeComputerCraftAPI.registerPeripheralProvider(new IntegrationPeripheralProvider());
    }

    public static void registerBlockIntegrations() {
        INTEGRATIONS.add(new BlockIntegration(NoteBlockIntegration::new, NoteBlock.class::isInstance));
    }

    @Override
    @NotNull
    public LazyOptional<IPeripheral> getPeripheral(Level level, BlockPos blockPos, Direction direction) {
        for (IPeripheralIntegration integration : INTEGRATIONS) {
            if (integration.isSuitable(level, blockPos, direction)) {
                return LazyOptional.of(() -> integration.buildPeripheral(level, blockPos, direction));
            }
        }
        return LazyOptional.empty();
    }
}
