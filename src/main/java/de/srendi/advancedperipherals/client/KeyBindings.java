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
package de.srendi.advancedperipherals.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static final KeyMapping DESCRIPTION_KEYBINDING = new KeyMapping("keybind.advancedperipherals.description",
            GLFW.GLFW_KEY_LEFT_CONTROL, "keybind.advancedperipherals.category");
    public static final KeyMapping GLASSES_HOTKEY_KEYBINDING = new KeyMapping(
            "keybind.advancedperipherals.glasses_hotkey", GLFW.GLFW_KEY_G, "keybind.advancedperipherals.category");

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(DESCRIPTION_KEYBINDING);
        event.register(GLASSES_HOTKEY_KEYBINDING);
    }

}
