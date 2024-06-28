package de.srendi.advancedperipherals.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SaddleTurtleScreen extends GuiComponent implements IGuiOverlay {
    public static final String ID = "saddle_turtle_overlay";

    private static final long ACTIVE_TIMEOUT = 5000;

    private ForgeGui gui;
    private int screenWidth = 0;
    private int screenHeight = 0;

    private int fuelLevel = 0;
    private int fuelLimit = 0;
    private int barColor = 0;
    private long lastActived = 0;

    public SaddleTurtleScreen() {}

    protected Font getFont() {
        return this.gui.getMinecraft().font;
    }

    protected int textWidth(String text) {
        return getFont().width(text);
    }

    protected int textWidth(FormattedText text) {
        return getFont().width(text);
    }

    protected int textWidth(FormattedCharSequence text) {
        return getFont().width(text);
    }

    public static boolean isPlayerMountedOnTurtle() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.getRootVehicle() instanceof TurtleSeatEntity;
    }

    public boolean shouldRenderFuelBar() {
        if (this.lastActived == 0) {
            return false;
        }
        if (!isPlayerMountedOnTurtle()) {
            this.hide();
            return false;
        }
        return this.lastActived + ACTIVE_TIMEOUT > System.currentTimeMillis();
    }

    public void hide() {
        this.fuelLevel = 0;
        this.fuelLimit = 0;
        this.barColor = 0;
        this.lastActived = 0;
    }

    public void keepAlive() {
        this.lastActived = System.currentTimeMillis();
    }

    public void setFuelLevel(int level) {
        if (level < 0) {
            level = 0;
        }
        if (this.fuelLevel != level) {
            this.fuelLevel = level;
            this.keepAlive();
        }
    }

    public void setFuelLimit(int limit) {
        if (this.fuelLimit != limit) {
            this.fuelLimit = limit;
            this.keepAlive();
        }
    }

    public void setBarColor(int color) {
        if (this.barColor != color) {
            this.barColor = color;
            this.keepAlive();
        }
    }

    private void renderFuelBar(PoseStack stack) {
        // TODO: use a better looking bar here, and/or find someway to change the bar's color
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        int fontColor = 0x80ff20;

        int width = 182;
        int left = this.screenWidth / 2 - 91;
        int top = this.screenHeight - 32 + 3;
        this.blit(stack, left, top, 0, 64, width, 5);
        if (fuelLevel > 0 && fuelLimit > 0) {
            int progWidth = fuelLevel * width / fuelLimit;
            this.blit(stack, left, top, 0, 69, progWidth, 5);
        }

        String text = fuelLimit > 0 ? String.format("%d / %d", fuelLevel, fuelLimit) : "Infinity";
        int x = (this.screenWidth - getFont().width(text)) / 2;
        int y = this.screenHeight - 31;
        getFont().draw(stack, text, (float)(x + 1), (float) y, 0);
        getFont().draw(stack, text, (float)(x - 1), (float) y, 0);
        getFont().draw(stack, text, (float) x, (float)(y + 1), 0);
        getFont().draw(stack, text, (float) x, (float)(y - 1), 0);
        getFont().draw(stack, text, (float) x, (float) y, fontColor);
    }

    private void renderDismountHint(PoseStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        Component name = Component.translatable("block.computercraft.turtle_normal.upgraded", Component.translatable("turtle.advancedperipherals.saddle_turtle"));
        // TODO: get and render turtle's label if exists
        Component text = Component.translatable("text.advancedperipherals.saddle_turtle_dismount_hint",
            name, minecraft.options.keyShift.getTranslatedKeyMessage(), minecraft.options.keyInventory.getTranslatedKeyMessage());
        float top = 10;
        float x = (float)(this.screenWidth / 2 - textWidth(text) / 2);
        getFont().drawShadow(stack, text, x, top, 0xffffff);
    }

    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        if (!isPlayerMountedOnTurtle()) {
            return;
        }

        this.gui = gui;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        if (this.shouldRenderFuelBar()) {
            this.renderFuelBar(poseStack);
        }
        this.renderDismountHint(poseStack);
    }
}
