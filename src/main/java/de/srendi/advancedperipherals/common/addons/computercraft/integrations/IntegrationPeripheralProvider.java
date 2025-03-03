package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.ComputerCraftAPI;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.Platform;
import de.srendi.advancedperipherals.lib.integrations.IPeripheralIntegration;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.Predicate;

public class IntegrationPeripheralProvider {

    private static final String[] SUPPORTED_MODS = new String[]{"mekanism", "powah"};

    private static final PriorityQueue<IPeripheralIntegration> integrations = new PriorityQueue<>(Comparator.comparingInt(IPeripheralIntegration::getPriority));

    private static void registerIntegration(IPeripheralIntegration integration) {
        integrations.add(integration);
    }

    public static void load() {
        ComputerCraftAPI.registerGenericSource(new BeaconIntegration());
        registerIntegration(new BlockIntegration(NoteBlockIntegration::new, NoteBlock.class::isInstance));

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

    /*@NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        for (IPeripheralIntegration integration : integrations) {
            if (integration.isSuitable(level, blockPos, direction))
                return LazyOptional.of(() -> integration.buildPeripheral(level, blockPos, direction));
        }
        return LazyOptional.empty();
    }*/
}
