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
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RectangleObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import java.util.List;

public class RectangleRenderer implements ITwoDObjectRenderer<RectangleObject> {

    @Override
    public void renderBatch(List<RectangleObject> objects, GuiGraphics gui, PoseStack ignored, DeltaTracker partialTick, int screenWidth, int screenHeight) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (RectangleObject obj : objects) {
            float rotX = obj.rotX;
            float rotY = obj.rotY;
            float rotZ = obj.rotZ;

            PoseStack poseStack = new PoseStack();

            poseStack.pushPose();

            poseStack.translate(obj.x, obj.y, obj.z);

            poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotZ));

            Matrix4f matrix = poseStack.last().pose();

            float alpha = obj.opacity;
            float red = RenderUtil.getRed(obj.color);
            float green = RenderUtil.getGreen(obj.color);
            float blue = RenderUtil.getBlue(obj.color);

            bufferBuilder.addVertex(matrix, 0, obj.maxY - obj.y, 0).setColor(red, green, blue, alpha);
            bufferBuilder.addVertex(matrix, obj.maxX - obj.x, obj.maxY - obj.y, 0).setColor(red, green, blue, alpha);
            bufferBuilder.addVertex(matrix, obj.maxX - obj.x, 0, 0).setColor(red, green, blue, alpha);
            bufferBuilder.addVertex(matrix, 0, 0, 0).setColor(red, green, blue, alpha);
            poseStack.popPose();

        }

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }
}
