package de.srendi.advancedperipherals.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

public class SaddleTurtleOverlay implements LayeredDraw.Layer {
    public static final ResourceLocation ID = AdvancedPeripherals.getRL("saddle_turtle_overlay");

    private static final long ACTIVE_TIMEOUT = 5000;

    public static final ResourceLocation GUI_SPRITE_ATLAS = ResourceLocation.withDefaultNamespace("textures/atlas/gui.png");

    private GuiGraphics gui;
    private int screenWidth = 0;
    private int screenHeight = 0;

    private int fuelLevel = 0;
    private int fuelLimit = 0;
    private int barColor = 0;
    private long lastActived = 0;

    public SaddleTurtleOverlay() {}

    protected Font getFont() {
        return this.gui.minecraft.font;
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

    private void renderFuelBar(PoseStack stack) {
        // TODO: use a better looking bar here, and/or find someway to change the bar's color
        RenderSystem.setShaderTexture(0, GUI_SPRITE_ATLAS);
        int fontColor = 0x80ff20;

        int width = 182;
        int left = this.screenWidth / 2 - 91;
        int top = this.screenHeight - 32 + 3;
        gui.blit(GUI_SPRITE_ATLAS, left, top, 0, 64, width, 5);
        if (fuelLevel > 0 && fuelLimit > 0) {
            int progWidth = fuelLevel * width / fuelLimit;
            gui.blit( GUI_SPRITE_ATLAS, left, top, 0, 69, progWidth, 5);
        }

        String text = fuelLimit > 0 ? String.format("%d / %d", fuelLevel, fuelLimit) : "Infinity";
        int x = (this.screenWidth - getFont().width(text)) / 2;
        int y = this.screenHeight - 31;
        getFont().drawInBatch(text, (float)(x + 1), (float) y, 0, false, stack.last().pose(), gui.bufferSource(), Font.DisplayMode.NORMAL, 0, 0);
        getFont().drawInBatch(text, (float)(x - 1), (float) y, 0, false, stack.last().pose(), gui.bufferSource(), Font.DisplayMode.NORMAL, 0, 0);
        getFont().drawInBatch(text, (float) x, (float)(y + 1), 0, false, stack.last().pose(), gui.bufferSource(), Font.DisplayMode.NORMAL, 0, 0);
        getFont().drawInBatch(text, (float) x, (float)(y - 1), 0, false, stack.last().pose(), gui.bufferSource(), Font.DisplayMode.NORMAL, 0, 0);
        getFont().drawInBatch(text, (float) x, (float) y, fontColor, false, stack.last().pose(), gui.bufferSource(), Font.DisplayMode.NORMAL, 0, 0);
    }

    private void renderDismountHint(PoseStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        Component name = Component.translatable("block.computercraft.turtle_normal.upgraded", Component.translatable("turtle.advancedperipherals.saddle_turtle"));
        // TODO: get and render turtle's label if exists
        Component text = Component.translatable("text.advancedperipherals.saddle_turtle.dismount_hint",
            name, minecraft.options.keyShift.getTranslatedKeyMessage(), minecraft.options.keyInventory.getTranslatedKeyMessage());
        float top = 10;
        float x = (float)(this.screenWidth / 2 - textWidth(text) / 2);
        getFont().drawInBatch(text, x, top, 0xffffff, true, stack.last().pose(), gui.bufferSource(), Font.DisplayMode.NORMAL, 0, 0);
    }

    @Override
    public void render(@NonNull GuiGraphics guiGraphics, @NonNull DeltaTracker deltaTracker) {
        if (!this.isPlayerMountedOnTurtle()) {
            return;
        }

        this.gui = guiGraphics;
        this.screenWidth = guiGraphics.guiWidth();
        this.screenHeight = guiGraphics.guiHeight();

        if (this.shouldRenderFuelBar()) {
            this.renderFuelBar(guiGraphics.pose());
        }
        if (this.isPlayerControllingTurtle()) {
            this.renderDismountHint(guiGraphics.pose());
        }
    }
}
