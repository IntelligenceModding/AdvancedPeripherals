package de.srendi.advancedperipherals.client.smartglasses.objects.twodim;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RectangleObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

import java.util.List;

public class RectangleRenderer implements ITwoDObjectRenderer<RectangleObject> {
    @Override
    public void renderBatch(List<RectangleObject> objects, GuiGraphics gui, float partialTick) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.QUADS_2D);

        for (RectangleObject obj : objects) {
            if (obj.sizeX == 0 || obj.sizeY == 0) {
                continue;
            }

            gui.pose().pushPose();
            gui.pose().translate(obj.x, obj.y, obj.z);
            gui.pose().rotateAround(obj.getRotation(), obj.sizeX / 2, obj.sizeY / 2, 0);

            Matrix4f matrix = gui.pose().last().pose();

            gui.pose().popPose();

            float alpha = obj.opacity;
            float red = RenderUtil.getRed(obj.color);
            float green = RenderUtil.getGreen(obj.color);
            float blue = RenderUtil.getBlue(obj.color);

            bufferBuilder.vertex(matrix, 0, obj.sizeY, 0).color(red, green, blue, alpha).endVertex();;
            bufferBuilder.vertex(matrix, obj.sizeX, obj.sizeY, 0).color(red, green, blue, alpha).endVertex();;
            bufferBuilder.vertex(matrix, obj.sizeX, 0, 0).color(red, green, blue, alpha).endVertex();;
            bufferBuilder.vertex(matrix, 0, 0, 0).color(red, green, blue, alpha).endVertex();;

        }
    }
}
