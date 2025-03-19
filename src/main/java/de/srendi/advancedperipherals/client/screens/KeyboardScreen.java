package de.srendi.advancedperipherals.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import dan200.computercraft.client.gui.ClientInputHandler;
import dan200.computercraft.client.gui.widgets.WidgetTerminal;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.core.InputHandler;
import de.srendi.advancedperipherals.client.ClientWorker;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toserver.KeyboardMouseClickPacket;
import de.srendi.advancedperipherals.common.network.toserver.KeyboardMouseMovePacket;
import de.srendi.advancedperipherals.common.network.toserver.KeyboardMouseScrollPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * A simple screen but without any rendering calls. Used to unlock the mouse so we can freely write stuff
 * <p>
 * We just create a terminal which is used to forward all the key presses and mouse clicks but we don't render it.
 */
public class KeyboardScreen extends Screen implements MenuAccess<KeyboardContainer> {

    protected final KeyboardContainer keyboardContainer;
    protected final InputHandler input;
    private final Terminal terminalData;
    private WidgetTerminal terminal;
    private MouseState mouseState = MouseState.RELEASED;
    private boolean captureMouse;
    private boolean regrabingMouse;
    private byte[] lastPosLock = new byte[0];
    private double lastX = 0;
    private double lastY = 0;
    private double lastScroll = 0;

    public KeyboardScreen(KeyboardContainer keyboardContainer, Inventory inv, Component titleIn) {
        super(titleIn);
        this.keyboardContainer = keyboardContainer;
        this.input = new ClientInputHandler(keyboardContainer);
        this.terminalData = new Terminal(0, 0, false);
    }

    @Override
    public KeyboardContainer getMenu() {
        return this.keyboardContainer;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int x, int y, float partialTicks) {
        super.render(poseStack, x, y, partialTicks);

        Minecraft minecraft = Minecraft.getInstance();
        float scale = 2f;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        // Make the text a bit smaller on small screens
        if (screenWidth <= 1080)
            scale = 1f;

        poseStack.scale(scale, scale, 1);
        Component text = Component.translatable("text.advancedperipherals.keyboard.close");
        float textX = (screenWidth / 2f - minecraft.font.width(text) * scale / 2f) / scale;
        minecraft.font.drawShadow(poseStack, text, textX, 1, 0xFFFFFF);
    }

    @Override
    protected void init() {
        if (this.isCapturingMouse()) {
            this.grabMouse();
        } else {
            this.grabMouseWithControl();
        }
        this.passEvents = true;
        KeyMapping.releaseAll();

        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        this.terminal = addWidget(new WidgetTerminal(terminalData, new ClientInputHandler(this.keyboardContainer), 0, 0));
        this.terminal.visible = false;
        this.terminal.active = false;
        setFocused(this.terminal);
    }

    @Override
    public final void removed() {
        if (this.regrabingMouse) {
            return;
        }
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void onClose() {
        // Don't allow closing using standard keys like E. Closing using ESCAPE is still possible due to the keyPressed method
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void mouseMoved(double x, double y) {
        if (this.mouseState != MouseState.CAPTURE) {
            return;
        }
        ClientWorker.put("mouse_move", () -> {
            synchronized (this.lastPosLock) {
                double dx = x - this.lastX;
                double dy = y - this.lastY;
                APNetworking.sendToServer(new KeyboardMouseMovePacket(dx, dy));
                this.lastX = x;
                this.lastY = y;
            }
        });
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.mouseState != MouseState.CAPTURE) {
            return false;
        }
        APNetworking.sendToServer(new KeyboardMouseClickPacket(button, false));
        return true;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (this.mouseState != MouseState.CAPTURE) {
            return false;
        }
        APNetworking.sendToServer(new KeyboardMouseClickPacket(button, true));
        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double direction) {
        this.lastScroll += direction;
        int scrolled = (int) this.lastScroll;
        if (scrolled == 0) {
            return true;
        }
        if (this.mouseState == MouseState.CAPTURE) {
            ClientWorker.put("mouse_scroll", () -> {
                if (this.mouseState != MouseState.CAPTURE) {
                    return;
                }
                this.lastScroll -= scrolled;
                APNetworking.sendToServer(new KeyboardMouseScrollPacket(scrolled));
            });
        } else {
            this.lastScroll -= scrolled;
            minecraft.player.getInventory().swapPaint(scrolled);
        }
        return true;
    }

    @Override
    public final boolean keyPressed(int key, int scancode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            super.onClose();
            return true;
        }
        // Forward the tab key to the terminal, rather than moving between controls.
        if (key == GLFW.GLFW_KEY_TAB && getFocused() != null && getFocused() == terminal) {
            return getFocused().keyPressed(key, scancode, modifiers);
        }

        return super.keyPressed(key, scancode, modifiers);
    }

    public boolean isCapturingMouse() {
        return this.captureMouse;
    }

    public void setCaptureMouse(boolean enable) {
        this.captureMouse = enable;
        if (enable) {
            this.grabMouse();
        } else {
            this.grabMouseWithControl();
        }
    }

    private void grabMouseWithControl() {
        if (this.mouseState == MouseState.NORMAL) {
            return;
        }
        this.releaseMouse();
        this.regrabingMouse = true;
        this.minecraft.mouseHandler.grabMouse();
        this.regrabingMouse = false;
        this.minecraft.screen = this;
        this.mouseState = MouseState.NORMAL;
    }

    private void grabMouse() {
        if (this.minecraft.mouseHandler.isMouseGrabbed()) {
            this.minecraft.mouseHandler.releaseMouse();
        }
        Window window = this.minecraft.getWindow();
        synchronized (this.lastPosLock) {
            this.lastX = window.getScreenWidth() / 2;
            this.lastY = window.getScreenHeight() / 2;
            InputConstants.grabOrReleaseMouse(window.getWindow(), InputConstants.CURSOR_DISABLED, this.lastX, this.lastY);
        }
        this.mouseState = MouseState.CAPTURE;
    }

    private void releaseMouse() {
        if (this.mouseState == MouseState.RELEASED) {
            return;
        }
        if (this.minecraft.mouseHandler.isMouseGrabbed()) {
            this.minecraft.mouseHandler.releaseMouse();
            return;
        }
        Window window = this.minecraft.getWindow();
        synchronized (this.lastPosLock) {
            this.lastX = window.getScreenWidth() / 2;
            this.lastY = window.getScreenHeight() / 2;
            InputConstants.grabOrReleaseMouse(window.getWindow(), InputConstants.CURSOR_NORMAL, this.lastX, this.lastY);
        }
        this.mouseState = MouseState.RELEASED;
    }

    private enum MouseState {
        RELEASED, NORMAL, CAPTURE
    }
}
