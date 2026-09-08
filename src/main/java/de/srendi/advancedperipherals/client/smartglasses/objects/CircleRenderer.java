package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.CircleObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class CircleRenderer extends Simple2DObjectRenderer<CircleObject> {
    @Override
    protected void render(CircleObject circle, GuiGraphics gui, float partialTick, boolean is3D) {
        float r = RenderUtil.getRed(circle.color), g = RenderUtil.getGreen(circle.color), b = RenderUtil.getBlue(circle.color), a = circle.opacity;

        drawCircle(gui.bufferSource(), gui.pose().last().pose(), circle, r, g, b, a);
    }

    private void drawCircle(MultiBufferSource.BufferSource bufferSource, Matrix4f matrix, CircleObject circle, float red, float green, float blue, float alpha) {
        float r = circle.radius;
        float borderWidth = circle.borderWidth;
        int segments = circle.segments;

        boolean isFilled = circle.filled;

        // Normal, smooth lines
        if (!circle.pixelated) {
            if (isFilled) {
                VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.TRIANGLE_FAN_2D);

                bufferBuilder.vertex(matrix, 0, 0, 0f).color(red, green, blue, alpha).endVertex();

                float angleStep = Mth.PI * 2 / segments;
                for (int i = 0; i <= segments; i++) {
                    float angle = i * angleStep;
                    float x = r * Mth.sin(angle);
                    float y = r * Mth.cos(angle);
                    bufferBuilder.vertex(matrix, x, y, 0).color(red, green, blue, alpha).endVertex();
                }
            } else {
                VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.TRIANGLE_STRIP_2D);

                float outerRadius = r;
                float innerRadius = r - borderWidth;

                float angleStep = Mth.PI * 2 / segments;
                for (int i = 0; i <= segments; i++) {
                    float angle = i * angleStep;

                    // Outer circle vertex
                    float outerX = innerRadius * Mth.sin(angle);
                    float outerY = innerRadius * Mth.cos(angle);
                    bufferBuilder.vertex(matrix, outerX, outerY, 0f).color(red, green, blue, alpha).endVertex();

                    // Inner circle vertex
                    float innerX = outerRadius * Mth.sin(angle);
                    float innerY = outerRadius * Mth.cos(angle);
                    bufferBuilder.vertex(matrix, innerX, innerY, 0f).color(red, green, blue, alpha).endVertex();
                }
            }
            return;
        }

        // Pixelated lines
        VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.QUADS_2D);

        final float pixelSize = borderWidth; // Defines the size of each "pixel" square
        final float halfPixelSize = pixelSize / 2;

        // The thickness of the hollow line in terms of pixel units.
        // A value of 1.0f means the line will be roughly one pixel thick.
        final float lineThicknessPixels = 1f;

        // Calculate the effective min/max coordinates in the relative space
        float effectiveMinX = -r - pixelSize;
        float effectiveMaxX = r + pixelSize;
        float effectiveMinY = -r - pixelSize;
        float effectiveMaxY = r + pixelSize;

        // Start the loop at the first multiple of PIXEL_SIZE that is less than or equal to effectiveMinX/Y
        float startX = Mth.floor(effectiveMinX / pixelSize) * pixelSize;
        float startY = Mth.floor(effectiveMinY / pixelSize) * pixelSize;


        for (float x = startX; x <= effectiveMaxX; x += pixelSize) {
            for (float y = startY; y <= effectiveMaxY; y += pixelSize) {
                // Calculate the center of the current pixel cell.
                // This is where you determine if the *center* of this block should be drawn.
                float pixelCenterX = x + halfPixelSize;
                float pixelCenterY = y + halfPixelSize;

                // Distance is calculated from (pixelCenterX, pixelCenterY) to (0,0)
                float distanceToCenter = pixelCenterX * pixelCenterX + pixelCenterY * pixelCenterY;
                float outerRadius = r + (lineThicknessPixels * halfPixelSize);

                boolean shouldDrawPixel = distanceToCenter <= outerRadius * outerRadius;
                if (shouldDrawPixel && !isFilled) {
                    float innerRadius = Math.max(0, r - (lineThicknessPixels * halfPixelSize));
                    if (distanceToCenter < innerRadius * innerRadius) {
                        shouldDrawPixel = false;
                    }
                }

                if (shouldDrawPixel) {
                    // Vertices for the QUAD (a PIXEL_SIZE x PIXEL_SIZE square)
                    // These coordinates are now relative to the current origin (0,0,0)
                    float pX1 = x;
                    float pY1 = y;
                    float pZ = 0f; // z-coordinate is relative to cz, so 0 in this space

                    float pX2 = x + pixelSize;
                    float pY2 = y + pixelSize;

                    // Vertices for the QUAD
                    // Ensure proper winding order (counter-clockwise for front face)
                    bufferBuilder.vertex(matrix, pX1, pY2, pZ).color(red, green, blue, alpha).endVertex(); // Bottom-left
                    bufferBuilder.vertex(matrix, pX2, pY2, pZ).color(red, green, blue, alpha).endVertex(); // Bottom-right
                    bufferBuilder.vertex(matrix, pX2, pY1, pZ).color(red, green, blue, alpha).endVertex(); // Top-right
                    bufferBuilder.vertex(matrix, pX1, pY1, pZ).color(red, green, blue, alpha).endVertex(); // Top-left
                }
            }
        }
    }
}

