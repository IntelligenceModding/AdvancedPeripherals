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

import de.srendi.advancedperipherals.lib.misc.IConfigHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public interface IAPConfig {

    ForgeConfigSpec getConfigSpec();

    String getFileName();

    ModConfig.Type getType();

    default void register(IConfigHandler[] data, final ForgeConfigSpec.Builder builder) {
        for (IConfigHandler handler : data) {
            handler.addToConfig(builder);
        }
    }

    default void pop(String name, ForgeConfigSpec.Builder builder) {
        builder.pop();
        builder.push(name);
    }
}
