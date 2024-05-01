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
package de.srendi.advancedperipherals.lib.misc;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface IConfigHandler {
    void addToConfig(ForgeConfigSpec.Builder builder);

    String name();

    default String settingsPostfix() {
        return "";
    }

    default String settingsName() {
        String name = name();
        String startName = Arrays.stream(name.toLowerCase().split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()).collect(Collectors.joining())
                + settingsPostfix();
        return startName.substring(0, 1).toLowerCase() + startName.substring(1);
    }
}
