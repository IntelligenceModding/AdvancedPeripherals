package de.srendi.advancedperipherals.common.container.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class BaseScreen<T extends BaseContainer> extends ContainerScreen<T> {

    public BaseScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        imageWidth = getSizeX();
        imageHeight = getSizeY();
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        renderTooltip(matrixStack, x, y);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        minecraft.getTextureManager().bind(getTexture());

        int xPos = (width - imageWidth) / 2;
        int yPos = (height - imageHeight) / 2;

        blit(matrixStack, xPos, yPos, 0, 0, imageWidth, imageHeight);
    }

    public abstract int getSizeX();

    public abstract int getSizeY();

    public abstract ResourceLocation getTexture();
}
