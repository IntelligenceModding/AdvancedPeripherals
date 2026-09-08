package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.TorusObject;
import net.minecraft.client.gui.GuiGraphics;

public class TorusRenderer extends Simple2DObjectRenderer<TorusObject> {
    @Override
    protected void render(TorusObject torus, GuiGraphics gui, float partialTick, boolean is3D) {
        VertexConsumer bufferBuilder = gui.bufferSource().getBuffer(APRenderTypes.QUADS_3D_MAP.apply(torus));

        float r = RenderUtil.getRed(torus.color), g = RenderUtil.getGreen(torus.color), b = RenderUtil.getBlue(torus.color), a = torus.opacity;
        RenderUtil.drawTorus(gui.pose(), bufferBuilder, torus.majorRadius, torus.minorRadius, r, g, b, a, torus.sides, torus.rings);
    }
}
