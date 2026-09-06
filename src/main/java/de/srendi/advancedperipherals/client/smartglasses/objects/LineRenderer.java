package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.LineObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public class LineRenderer implements IObjectRenderer<LineObject> {
    @Override
    public void render2D(List<LineObject> objects, GuiGraphics gui, DeltaTracker partialTick) {
        VertexConsumer bufferBuilder = gui.bufferSource().getBuffer(APRenderTypes.QUADS_2D);
        for (LineObject line : objects) {
            float width = line.width;
            float halfWidth = line.width / 2;
            if (halfWidth == 0) {
                continue;
            }

            float r = RenderUtil.getRed(line.color), g = RenderUtil.getGreen(line.color), b = RenderUtil.getBlue(line.color), a = line.opacity;

            // Start and end points of the line
            float x1 = line.x;
            float y1 = line.y;
            float x2 = line.endX;
            float y2 = line.endY;
            float z = line.z;

            float dx = x2 - x1;
            float dy = y2 - y1;

            gui.pose().pushPose();
            gui.pose().rotateAround(line.getRotation(partialTick.getGameTimeDeltaPartialTick(true)), (x1 + x2) / 2, (y1 + y2) / 2, z);

            Matrix4f matrix = gui.pose().last().pose();
            gui.pose().popPose();

            // Normal, smooth lines
            if (!line.pixelated) {
                if (y1 == y2) {
                    bufferBuilder.addVertex(matrix, x1, y1 - halfWidth, z).setColor(r, g, b, a);
                    bufferBuilder.addVertex(matrix, x1, y1 + halfWidth, z).setColor(r, g, b, a);
                    bufferBuilder.addVertex(matrix, x2, y1 + halfWidth, z).setColor(r, g, b, a);
                    bufferBuilder.addVertex(matrix, x2, y1 - halfWidth, z).setColor(r, g, b, a);
                    continue;
                }
                if (x1 == x2) {
                    bufferBuilder.addVertex(matrix, x1 - halfWidth, y1, z).setColor(r, g, b, a);
                    bufferBuilder.addVertex(matrix, x1 + halfWidth, y1, z).setColor(r, g, b, a);
                    bufferBuilder.addVertex(matrix, x1 + halfWidth, y2, z).setColor(r, g, b, a);
                    bufferBuilder.addVertex(matrix, x1 - halfWidth, y2, z).setColor(r, g, b, a);
                    continue;
                }
                float l = (float) Math.sqrt(dx * dx + dy * dy);
                float rx = -dy / l * halfWidth, ry = dx / l * halfWidth;
                bufferBuilder.addVertex(matrix, x1 - rx, y1 - ry, z).setColor(r, g, b, a);
                bufferBuilder.addVertex(matrix, x1 + rx, y1 + ry, z).setColor(r, g, b, a);
                bufferBuilder.addVertex(matrix, x2 + rx, y2 + ry, z).setColor(r, g, b, a);
                bufferBuilder.addVertex(matrix, x2 - rx, y2 - ry, z).setColor(r, g, b, a);
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

                bufferBuilder.addVertex(matrix, pX1, pY2, z).setColor(r, g, b, a); // Bottom-left
                bufferBuilder.addVertex(matrix, pX2, pY2, z).setColor(r, g, b, a); // Bottom-right
                bufferBuilder.addVertex(matrix, pX2, pY1, z).setColor(r, g, b, a); // Top-right
                bufferBuilder.addVertex(matrix, pX1, pY1, z).setColor(r, g, b, a); // Top-left
            }
        }
    }

    @Override
    public void render3D(List<LineObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        // TODO
    }
}
