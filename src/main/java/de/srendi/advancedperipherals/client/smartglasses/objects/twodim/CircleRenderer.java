package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.CircleObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class CircleRenderer implements ITwoDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        for (RenderableObject obj : objects) {

            CircleObject circle = (CircleObject) obj;

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

        poseStack.translate(cx, cy, cz);

        poseStack.pushPose();

        poseStack.mulPose(Vector3f.XP.rotationDegrees(rotX));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotY));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotZ));

        RenderSystem.disableCull();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        Matrix4f matrix = poseStack.last().pose();

        // Normal, smooth lines
        if (!isPixelated) {
            if (isFilled) {

                bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

                bufferbuilder.vertex(matrix, 0, 0, 0f).color(red, green, blue, alpha).endVertex();

                double angleStep = Math.PI * 2 / segments;

                for (int i = 0; i <= segments; i++) {
                    double angle = i * angleStep;
                    double x = r * Math.sin(angle);
                    double y = r * Math.cos(angle);

                    bufferbuilder.vertex(matrix, (float) x, (float) y, 0).color(red, green, blue, alpha).endVertex();
                }

            } else {
                float outerRadius = r;
                float innerRadius = r - borderWidth;

                bufferbuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

                double angleStep = Math.PI * 2 / segments;

                for (int i = 0; i <= segments; i++) {
                    double angle = i * angleStep;

                    // Outer circle vertex
                    double outerX = innerRadius * Math.sin(angle);
                    double outerY = innerRadius * Math.cos(angle);
                    bufferbuilder.vertex(matrix, (float) outerX, (float) outerY, 0f).color(red, green, blue, alpha).endVertex();

                    // Inner circle vertex
                    double innerX = outerRadius * Math.sin(angle);
                    double innerY = outerRadius * Math.cos(angle);
                    bufferbuilder.vertex(matrix, (float) innerX, (float) innerY, 0f).color(red, green, blue, alpha).endVertex();
                }
            }

            BufferUploader.drawWithShader(bufferbuilder.end());

            return;
        }

        // Pixelated lines
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        final float PIXEL_SIZE = borderWidth; // Defines the size of each "pixel" square

        // The thickness of the hollow line in terms of pixel units.
        // A value of 1.0f means the line will be roughly one pixel thick.
        final float LINE_THICKNESS_PIXELS = 1f;

        // Calculate the effective min/max coordinates in the relative space
        float effectiveMinX = -r - PIXEL_SIZE;
        float effectiveMaxX = r + PIXEL_SIZE;
        float effectiveMinY = -r - PIXEL_SIZE;
        float effectiveMaxY = r + PIXEL_SIZE;

// Start the loop at the first multiple of PIXEL_SIZE that is less than or equal to effectiveMinX/Y
        float startX = (float) Math.floor(effectiveMinX / PIXEL_SIZE) * PIXEL_SIZE;
        float startY = (float) Math.floor(effectiveMinY / PIXEL_SIZE) * PIXEL_SIZE;


        for (float x = startX; x <= effectiveMaxX; x += PIXEL_SIZE) {
            for (float y = startY; y <= effectiveMaxY; y += PIXEL_SIZE) {
                // Calculate the center of the current pixel cell.
                // This is where you determine if the *center* of this block should be drawn.
                float pixelCenterX = x + (PIXEL_SIZE / 2.0F);
                float pixelCenterY = y + (PIXEL_SIZE / 2.0F);

                // Distance is calculated from (pixelCenterX, pixelCenterY) to (0,0)
                double distanceToCenter = Math.sqrt(
                        Math.pow(pixelCenterX, 2) + Math.pow(pixelCenterY, 2)
                );

                boolean shouldDrawPixel;

                if (!isFilled) {
                    float outerRadius = r + (LINE_THICKNESS_PIXELS * (PIXEL_SIZE / 2.0F));
                    float innerRadius = r - (LINE_THICKNESS_PIXELS * (PIXEL_SIZE / 2.0F));

                    if (innerRadius < 0) innerRadius = 0;

                    shouldDrawPixel = (distanceToCenter <= outerRadius) && (distanceToCenter >= innerRadius);
                } else {
                    shouldDrawPixel = distanceToCenter <= r + (PIXEL_SIZE / 2.0F);
                }

                if (shouldDrawPixel) {
                    // Vertices for the QUAD (a PIXEL_SIZE x PIXEL_SIZE square)
                    // These coordinates are now relative to the current origin (0,0,0)
                    float p_x1 = x;
                    float p_y1 = y;
                    float p_z = 0f; // z-coordinate is relative to cz, so 0 in this space

                    float p_x2 = x + PIXEL_SIZE;
                    float p_y2 = y + PIXEL_SIZE;

                    // Vertices for the QUAD
                    // Ensure proper winding order (counter-clockwise for front face)
                    bufferbuilder.vertex(matrix, p_x1, p_y2, p_z).color(red, green, blue, alpha).endVertex(); // Bottom-left
                    bufferbuilder.vertex(matrix, p_x2, p_y2, p_z).color(red, green, blue, alpha).endVertex(); // Bottom-right
                    bufferbuilder.vertex(matrix, p_x2, p_y1, p_z).color(red, green, blue, alpha).endVertex(); // Top-right
                    bufferbuilder.vertex(matrix, p_x1, p_y1, p_z).color(red, green, blue, alpha).endVertex(); // Top-left
                }
            }
        }

        RenderSystem.enableCull();

        BufferUploader.drawWithShader(bufferbuilder.end());


        poseStack.popPose();

    }
}

