package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public class Platform {
    @Nullable
    public static Runnable maybeLoadIntegration(final String path) {
        String classPath = AdvancedPeripherals.class.getPackage().getName() + ".common.addons." + path;
        try {
            Class<?> clazz = Class.forName(classPath);
            return (Runnable) clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Class " + classPath + " does not have public default constructor", e);
        } catch (ClassNotFoundException e) {
            AdvancedPeripherals.debug("Did not find integration entry point at {}", classPath);
            return null;
        } catch (InstantiationException | InvocationTargetException | RuntimeException e) {
            AdvancedPeripherals.exception("Failed to initialize integration class " + classPath, e);
            return null;
        }
    }
}
