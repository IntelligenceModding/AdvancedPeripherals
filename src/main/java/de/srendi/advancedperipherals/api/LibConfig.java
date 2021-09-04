package de.srendi.advancedperipherals.api;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Configuration class for tweaks of library
 */
public class LibConfig {

    private static boolean testMode = false;

    public static boolean isInitialCooldownEnabled = true;
    public static int initialCooldownSensetiveLevel = 6_000;

    private static ForgeConfigSpec.BooleanValue IS_INITIAL_COOLDOWN_ENABLED;
    private static ForgeConfigSpec.IntValue INITIAL_COOLDOWN_SENSENTIVE_LEVEL;

    public static void setTestMode(boolean mode) {
        testMode = mode;
        if (mode) {
            isInitialCooldownEnabled = false;
        } else {
            if (IS_INITIAL_COOLDOWN_ENABLED != null) {
                reloadConfig();
            } else {
                isInitialCooldownEnabled = true;
            }
        }
    }

    public static void build(final ForgeConfigSpec.Builder builder) {
        IS_INITIAL_COOLDOWN_ENABLED = builder.comment("Enables initial cooldown on peripheral initialization").define("isInitialCooldownEnabled", true);
        INITIAL_COOLDOWN_SENSENTIVE_LEVEL = builder.comment("Determinates initial cooldown sensentive level, values lower then this value will not trigger initial cooldown")
                .defineInRange("initialCooldownSensetiveLevel", 6_000, 0, Integer.MAX_VALUE);
    }

    public static void reloadConfig() {
        if (!testMode) {
            isInitialCooldownEnabled = IS_INITIAL_COOLDOWN_ENABLED.get();
            initialCooldownSensetiveLevel = INITIAL_COOLDOWN_SENSENTIVE_LEVEL.get();
        }
    }
}
