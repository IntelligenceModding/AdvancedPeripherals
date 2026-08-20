package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

import java.util.List;

public class CircleRenderer implements ITwoDObjectRenderer<CircleObject> {
    @Override
    public void renderBatch(List<CircleObject> objects, GuiGraphics gui, float partialTick) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        for (CircleObject circle : objects) {
            float alpha = circle.opacity;
            float red = RenderUtil.getRed(circle.color);
            float green = RenderUtil.getGreen(circle.color);
            float blue = RenderUtil.getBlue(circle.color);

            drawCircle(bufferSource, gui.pose(), circle, red, green, blue, alpha);
        }
    }

    private void drawCircle(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, CircleObject circle, float red, float green, float blue, float alpha) {
        float r = circle.radius;
        float cx = circle.x;
        float cy = circle.y;
        float cz = circle.z;
        float borderWidth = circle.borderWidth;
        int segments = circle.segments;

        boolean isFilled = circle.filled;
        boolean isPixelated = circle.pixelated;

        poseStack.pushPose();

        poseStack.translate(cx, cy, cz);
        poseStack.mulPose(circle.getRotation());

        Matrix4f matrix = poseStack.last().pose();

        // Normal, smooth lines
        if (!isPixelated) {
            if (isFilled) {
                VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.TRIANGLE_FAN_2D);

                bufferBuilder.vertex(matrix, 0, 0, 0f).color(red, green, blue, alpha).endVertex();

                double angleStep = Math.PI * 2 / segments;
                for (int i = 0; i <= segments; i++) {
                    double angle = i * angleStep;
                    double x = r * Math.sin(angle);
                    double y = r * Math.cos(angle);
                    bufferBuilder.vertex(matrix, (float) x, (float) y, 0).color(red, green, blue, alpha).endVertex();
                }
            } else {
                VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.TRIANGLE_STRIP_2D);

                float outerRadius = r;
                float innerRadius = r - borderWidth;

                double angleStep = Math.PI * 2 / segments;
                for (int i = 0; i <= segments; i++) {
                    double angle = i * angleStep;

                    // Outer circle vertex
                    double outerX = innerRadius * Math.sin(angle);
                    double outerY = innerRadius * Math.cos(angle);
                    bufferBuilder.vertex(matrix, (float) outerX, (float) outerY, 0f).color(red, green, blue, alpha).endVertex();

                    // Inner circle vertex
                    double innerX = outerRadius * Math.sin(angle);
                    double innerY = outerRadius * Math.cos(angle);
                    bufferBuilder.vertex(matrix, (float) innerX, (float) innerY, 0f).color(red, green, blue, alpha).endVertex();
                }
            }
        } else {
            // Pixelated lines
            VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.QUADS_2D);

            final float pixelSize = borderWidth; // Defines the size of each "pixel" square

            // The thickness of the hollow line in terms of pixel units.
            // A value of 1.0f means the line will be roughly one pixel thick.
            final float lineThicknessPixels = 1f;

            // Calculate the effective min/max coordinates in the relative space
            float effectiveMinX = -r - pixelSize;
            float effectiveMaxX = r + pixelSize;
            float effectiveMinY = -r - pixelSize;
            float effectiveMaxY = r + pixelSize;

            // Start the loop at the first multiple of PIXEL_SIZE that is less than or equal to effectiveMinX/Y
            float startX = (float) Math.floor(effectiveMinX / pixelSize) * pixelSize;
            float startY = (float) Math.floor(effectiveMinY / pixelSize) * pixelSize;


            for (float x = startX; x <= effectiveMaxX; x += pixelSize) {
                for (float y = startY; y <= effectiveMaxY; y += pixelSize) {
                    // Calculate the center of the current pixel cell.
                    // This is where you determine if the *center* of this block should be drawn.
                    float pixelCenterX = x + (pixelSize / 2.0F);
                    float pixelCenterY = y + (pixelSize / 2.0F);

                    // Distance is calculated from (pixelCenterX, pixelCenterY) to (0,0)
                    double distanceToCenter = Math.sqrt(
                            Math.pow(pixelCenterX, 2) + Math.pow(pixelCenterY, 2)
                    );

                    boolean shouldDrawPixel;
                    if (!isFilled) {
                        float outerRadius = r + (lineThicknessPixels * (pixelSize / 2.0F));
                        float innerRadius = r - (lineThicknessPixels * (pixelSize / 2.0F));
                        if (innerRadius < 0) {
                            innerRadius = 0;
                        }
                        shouldDrawPixel = (distanceToCenter <= outerRadius) && (distanceToCenter >= innerRadius);
                    } else {
                        shouldDrawPixel = distanceToCenter <= r + (pixelSize / 2.0F);
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
        poseStack.popPose();
    }
}

