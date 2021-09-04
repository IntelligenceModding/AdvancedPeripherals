package de.srendi.advancedperipherals.api;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Configuration class for tweaks of library
 */
public class LibConfig {

    public static boolean isInitialCooldownEnabled = true;
    public static int initialCooldownSensetiveLevel = 6_000;

    private static ForgeConfigSpec.BooleanValue IS_INITIAL_COOLDOWN_ENABLED;
    private static ForgeConfigSpec.IntValue INITIAL_COOLDOWN_SENSENTIVE_LEVEL;

    public static void build(final ForgeConfigSpec.Builder builder) {
        IS_INITIAL_COOLDOWN_ENABLED = builder.comment("Enables initial cooldown on peripheral initialization").define("isInitialCooldownEnabled", isInitialCooldownEnabled);
        INITIAL_COOLDOWN_SENSENTIVE_LEVEL = builder.comment("Determinates initial cooldown sensentive level, values lower then this value will not trigger initial cooldown")
                .defineInRange("initialCooldownSensetiveLevel", initialCooldownSensetiveLevel, 0, Integer.MAX_VALUE);
    }

    public static void reloadConfig() {
        isInitialCooldownEnabled = IS_INITIAL_COOLDOWN_ENABLED.get();
        initialCooldownSensetiveLevel = INITIAL_COOLDOWN_SENSENTIVE_LEVEL.get();
    }
}
