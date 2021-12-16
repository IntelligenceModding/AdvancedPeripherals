package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.Platform;
import de.srendi.advancedperipherals.lib.integrations.IPeripheralIntegration;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;

public class IntegrationPeripheralProvider implements IPeripheralProvider {

    public static final String[] SUPPORTED_MODS = new String[]{};

    public static final PriorityQueue<IPeripheralIntegration> integrations = new PriorityQueue<>(Comparator.comparingInt(IPeripheralIntegration::getPriority));

    public static void registerIntegration(IPeripheralIntegration integration) {
        integrations.add(integration);
    }

    /**
     * Register tile entity integration, better use this method over manual TileEntityIntegration creation, because this method provides type check
     *
     * @param integration integration generator
     * @param tileClass   target integration class
     * @param <T>         target integration
     */
    public static <T extends BlockEntity> void registerBlockEntityIntegration(Function<BlockEntity, BlockEntityIntegrationPeripheral<T>> integration, Class<T> tileClass) {
        registerIntegration(new BlockEntityIntegration(integration, tileClass::isInstance));
    }

    /**
     * Register tile entity integration, better use this method over manual TileEntityIntegration creation, because this method provides type check
     *
     * @param integration integration generator
     * @param tileClass   target integration class
     * @param priority    Integration priority, lower is better
     * @param <T>         target integration
     */
    public static <T extends BlockEntity> void registerBlockEntityIntegration(Function<BlockEntity, BlockEntityIntegrationPeripheral<T>> integration, Class<T> tileClass, int priority) {
        registerIntegration(new BlockEntityIntegration(integration, tileClass::isInstance, priority));
    }

    public static void load() {
        registerIntegration(new BlockEntityIntegration(BeaconIntegration::new, BeaconBlockEntity.class::isInstance));
        registerIntegration(new BlockIntegration(NoteblockIntegration::new, NoteBlock.class::isInstance));

        for (String mod : SUPPORTED_MODS) {
            Optional<Object> integration = Platform.maybeLoadIntegration(mod, mod + ".Integration");
            integration.ifPresent(obj -> {
                AdvancedPeripherals.LOGGER.warn("Successfully loaded integration for {}", mod);
                ((Runnable) obj).run();
            });
            if (integration.isEmpty())
                AdvancedPeripherals.LOGGER.warn("Failed to load integration for {}", mod);
        }
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        for (IPeripheralIntegration integration : integrations) {
            if (integration.isSuitable(level, blockPos, direction))
                return LazyOptional.of(() -> integration.buildPeripheral(level, blockPos, direction));
        }
        return LazyOptional.empty();
    }
}
