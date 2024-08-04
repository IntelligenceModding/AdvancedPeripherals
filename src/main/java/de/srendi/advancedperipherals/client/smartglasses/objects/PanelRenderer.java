package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class PanelRenderer implements IObjectRenderer {

    @Override
    public void render(RenderableObject object, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        float alpha = object.opacity;
        float red = (float) (object.color >> 16 & 255) / 255.0F;
        float green = (float) (object.color >> 8 & 255) / 255.0F;
        float blue = (float) (object.color & 255) / 255.0F;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        Matrix4f matrix = poseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, (float) object.x, (float) object.maxY, 0f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float) object.maxX, (float) object.maxY, 0f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float) object.maxX, (float) object.y, 0f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float) object.x, (float) object.y, 0f).color(red, green, blue, alpha).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
