package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.neoforged.fml.ModList;

import java.util.Optional;

public class Platform {

    public static Optional<Object> maybeLoadIntegration(final String modid, final String path) {
        if (!ModList.get().isLoaded(modid)) {
            AdvancedPeripherals.LOGGER.info("{} not loaded, skip integration loading", modid);
            return Optional.empty();
        }
        return maybeLoadIntegration(path);
    }

    public static Optional<Object> maybeLoadIntegration(final String path) {
        try {
            Class<?> clazz = Class.forName(AdvancedPeripherals.class.getPackage().getName() + ".common.addons." + path);
            return Optional.of(clazz.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException loadException) {
            if (APConfig.GENERAL_CONFIG.enableDebugMode.get())
                loadException.printStackTrace();
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
