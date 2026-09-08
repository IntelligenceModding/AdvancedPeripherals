package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.LineObject;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

public class LineRenderer extends Simple2DObjectRenderer<LineObject> {
    @Override
    public void render(LineObject line, GuiGraphics gui, float partialTick, boolean is3D) {
        VertexConsumer bufferBuilder = gui.bufferSource().getBuffer(APRenderTypes.QUADS_2D);

        float width = line.width;
        float halfWidth = line.width / 2;
        if (halfWidth == 0) {
            return;
        }

        float r = RenderUtil.getRed(line.color), g = RenderUtil.getGreen(line.color), b = RenderUtil.getBlue(line.color), a = line.opacity;

        float dx = line.endX - line.x;
        float dy = line.endY - line.y;

        Matrix4f matrix = gui.pose().last().pose();

        // Normal, smooth lines
        if (!line.pixelated) {
            if (dy == 0) {
                bufferBuilder.vertex(matrix, 0, -halfWidth, 0).color(r, g, b, a).endVertex();
                bufferBuilder.vertex(matrix, 0, halfWidth, 0).color(r, g, b, a).endVertex();
                bufferBuilder.vertex(matrix, dx, halfWidth, 0).color(r, g, b, a).endVertex();
                bufferBuilder.vertex(matrix, dx, -halfWidth, 0).color(r, g, b, a).endVertex();
                return;
            }
            if (dx == 0) {
                bufferBuilder.vertex(matrix, -halfWidth, 0, 0).color(r, g, b, a).endVertex();
                bufferBuilder.vertex(matrix, halfWidth, 0, 0).color(r, g, b, a).endVertex();
                bufferBuilder.vertex(matrix, halfWidth, dy, 0).color(r, g, b, a).endVertex();
                bufferBuilder.vertex(matrix, -halfWidth, dy, 0).color(r, g, b, a).endVertex();
                return;
            }
            float l = (float) Math.sqrt(dx * dx + dy * dy);
            float rx = -dy / l * halfWidth, ry = dx / l * halfWidth;
            bufferBuilder.vertex(matrix, -rx, -ry, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, rx, +ry, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, dx + rx, dy + ry, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, dx - rx, dy - ry, 0).color(r, g, b, a).endVertex();
            return;
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

            // Calculate the exact point on the line and
            // Snap current point to the nearest pixel grid for consistent placement.
            // This is key for placing pixels at corners or full side of each other.
            float x1 = Math.round(dx * t / width) * width;
            float y1 = Math.round(dy * t / width) * width;

            float x2 = x1 + width;
            float y2 = y1 + width;

            bufferBuilder.vertex(matrix, x1, y2, 0).color(r, g, b, a).endVertex(); // Bottom-left
            bufferBuilder.vertex(matrix, x2, y2, 0).color(r, g, b, a).endVertex(); // Bottom-right
            bufferBuilder.vertex(matrix, x2, y1, 0).color(r, g, b, a).endVertex(); // Top-right
            bufferBuilder.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex(); // Top-left
        }
    }
}
