package de.srendi.advancedperipherals.common.argoggles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;

public class ARRenderHelper extends AbstractGui {

    private static ARRenderHelper instance = new ARRenderHelper();

    public static void drawRightboundString(MatrixStack matrixStack, FontRenderer fontRenderer, String text, int x,
                                            int y, int color) {
        drawString(matrixStack, fontRenderer, text, x - fontRenderer.width(text), y, color);
    }

    public static ARRenderHelper getInstance() {
        return instance;
    }

    public static int fixAlpha(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }

    @Override
    public void hLine(MatrixStack matrixStack, int minX, int maxX, int y, int color) {
        color = ARRenderHelper.fixAlpha(color);
        super.hLine(matrixStack, minX, maxX, y, color);
    }

    @Override
    public void vLine(MatrixStack matrixStack, int x, int minY, int maxY, int color) {
        color = ARRenderHelper.fixAlpha(color);
        super.vLine(matrixStack, x, minY, maxY, color);
    }

    @Override
    protected void fillGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        colorFrom = ARRenderHelper.fixAlpha(colorFrom);
        colorTo = ARRenderHelper.fixAlpha(colorTo);
        super.fillGradient(matrixStack, x1, y1, x2, y2, colorFrom, colorTo);
    }

    public void drawCircle(MatrixStack matrixStack, int centerX, int centerY, float radius, int color) {
        color = fixAlpha(color);

        final int n_segments = 360;
        Matrix4f matrix = matrixStack.last().pose();

        float z = this.getBlitOffset();
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < n_segments; i++) {
            double angle = 2 * Math.PI * i / n_segments;
            float xd = (float) (radius * Math.sin(angle));
            float yd = (float) (radius * Math.cos(angle));
            bufferbuilder.vertex(matrix, centerX + xd, centerY + yd, z).color(r, g, b, a).endVertex();
        }
        bufferbuilder.end();
        WorldVertexBufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void fillCircle(MatrixStack matrixStack, int centerX, int centerY, float radius, int color) {
        color = fixAlpha(color);

        final float increment = 0.5f;
        Matrix4f matrix = matrixStack.last().pose();

        float z = this.getBlitOffset();
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (float y = centerY - radius; y < centerY + radius; y += increment) {
            float yd = centerY - y;
            float xd = (float) Math.sqrt(Math.pow(radius, 2) - Math.pow(yd, 2));
            bufferbuilder.vertex(matrix, centerX - xd, y, z).color(r, g, b, a).endVertex();
            bufferbuilder.vertex(matrix, centerX + xd, y, z).color(r, g, b, a).endVertex();
        }
        bufferbuilder.end();
        WorldVertexBufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void drawItemIcon(MatrixStack matrixStack, ItemRenderer itemRenderer, String item, int x, int y) {
        ItemStack stack = new ItemStack(ItemUtil.getRegistryEntry(item, ForgeRegistries.ITEMS));
        itemRenderer.renderGuiItem(stack, x, y);
    }
}
