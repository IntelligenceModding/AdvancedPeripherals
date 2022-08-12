package de.srendi.advancedperipherals.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static final KeyMapping DESCRIPTION_KEYBINDING = new KeyMapping("keybind.advancedperipherals.description", GLFW.GLFW_KEY_LEFT_CONTROL, "keybind.advancedperipherals.category");

    public static void register() {
        net.minecraftforge.client.ClientRegistry.registerKeyBinding(DESCRIPTION_KEYBINDING);
    }

}
