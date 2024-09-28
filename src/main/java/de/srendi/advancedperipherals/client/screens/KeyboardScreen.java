package de.srendi.advancedperipherals.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.client.gui.ClientInputHandler;
import dan200.computercraft.shared.computer.core.InputHandler;
import de.srendi.advancedperipherals.client.screens.base.BaseScreen;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.BitSet;

/**
 * A simple screen but without any rendering calls. Used to unlock the mouse so we can freely write stuff
 * <p>
 * Char/key logic stolen from CC's WidgetTerminal
 */
public class KeyboardScreen extends BaseScreen<KeyboardContainer> {

    protected final InputHandler input;
    private final BitSet keysDown = new BitSet(256);

    private float terminateTimer = -1;
    private float rebootTimer = -1;
    private float shutdownTimer = -1;

    private int lastMouseButton = -1;
    private int lastMouseX = -1;
    private int lastMouseY = -1;

    public KeyboardScreen(KeyboardContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        input = new ClientInputHandler(menu);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int x, int y, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        float scale = 2f;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        // Make the text a bit smaller on small screens
        if (screenWidth <= 1080)
            scale = 1f;

        matrixStack.scale(scale, scale, 1);
        String text = "Press ESC to close the Keyboard Screen.";
        float textX = (screenWidth / 2f - minecraft.font.width(text) * scale / 2f) / scale;
        minecraft.font.drawShadow(matrixStack, text, textX, 1, 0xFFFFFF);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
    }

    @Override
    public void renderBackground(@NotNull PoseStack pPoseStack) {
    }

    @Override
    public boolean charTyped(char ch, int modifiers) {
        if (ch >= 32 && ch <= 126 || ch >= 160 && ch <= 255) { // printable chars in byte range
            // Queue the "char" event
            input.queueEvent("char", new Object[]{Character.toString(ch)});
        }

        return true;
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            switch (key) {
                case GLFW.GLFW_KEY_T:
                    if (terminateTimer < 0) terminateTimer = 0;
                    return true;
                case GLFW.GLFW_KEY_S:
                    if (shutdownTimer < 0) shutdownTimer = 0;
                    return true;
                case GLFW.GLFW_KEY_R:
                    if (rebootTimer < 0) rebootTimer = 0;
                    return true;

                case GLFW.GLFW_KEY_V:
                    // Ctrl+V for paste
                    String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
                    if (clipboard != null) {
                        // Clip to the first occurrence of \r or \n
                        int newLineIndex1 = clipboard.indexOf("\r");
                        int newLineIndex2 = clipboard.indexOf("\n");
                        if (newLineIndex1 >= 0 && newLineIndex2 >= 0) {
                            clipboard = clipboard.substring(0, Math.min(newLineIndex1, newLineIndex2));
                        } else if (newLineIndex1 >= 0) {
                            clipboard = clipboard.substring(0, newLineIndex1);
                        } else if (newLineIndex2 >= 0) {
                            clipboard = clipboard.substring(0, newLineIndex2);
                        }

                        // Filter the string
                        clipboard = SharedConstants.filterText(clipboard);
                        if (!clipboard.isEmpty()) {
                            // Clip to 512 characters and queue the event
                            if (clipboard.length() > 512) clipboard = clipboard.substring(0, 512);
                            input.queueEvent("paste", new Object[]{clipboard});
                        }

                        return true;
                    }
            }
        }

        if (key >= 0 && terminateTimer < 0 && rebootTimer < 0 && shutdownTimer < 0) {
            // Queue the "key" event and add to the down set
            boolean repeat = keysDown.get(key);
            keysDown.set(key);
            input.keyDown(key, repeat);
        }

        return true;
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        // Queue the "key_up" event and remove from the down set
        if (key >= 0 && keysDown.get(key)) {
            keysDown.set(key, false);
            input.keyUp(key);
        }

        switch (key) {
            case GLFW.GLFW_KEY_T:
                terminateTimer = -1;
                break;
            case GLFW.GLFW_KEY_R:
                rebootTimer = -1;
                break;
            case GLFW.GLFW_KEY_S:
                shutdownTimer = -1;
                break;
            case GLFW.GLFW_KEY_LEFT_CONTROL:
            case GLFW.GLFW_KEY_RIGHT_CONTROL:
                terminateTimer = rebootTimer = shutdownTimer = -1;
                break;
        }

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        input.mouseClick(button + 1, (int) mouseX, (int) mouseY);

        lastMouseButton = button;
        lastMouseX = (int) mouseX;
        lastMouseY = (int) mouseY;

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (lastMouseButton == button) {
            input.mouseUp(lastMouseButton + 1, (int) mouseX, (int) mouseY);
            lastMouseButton = -1;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double v2, double v3) {
        if (button == lastMouseButton && (mouseX != lastMouseX || mouseY != lastMouseY)) {
            input.mouseDrag(button + 1, (int) mouseX, (int) mouseY);
            lastMouseX = (int) mouseX;
            lastMouseY = (int) mouseY;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        input.mouseScroll(delta < 0 ? 1 : -1, (int) mouseX, (int) mouseY);

        lastMouseX = (int) mouseX;
        lastMouseY = (int) mouseY;

        return true;
    }

    @Override
    public int getSizeX() {
        return 256;
    }

    @Override
    public int getSizeY() {
        return 256;
    }

    @Override
    public ResourceLocation getTexture() {
        return null;
    }
}
