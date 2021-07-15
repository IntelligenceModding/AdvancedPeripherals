package de.srendi.advancedperipherals.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyBinding DESCRIPTION_KEYBINDING = new KeyBinding("keybind.advancedperipherals.description", GLFW.GLFW_KEY_LEFT_CONTROL,
            "keybind.advancedperipherals.category");

    public static void register() {
        ClientRegistry.registerKeyBinding(DESCRIPTION_KEYBINDING);
    }

}
