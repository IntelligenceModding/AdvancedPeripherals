package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.LineObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

import java.util.List;

public class LineRenderer implements ITwoDObjectRenderer<LineObject> {
    @Override
    public void renderBatch(List<LineObject> objects, GuiGraphics gui, DeltaTracker partialTick) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.QUADS_2D);
        for (LineObject line : objects) {
            float width = line.width;
            float halfWidth = line.width / 2;
            if (halfWidth == 0) {
                continue;
            }

            float alpha = line.opacity;
            float red = RenderUtil.getRed(line.color);
            float green = RenderUtil.getGreen(line.color);
            float blue = RenderUtil.getBlue(line.color);

            // Start and end points of the line
            float x1 = line.x;
            float y1 = line.y;
            float x2 = line.endX;
            float y2 = line.endY;
            float z = line.z;

            float dx = x2 - x1;
            float dy = y2 - y1;

            gui.pose().pushPose();
            gui.pose().rotateAround(line.getRotation(), (x1 + x2) / 2, (y1 + y2) / 2, z);

            Matrix4f matrix = gui.pose().last().pose();
            gui.pose().popPose();

            // Normal, smooth lines
            if (!line.pixelated) {
                if (y1 == y2) {
                    bufferBuilder.addVertex(matrix, x1, y1 - halfWidth, z).setColor(red, green, blue, alpha);
                    bufferBuilder.addVertex(matrix, x1, y1 + halfWidth, z).setColor(red, green, blue, alpha);
                    bufferBuilder.addVertex(matrix, x2, y1 + halfWidth, z).setColor(red, green, blue, alpha);
                    bufferBuilder.addVertex(matrix, x2, y1 - halfWidth, z).setColor(red, green, blue, alpha);
                    continue;
                }
                if (x1 == x2) {
                    bufferBuilder.addVertex(matrix, x1 - halfWidth, y1, z).setColor(red, green, blue, alpha);
                    bufferBuilder.addVertex(matrix, x1 + halfWidth, y1, z).setColor(red, green, blue, alpha);
                    bufferBuilder.addVertex(matrix, x1 + halfWidth, y2, z).setColor(red, green, blue, alpha);
                    bufferBuilder.addVertex(matrix, x1 - halfWidth, y2, z).setColor(red, green, blue, alpha);
                    continue;
                }
                float l = (float) Math.sqrt(dx * dx + dy * dy);
                float rx = -dy / l * halfWidth, ry = dx / l * halfWidth;
                bufferBuilder.addVertex(matrix, x1 - rx, y1 - ry, z).setColor(red, green, blue, alpha);
                bufferBuilder.addVertex(matrix, x1 + rx, y1 + ry, z).setColor(red, green, blue, alpha);
                bufferBuilder.addVertex(matrix, x2 + rx, y2 + ry, z).setColor(red, green, blue, alpha);
                bufferBuilder.addVertex(matrix, x2 - rx, y2 - ry, z).setColor(red, green, blue, alpha);
                continue;
            }

            // Pixelated lines

            float maxDim = Math.max(Math.abs(dx), Math.abs(dy));
            int numPixels = (int) Math.ceil(maxDim / width);

            if (numPixels == 0) {
                numPixels = 1; // Always draw at least one pixel for very short lines
            }

            // Iterate and draw a square for each "pixel"
            for (int i = 0; i <= numPixels; i++) {
                float t = (float) i / numPixels; // Interpolation factor (0.0 to 1.0)

                // Calculate the exact point on the line
                float currentX = x1 + dx * t;
                float currentY = y1 + dy * t;

                // Snap current point to the nearest pixel grid for consistent placement.
                // This is key for placing pixels at corners or full side of each other.
                currentX = Math.round(currentX / width) * width;
                currentY = Math.round(currentY / width) * width;

                float pX1 = currentX;
                float pY1 = currentY;

                float pX2 = currentX + width;
                float pY2 = currentY + width;

                bufferBuilder.addVertex(matrix, pX1, pY2, z).setColor(red, green, blue, alpha); // Bottom-left
                bufferBuilder.addVertex(matrix, pX2, pY2, z).setColor(red, green, blue, alpha); // Bottom-right
                bufferBuilder.addVertex(matrix, pX2, pY1, z).setColor(red, green, blue, alpha); // Top-right
                bufferBuilder.addVertex(matrix, pX1, pY1, z).setColor(red, green, blue, alpha); // Top-left
            }
        }
    }
}
