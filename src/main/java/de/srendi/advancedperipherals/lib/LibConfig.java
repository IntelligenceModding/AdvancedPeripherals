/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.lib;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Configuration class for tweaks of library
 */
public class LibConfig {

    public static boolean initialCooldownEnabled = true;
    public static int initialCooldownSensitivity = 6_000;
    private static boolean testMode = false;
    private static ForgeConfigSpec.BooleanValue isInitialCooldownEnabled;
    private static ForgeConfigSpec.IntValue initialCooldownSensitiveLevel;

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

    public static void build(final ForgeConfigSpec.Builder builder) {
        isInitialCooldownEnabled = builder.comment("Enables initial cooldown on peripheral initialization")
                .define("isInitialCooldownEnabled", true);
        initialCooldownSensitiveLevel = builder.comment(
                "Determinates initial cooldown sensitive level, values lower then this value will not trigger initial cooldown")
                .defineInRange("initialCooldownSensitiveLevel", 6_000, 0, Integer.MAX_VALUE);
    }

    public static void reloadConfig() {
        if (!testMode) {
            initialCooldownEnabled = isInitialCooldownEnabled.get();
            initialCooldownSensitivity = initialCooldownSensitiveLevel.get();
        }
    }
}
