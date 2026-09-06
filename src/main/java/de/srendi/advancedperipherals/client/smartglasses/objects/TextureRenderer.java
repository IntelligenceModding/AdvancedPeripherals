package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.TextureObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.ThreeDimensionalObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

import java.util.function.Function;

public class TextureRenderer extends Simple2DObjectRenderer<TextureObject> {
    @Override
    protected void render(TextureObject obj, GuiGraphics gui, DeltaTracker partialTick, boolean is3D) {
        Function<ThreeDimensionalObject, RenderType> renderTypesMap = obj.updateAndGetRenderTypes();
        if (renderTypesMap == null) {
            return;
        }
        VertexConsumer buffer = gui.bufferSource().getBuffer(renderTypesMap.apply(obj));

        float r = RenderUtil.getRed(obj.color), g = RenderUtil.getGreen(obj.color), b = RenderUtil.getBlue(obj.color), a = obj.opacity;

        Matrix4f mat = gui.pose().last().pose();
        buffer.addVertex(mat, 0, 0, 0).setColor(r, g, b, a).setUv(0, 1);
        buffer.addVertex(mat, obj.sizeX, 0, 0).setColor(r, g, b, a).setUv(1, 1);
        buffer.addVertex(mat, obj.sizeX, obj.sizeY, 0).setColor(r, g, b, a).setUv(1, 0);
        buffer.addVertex(mat, 0, obj.sizeY, 0).setColor(r, g, b, a).setUv(0, 0);
    }
}
