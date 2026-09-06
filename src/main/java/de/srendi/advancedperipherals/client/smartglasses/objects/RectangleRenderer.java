package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.RectangleObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

public class RectangleRenderer extends Simple2DObjectRenderer<RectangleObject> {
    @Override
    protected void render(RectangleObject obj, GuiGraphics gui, DeltaTracker partialTick, boolean is3D) {
        if (obj.sizeX == 0 || obj.sizeY == 0) {
            return;
        }

        VertexConsumer bufferBuilder = gui.bufferSource().getBuffer(APRenderTypes.QUADS_2D);

        float w2 = obj.sizeX / 2, h2 = obj.sizeY / 2;
        float r = RenderUtil.getRed(obj.color), g = RenderUtil.getGreen(obj.color), b = RenderUtil.getBlue(obj.color), a = obj.opacity;

        Matrix4f matrix = gui.pose().last().pose();
        bufferBuilder.addVertex(matrix, -w2, h2, 0).setColor(r, g, b, a);
        bufferBuilder.addVertex(matrix, w2, h2, 0).setColor(r, g, b, a);
        bufferBuilder.addVertex(matrix, w2, -h2, 0).setColor(r, g, b, a);
        bufferBuilder.addVertex(matrix, -w2, -h2, 0).setColor(r, g, b, a);
    }
}
