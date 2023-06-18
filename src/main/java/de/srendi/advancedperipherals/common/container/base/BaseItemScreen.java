package de.srendi.advancedperipherals.common.container.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class BaseItemScreen<T extends BaseItemContainer> extends AbstractContainerScreen<T> {

    public BaseItemScreen(T screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);

        imageWidth = getSizeX();
        imageHeight = getSizeY();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, x, y, partialTicks);
        renderTooltip(guiGraphics, x, y);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        int xPos = (width - imageWidth) / 2;
        int yPos = (height - imageHeight) / 2;

        guiGraphics.blit(getTexture(), xPos, yPos, 0, 0, imageWidth, imageHeight);
    }

    public abstract int getSizeX();

    public abstract int getSizeY();

    public abstract ResourceLocation getTexture();
}
