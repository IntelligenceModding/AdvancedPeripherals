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
package de.srendi.advancedperipherals.common.util;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraftforge.fml.ModList;

import java.util.Optional;

public class Platform {

    public static Optional<Object> maybeLoadIntegration(final String modid, final String path) {
        if (!ModList.get().isLoaded(modid)) {
            AdvancedPeripherals.LOGGER.info("{} not loaded, skip integration loading", modid);
            return Optional.empty();
        }
        return maybeLoadIntegration(path);
    }

    public static Optional<Object> maybeLoadIntegration(final String path) {
        try {
            Class<?> clazz = Class.forName(AdvancedPeripherals.class.getPackage().getName() + ".common.addons." + path);
            return Optional.of(clazz.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException loadException) {
            if (APConfig.GENERAL_CONFIG.enableDebugMode.get())
                loadException.printStackTrace();
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
