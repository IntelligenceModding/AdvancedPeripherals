package de.srendi.advancedperipherals.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static final KeyMapping DESCRIPTION_KEYBINDING = new KeyMapping("keybind.advancedperipherals.description", GLFW.GLFW_KEY_LEFT_CONTROL, "keybind.advancedperipherals.category");

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(DESCRIPTION_KEYBINDING);
    }

}
