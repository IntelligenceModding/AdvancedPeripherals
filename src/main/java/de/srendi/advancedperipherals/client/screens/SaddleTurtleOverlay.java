package de.srendi.advancedperipherals.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SaddleTurtleOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = AdvancedPeripherals.getRL("saddle_turtle_overlay");

    private static final long ACTIVE_TIMEOUT = 5000;

    private static final ResourceLocation JUMP_BAR_COOLDOWN_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_cooldown");
    private static final ResourceLocation JUMP_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_progress");

    private int fuelLevel = 0;
    private int fuelLimit = 0;
    private int barColor = 0;
    private long lastActived = 0;

    public SaddleTurtleOverlay() {}

    public boolean isPlayerControllingTurtle() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.getVehicle() instanceof TurtleSeatEntity;
    }

    public boolean isPlayerMountedOnTurtle() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.getRootVehicle() instanceof TurtleSeatEntity;
    }

    public boolean shouldRenderFuelBar() {
        if (this.lastActived == 0) {
            return false;
        }
        if (!this.isPlayerMountedOnTurtle()) {
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

    private void renderFuelBar(GuiGraphics gui) {
        Font font = gui.minecraft.font;
        // TODO: use a better looking bar here, and/or find someway to change the bar's color
        int fontColor = 0x80ff20;

        int width = 182;
        int left = gui.guiWidth() / 2 - 91;
        int top = gui.guiHeight() - 32 + 3;
        RenderSystem.enableBlend();
        gui.blitSprite(JUMP_BAR_COOLDOWN_SPRITE, left, top, width, 5);
        if (fuelLevel > 0 && fuelLimit > 0) {
            int progWidth = fuelLevel * width / fuelLimit;
            gui.blitSprite(JUMP_BAR_PROGRESS_SPRITE, width, 5, 0, 0, left, top, progWidth, 5);
        }
        RenderSystem.disableBlend();

        String text = fuelLimit > 0 ? String.format("%d / %d", fuelLevel, fuelLimit) : "Infinity";
        int x = (gui.guiWidth() - font.width(text)) / 2;
        int y = gui.guiHeight() - 31;
        gui.drawString(font, text, (float)(x + 1), (float) y, 0, false);
        gui.drawString(font, text, (float)(x - 1), (float) y, 0, false);
        gui.drawString(font, text, (float) x, (float)(y + 1), 0, false);
        gui.drawString(font, text, (float) x, (float)(y - 1), 0, false);
        gui.drawString(font, text, (float) x, (float) y, fontColor, false);
    }

    private void renderDismountHint(GuiGraphics gui) {
        Minecraft minecraft = gui.minecraft;
        Font font = minecraft.font;
        Component name = Component.translatable("block.computercraft.turtle_normal.upgraded", Component.translatable("turtle.advancedperipherals.saddle_turtle"));
        // TODO: get and render turtle's label if exists
        Component text = Component.translatable("text.advancedperipherals.saddle_turtle.dismount_hint",
            name, minecraft.options.keyShift.getTranslatedKeyMessage(), minecraft.options.keyInventory.getTranslatedKeyMessage());
        int top = 10;
        int x = gui.guiWidth() / 2 - font.width(text) / 2;
        gui.drawString(font, text, x, top, 0xffffff, true);
    }

    @Override
    public void render(@NotNull GuiGraphics gui, @NotNull DeltaTracker deltaTracker) {
        if (!this.isPlayerMountedOnTurtle()) {
            return;
        }

        if (this.shouldRenderFuelBar()) {
            this.renderFuelBar(gui);
        }
        if (this.isPlayerControllingTurtle()) {
            this.renderDismountHint(gui);
        }
    }
}
