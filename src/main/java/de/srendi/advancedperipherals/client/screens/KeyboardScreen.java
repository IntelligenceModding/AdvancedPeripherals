// SPDX-FileCopyrightText: 2021 The CC: Tweaked Developers
// SPDX-FileCopyrightText: 2025 The AdvancedPeripheral Developers
//
// SPDX-License-Identifier: MPL-2.0

package de.srendi.advancedperipherals.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import dan200.computercraft.client.gui.ClientComputerActions;
import dan200.computercraft.client.gui.ClientComputerInput;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import dan200.computercraft.core.input.UserComputerInput;
import dan200.computercraft.core.terminal.Terminal;
import de.srendi.advancedperipherals.client.ClientWorker;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.network.toserver.KeyboardMouseClickPacket;
import de.srendi.advancedperipherals.common.network.toserver.KeyboardMouseMovePacket;
import de.srendi.advancedperipherals.common.network.toserver.KeyboardMouseScrollPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * A simple screen but without any rendering calls. Used to unlock the mouse so we can freely write stuff
 * <p>
 * We just create a terminal which is used to forward all the key presses and mouse clicks but we don't render it.
 */
public class KeyboardScreen extends Screen implements MenuAccess<KeyboardContainer> {

    protected final KeyboardContainer keyboardContainer;
    protected final UserComputerInput input;
    protected final ClientComputerActions actions;
    private final Terminal terminalData;
    private TerminalWidget terminal;
    private MouseState mouseState = MouseState.RELEASED;
    private boolean captureMouse;
    private boolean regrabingMouse;
    private final byte[] lastPosLock = new byte[0];
    private double lastX = 0;
    private double lastY = 0;
    private double lastScrollX = 0;
    private double lastScrollY = 0;

    public KeyboardScreen(KeyboardContainer keyboardContainer, Inventory inv, Component titleIn) {
        super(titleIn);
        this.keyboardContainer = keyboardContainer;
        this.input = new UserComputerInput(new ClientComputerInput(keyboardContainer), false, 0, 0);
        this.actions = new ClientComputerActions(keyboardContainer);
        this.terminalData = new Terminal(0, 0, false);
    }

    @Override
    public KeyboardContainer getMenu() {
        return this.keyboardContainer;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int x, int y, float partialTicks) {
        super.render(graphics, x, y, partialTicks);

        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();

        // float scale = 2f;
        // // Make the text a bit smaller on small screens
        // if (screenWidth <= 1080) {
        //     scale = 1f;
        // }
        // poseStack.scale(scale, scale, 1);

        Component text = Component.translatable("text.advancedperipherals.keyboard.close");
        graphics.drawCenteredString(minecraft.font, text, screenWidth / 2, 1, 0xFFFFFF);
    }

    @Override
    protected void init() {
        if (this.isCapturingMouse()) {
            this.grabMouse();
        } else {
            this.grabMouseWithControl();
        }
        KeyMapping.releaseAll();

        super.init();
        // this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        this.terminal = addWidget(new TerminalWidget(terminalData, this.input, this.actions, 0, 0));
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
        if (this.minecraft.player != null) {
            this.keyboardContainer.removed(this.minecraft.player);
        }
        // this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
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
                PacketDistributor.sendToServer(new KeyboardMouseMovePacket(dx, dy));
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
        PacketDistributor.sendToServer(new KeyboardMouseClickPacket(button, false));
        return true;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (this.mouseState != MouseState.CAPTURE) {
            return false;
        }
        PacketDistributor.sendToServer(new KeyboardMouseClickPacket(button, true));
        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        this.lastScrollX += scrollX;
        this.lastScrollY += scrollY;
        int scrolledX = (int) this.lastScrollX;
        int scrolledY = (int) this.lastScrollY;
        if (scrolledX == 0 && scrolledY == 0) {
            return true;
        }
        if (this.mouseState == MouseState.CAPTURE) {
            ClientWorker.put("mouse_scroll", () -> {
                if (this.mouseState != MouseState.CAPTURE) {
                    return;
                }
                this.lastScrollX -= scrolledX;
                this.lastScrollY -= scrolledY;
                PacketDistributor.sendToServer(new KeyboardMouseScrollPacket(scrolledY, scrolledX));
            });
        } else {
            this.lastScrollX -= scrolledX;
            this.lastScrollY -= scrolledY;
            minecraft.player.getInventory().swapPaint(scrolledY == 0 ? -scrolledX : scrolledY);
        }
        return true;
    }

    @Override
    public final boolean keyPressed(int key, int scancode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            } else {
                super.onClose();
            }
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
