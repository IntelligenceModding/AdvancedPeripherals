package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import java.util.List;

public class CircleRenderer implements ITwoDObjectRenderer<CircleObject> {
    @Override
    public void renderBatch(List<CircleObject> objects, GuiGraphics gui, PoseStack poseStack, DeltaTracker partialTick, int screenWidth, int screenHeight) {
        for (CircleObject circle : objects) {
            float alpha = circle.opacity;
            float red = RenderUtil.getRed(circle.color);
            float green = RenderUtil.getGreen(circle.color);
            float blue = RenderUtil.getBlue(circle.color);

            drawCircle(poseStack, circle, red, green, blue, alpha);
        }
    }

    public void drawCircle(PoseStack t, CircleObject circle, float red, float green, float blue, float alpha) {
        float r = circle.radius;
        float cx = circle.x;
        float cy = circle.y;
        float cz = circle.z;
        float rotX = circle.rotX;
        float rotY = circle.rotY;
        float rotZ = circle.rotZ;
        float borderWidth = circle.borderWidth;
        int segments = circle.segments;

        boolean isFilled = circle.filled;
        boolean isPixelated = circle.pixelated;

        PoseStack poseStack = new PoseStack();

        poseStack.pushPose();

        poseStack.translate(cx, cy, cz);

        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotZ));

        RenderSystem.disableCull();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder;

        Matrix4f matrix = poseStack.last().pose();

        // Normal, smooth lines
        if (!isPixelated) {
            if (isFilled) {
                bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

                bufferBuilder.addVertex(matrix, 0, 0, 0f).setColor(red, green, blue, alpha);

                double angleStep = Math.PI * 2 / segments;

                for (int i = 0; i <= segments; i++) {
                    double angle = i * angleStep;
                    double x = r * Math.sin(angle);
                    double y = r * Math.cos(angle);

                    bufferBuilder.addVertex(matrix, (float) x, (float) y, 0).setColor(red, green, blue, alpha);
                }

            } else {
                bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

                float outerRadius = r;
                float innerRadius = r - borderWidth;

                double angleStep = Math.PI * 2 / segments;

                for (int i = 0; i <= segments; i++) {
                    double angle = i * angleStep;

                    // Outer circle vertex
                    double outerX = innerRadius * Math.sin(angle);
                    double outerY = innerRadius * Math.cos(angle);
                    bufferBuilder.addVertex(matrix, (float) outerX, (float) outerY, 0f).setColor(red, green, blue, alpha);

                    // Inner circle vertex
                    double innerX = outerRadius * Math.sin(angle);
                    double innerY = outerRadius * Math.cos(angle);
                    bufferBuilder.addVertex(matrix, (float) innerX, (float) innerY, 0f).setColor(red, green, blue, alpha);
                }
            }

            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

            return;
        }

        // Pixelated lines
        bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

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

                    if (innerRadius < 0) innerRadius = 0;

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
                    bufferBuilder.addVertex(matrix, pX1, pY2, pZ).setColor(red, green, blue, alpha); // Bottom-left
                    bufferBuilder.addVertex(matrix, pX2, pY2, pZ).setColor(red, green, blue, alpha); // Bottom-right
                    bufferBuilder.addVertex(matrix, pX2, pY1, pZ).setColor(red, green, blue, alpha); // Top-right
                    bufferBuilder.addVertex(matrix, pX1, pY1, pZ).setColor(red, green, blue, alpha); // Top-left
                }
            }
        }

        RenderSystem.enableCull();

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        poseStack.popPose();
    }
}

