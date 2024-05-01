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
import net.minecraft.network.chat.Component;

public class TranslationUtil {

    public static Component itemTooltip(String descriptionId) {
        int lastIndex = descriptionId.lastIndexOf('.');
        return Component.translatable(
                String.format("%s.tooltip.%s", descriptionId.substring(0, lastIndex).replaceFirst("^block", "item"),
                        descriptionId.substring(lastIndex + 1)));
    }

    public static String turtle(String name) {
        return String.format("turtle.%s.%s", AdvancedPeripherals.MOD_ID, name);
    }

    public static String pocket(String name) {
        return String.format("pocket.%s.%s", AdvancedPeripherals.MOD_ID, name);
    }
}
