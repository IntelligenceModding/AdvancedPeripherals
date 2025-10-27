package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RenderableObject;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.client.gui.overlay.ForgeGui;

import java.util.List;

public class RectangleRenderer implements ITwoDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, ForgeGui gui, PoseStack ignored, float partialTick, int screenWidth, int screenHeight) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (RenderableObject obj : objects) {
            float rotX = obj.rotX;
            float rotY = obj.rotY;
            float rotZ = obj.rotZ;

            PoseStack poseStack = new PoseStack();

            poseStack.translate(obj.x, obj.y, obj.z);

            poseStack.pushPose();

            Matrix4f matrix = poseStack.last().pose();

            poseStack.mulPose(Vector3f.XP.rotationDegrees(rotX));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotY));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotZ));

            float alpha = obj.opacity;
            float red = RenderUtil.getRed(obj.color);
            float green = RenderUtil.getGreen(obj.color);
            float blue = RenderUtil.getBlue(obj.color);

            bufferbuilder.vertex(matrix, 0, obj.maxY - obj.y, 0).color(red, green, blue, alpha).endVertex();
            bufferbuilder.vertex(matrix, obj.maxX - obj.x, obj.maxY  - obj.y, 0).color(red, green, blue, alpha).endVertex();
            bufferbuilder.vertex(matrix, obj.maxX - obj.x, 0, 0).color(red, green, blue, alpha).endVertex();
            bufferbuilder.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha).endVertex();
            poseStack.popPose();

        }

        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
