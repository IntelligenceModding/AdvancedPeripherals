package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraftforge.fml.ModList;

import java.util.Optional;

public class Platform {

    public static Optional<Object> maybeLoadIntegration(final String modid, final String path) {
        AdvancedPeripherals.LOGGER.info(String.format("%s not loaded, skip integration loading", modid));
        return ModList.get().isLoaded(modid) ? maybeLoadIntegration(path) : Optional.empty();
    }

    public static Optional<Object> maybeLoadIntegration(final String path) {
        try {
            Class<?> clazz = Class.forName(AdvancedPeripherals.class.getPackage().getName() + ".common.addon." + path);
            return Optional.of(clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ignored) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
