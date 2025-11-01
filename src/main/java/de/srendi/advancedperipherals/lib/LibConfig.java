package de.srendi.advancedperipherals.lib;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuration class for tweaks of library
 */
public class LibConfig {

    public static boolean initialCooldownEnabled = true;
    public static int initialCooldownSensitivity = 6_000;
    private static boolean testMode = false;
    private static ModConfigSpec.BooleanValue isInitialCooldownEnabled;
    private static ModConfigSpec.IntValue initialCooldownSensitiveLevel;

    public static void setTestMode(boolean mode) {
        testMode = mode;
        if (mode) {
            initialCooldownEnabled = false;
        } else {
            if (isInitialCooldownEnabled != null) {
                reloadConfig();
            } else {
                initialCooldownEnabled = true;
            }
        }
    }

    public static void build(final ModConfigSpec.Builder builder) {
        isInitialCooldownEnabled = builder.comment("Enables initial cooldown on peripheral initialization").define("isInitialCooldownEnabled", true);
        initialCooldownSensitiveLevel = builder.comment("Determinates initial cooldown sensitive level, values lower then this value will not trigger initial cooldown").defineInRange("initialCooldownSensitiveLevel", 6_000, 0, Integer.MAX_VALUE);
    }

    public static void reloadConfig() {
        if (!testMode) {
            initialCooldownEnabled = isInitialCooldownEnabled.get();
            initialCooldownSensitivity = initialCooldownSensitiveLevel.get();
        }
    }
}
