package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.LineObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class LineRenderer implements ITwoDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        Matrix4f matrix = poseStack.last().pose();

        for (RenderableObject obj : objects) {

            LineObject line = (LineObject) obj;

            float alpha = obj.opacity;
            float red = RenderUtil.getRed(obj.color);
            float green = RenderUtil.getGreen(obj.color);
            float blue = RenderUtil.getBlue(obj.color);

            // Start and end points of the line
            float x1 = obj.x;
            float y1 = obj.y;
            float z1 = obj.z;

            float x2 = obj.maxX;
            float y2 = obj.maxY;
            float z2 = obj.maxZ;

            // Normal, smooth lines
            if (!line.pixelated) {
                bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                bufferbuilder.vertex(matrix, x1, y1, 0).color(red, green, blue, alpha).endVertex();
                bufferbuilder.vertex(matrix, x2, y2, 0).color(red, green, blue, alpha).endVertex();
                BufferUploader.drawWithShader(bufferbuilder.end());

                continue; // Skip the rest of the loop for this object
            }

            // Pixelated lines
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            // Calculate the delta for each axis
            float dx = x2 - x1;
            float dy = y2 - y1;
            float dz = z2 - z1;

            final float PIXEL_SIZE = line.pixelSize;

            float maxDim = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
            int numPixels = (int) Math.ceil(maxDim / PIXEL_SIZE);

            if (numPixels == 0) {
                numPixels = 1; // Always draw at least one pixel for very short lines
            }

            // Iterate and draw a square for each "pixel"
            for (int i = 0; i <= numPixels; i++) {
                float t = (float) i / numPixels; // Interpolation factor (0.0 to 1.0)

                // Calculate the exact point on the line
                float currentX = x1 + dx * t;
                float currentY = y1 + dy * t;
                float currentZ = z1 + dz * t;

                // Snap current point to the nearest pixel grid for consistent placement.
                // This is key for placing pixels at corners or full side of each other.
                currentX = Math.round(currentX / PIXEL_SIZE) * PIXEL_SIZE;
                currentY = Math.round(currentY / PIXEL_SIZE) * PIXEL_SIZE;
                currentZ = Math.round(currentZ / PIXEL_SIZE) * PIXEL_SIZE;

                float p_x1 = currentX;
                float p_y1 = currentY;
                float p_z1 = currentZ;

                float p_x2 = currentX + PIXEL_SIZE;
                float p_y2 = currentY + PIXEL_SIZE;
                float p_z2 = currentZ;

                bufferbuilder.vertex(matrix, p_x1, p_y2, p_z1).color(red, green, blue, alpha).endVertex(); // Bottom-left
                bufferbuilder.vertex(matrix, p_x2, p_y2, p_z1).color(red, green, blue, alpha).endVertex(); // Bottom-right
                bufferbuilder.vertex(matrix, p_x2, p_y1, p_z2).color(red, green, blue, alpha).endVertex(); // Top-right
                bufferbuilder.vertex(matrix, p_x1, p_y1, p_z2).color(red, green, blue, alpha).endVertex(); // Top-left
            }
            BufferUploader.drawWithShader(bufferbuilder.end());
        }
    }
}
