package de.srendi.advancedperipherals.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyMapping DESCRIPTION_KEYBINDING = new KeyMapping("keybind.advancedperipherals.description", GLFW.GLFW_KEY_LEFT_CONTROL,
            "keybind.advancedperipherals.category");

    public static void register() {
        ClientRegistry.registerKeyBinding(DESCRIPTION_KEYBINDING);
    }

}
