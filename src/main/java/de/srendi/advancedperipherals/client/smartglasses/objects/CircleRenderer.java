package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.CircleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class CircleRenderer implements IObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        for (RenderableObject object : objects) {

            CircleObject circle = (CircleObject) object;

            float alpha = object.opacity;
            float red = (float) (object.color >> 16 & 255) / 255.0F;
            float green = (float) (object.color >> 8 & 255) / 255.0F;
            float blue = (float) (object.color & 255) / 255.0F;

            drawCircle(poseStack, circle.x, circle.y, circle.radius, 120, red, green, blue, alpha);
        }
    }

    public void drawCircle(PoseStack poseStack, float cx, float cy, float r, int numSegments, float red, float green, float blue, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        Matrix4f matrix = poseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        bufferbuilder.vertex(matrix, cx, cy, 1f).color(red, green, blue, alpha).endVertex();

        double angle = Math.PI * 2 / numSegments;

        for (int i = 0; i <= numSegments; i++) {
            double x = cx + r * Math.sin(i * angle);
            double y = cy + r * Math.cos(i * angle);

            bufferbuilder.vertex(matrix, (float) x, (float) y, 0f).color(red, green, blue, alpha).endVertex();
        }

        BufferUploader.drawWithShader(bufferbuilder.end());
    }



}
