package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
    public static final ForgeConfigSpec COMMON_SPEC;
    static final AdvancedPeripheralsConfig.CommonConfig COMMON_CONFIG;

    static {
        {
            final Pair<AdvancedPeripheralsConfig.CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(AdvancedPeripheralsConfig.CommonConfig::new);
            COMMON_CONFIG = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
    }
}
