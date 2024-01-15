package de.srendi.advancedperipherals.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class KeybindUtil {

    public static boolean isKeyPressed(KeyMapping keyBinding) {
        if (keyBinding.isDown()) {
            return true;
        } else if (keyBinding.getKeyConflictContext().isActive() && keyBinding.getKeyModifier().isActive(keyBinding.getKeyConflictContext())) {
            return isKeyDown(keyBinding);
        } else {
            return KeyModifier.isKeyCodeModifier(keyBinding.getKey()) && isKeyDown(keyBinding);
        }
    }

    private static boolean isKeyDown(KeyMapping keyBinding) {
        InputConstants.Key key = keyBinding.getKey();
        int keyCode = key.getValue();
        if (keyCode != InputConstants.UNKNOWN.getValue()) {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            if (key.getType() == InputConstants.Type.KEYSYM) {
                return InputConstants.isKeyDown(windowHandle, keyCode);
            }

            if (key.getType() == InputConstants.Type.MOUSE) {
                return GLFW.glfwGetMouseButton(windowHandle, keyCode) == 1;
            }
        }

        return false;
    }


}
