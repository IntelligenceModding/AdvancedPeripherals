package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.LineObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import java.util.List;

public class LineRenderer implements ITwoDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, GuiGraphics gui, PoseStack poseStack, DeltaTracker partialTick, int screenWidth, int screenHeight) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
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
                BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
                bufferBuilder.addVertex(matrix, x1, y1, 0).setColor(red, green, blue, alpha);
                bufferBuilder.addVertex(matrix, x2, y2, 0).setColor(red, green, blue, alpha);
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

                continue; // Skip the rest of the loop for this object
            }

            // Pixelated lines
            BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            // Calculate the delta for each axis
            float dx = x2 - x1;
            float dy = y2 - y1;
            float dz = z2 - z1;

            final float pixelSize = line.pixelSize;

            float maxDim = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
            int numPixels = (int) Math.ceil(maxDim / pixelSize);

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
                currentX = Math.round(currentX / pixelSize) * pixelSize;
                currentY = Math.round(currentY / pixelSize) * pixelSize;
                currentZ = Math.round(currentZ / pixelSize) * pixelSize;

                float pX1 = currentX;
                float pY1 = currentY;
                float pZ1 = currentZ;

                float pX2 = currentX + pixelSize;
                float pY2 = currentY + pixelSize;
                float pZ2 = currentZ;

                bufferBuilder.addVertex(matrix, pX1, pY2, pZ1).setColor(red, green, blue, alpha); // Bottom-left
                bufferBuilder.addVertex(matrix, pX2, pY2, pZ1).setColor(red, green, blue, alpha); // Bottom-right
                bufferBuilder.addVertex(matrix, pX2, pY1, pZ2).setColor(red, green, blue, alpha); // Top-right
                bufferBuilder.addVertex(matrix, pX1, pY1, pZ2).setColor(red, green, blue, alpha); // Top-left
            }
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        }
    }
}
