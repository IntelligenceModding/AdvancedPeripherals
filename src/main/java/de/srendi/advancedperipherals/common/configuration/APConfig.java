package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.HashMap;
import java.util.Map;

public class APConfig {
    private static final String CONFIG_DIR_NAME = "Advancedperipherals";
    private static final Map<IConfigSpec, IAPConfig> KNOWN_CONFIGS = new HashMap<>();

    public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig();
    public static final PeripheralsConfig PERIPHERALS_CONFIG = new PeripheralsConfig();
    public static final MetaphysicsConfig METAPHYSICS_CONFIG = new MetaphysicsConfig();
    public static final WorldConfig WORLD_CONFIG = new WorldConfig();

    public APConfig() {
    }

    public static void register(ModLoadingContext context) {
        //Creates the config folder
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(CONFIG_DIR_NAME));

        ModContainer modContainer = context.getActiveContainer();
        APConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, GENERAL_CONFIG);
        APConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, PERIPHERALS_CONFIG);
        APConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, METAPHYSICS_CONFIG);
        APConfigHelper.registerConfig(KNOWN_CONFIGS, modContainer, WORLD_CONFIG);
    }

    public static class APConfigHelper {
        public static String getAPConfigFilePath(IAPConfig config) {
            return CONFIG_DIR_NAME + "/" + config.getFileName() + ".toml";
        }

        public static void registerConfig(Map<IConfigSpec, IAPConfig> knownConfigs, ModContainer modContainer, IAPConfig config) {
            modContainer.addConfig(new ModConfig(config.getType(), config.getConfigSpec(), modContainer, getAPConfigFilePath(config)));
            knownConfigs.put(config.getConfigSpec(), config);
        }
    }
}
