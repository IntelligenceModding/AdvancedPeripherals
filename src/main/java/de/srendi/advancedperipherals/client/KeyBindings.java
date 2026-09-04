package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.platform.InputConstants;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.annotation.DefaultTranslation;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    private static final String KEYBIND_PREFIX = "keybind." + AdvancedPeripherals.MOD_ID;
    private static final String CATEGORY = KEYBIND_PREFIX + ".category";

    @DefaultTranslation("Show Description")
    public static final KeyMapping DESCRIPTION_KEYBINDING = new KeyMapping(
        KEYBIND_PREFIX + ".description",
        KeyConflictContext.GUI,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_LEFT_CONTROL,
        CATEGORY
    );
    @DefaultTranslation("Smart Glasses Hotkey")
    public static final KeyMapping GLASSES_HOTKEY_KEYBINDING = new KeyMapping(
        KEYBIND_PREFIX + ".glasses_hotkey",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
        CATEGORY
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(DESCRIPTION_KEYBINDING);
        event.register(GLASSES_HOTKEY_KEYBINDING);
    }
}
