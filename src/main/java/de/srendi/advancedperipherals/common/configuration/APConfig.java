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
package de.srendi.advancedperipherals.common.configuration;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.function.Function;

public class APConfig extends ModConfig {

    public static final ConfigFileHandler CONFIG_FILE_HANDLER = new ConfigFileHandler();

    public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig();
    public static final PeripheralsConfig PERIPHERALS_CONFIG = new PeripheralsConfig();
    public static final MetaphysicsConfig METAPHYSICS_CONFIG = new MetaphysicsConfig();
    public static final WorldConfig WORLD_CONFIG = new WorldConfig();

    public APConfig(IAPConfig config, ModContainer container) {
        super(config.getType(), config.getConfigSpec(), container,
                "Advancedperipherals/" + config.getFileName() + ".toml");
    }

    public static void register(ModLoadingContext context) {
        // Creates the config folder
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve("Advancedperipherals"),
                "Advancedperipherals");

        ModContainer modContainer = context.getActiveContainer();
        modContainer.addConfig(new APConfig(GENERAL_CONFIG, modContainer));
        modContainer.addConfig(new APConfig(PERIPHERALS_CONFIG, modContainer));
        modContainer.addConfig(new APConfig(METAPHYSICS_CONFIG, modContainer));
        modContainer.addConfig(new APConfig(WORLD_CONFIG, modContainer));
    }

    @Override
    public ConfigFileTypeHandler getHandler() {
        return CONFIG_FILE_HANDLER;
    }

    public static class ConfigFileHandler extends ConfigFileTypeHandler {

        public static Path getPath(Path path) {
            if (path.endsWith("serverconfig"))
                return FMLPaths.CONFIGDIR.get();

            return path;
        }

        @Override
        public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
            return super.reader(getPath(configBasePath));
        }

        @Override
        public void unload(Path configBasePath, ModConfig config) {
            super.unload(getPath(configBasePath), config);
        }
    }
}
