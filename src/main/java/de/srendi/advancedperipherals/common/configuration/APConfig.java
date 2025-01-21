package de.srendi.advancedperipherals.common.configuration;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class APConfig extends ModConfig {

    public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig();
    public static final PeripheralsConfig PERIPHERALS_CONFIG = new PeripheralsConfig();
    public static final MetaphysicsConfig METAPHYSICS_CONFIG = new MetaphysicsConfig();
    public static final WorldConfig WORLD_CONFIG = new WorldConfig();

    public APConfig(IAPConfig config, ModContainer container) {
        super(config.getType(), config.getConfigSpec(), container, "Advancedperipherals/" + config.getFileName() + ".toml");
    }

    public static void register(ModLoadingContext context) {
        ModContainer modContainer = context.getActiveContainer();
        modContainer.addConfig(new APConfig(GENERAL_CONFIG, modContainer));
        modContainer.addConfig(new APConfig(PERIPHERALS_CONFIG, modContainer));
        modContainer.addConfig(new APConfig(METAPHYSICS_CONFIG, modContainer));
        modContainer.addConfig(new APConfig(WORLD_CONFIG, modContainer));
    }
}
