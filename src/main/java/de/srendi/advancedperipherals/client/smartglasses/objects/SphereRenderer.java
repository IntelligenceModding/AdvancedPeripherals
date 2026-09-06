package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.SphereObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public class SphereRenderer extends Simple2DObjectRenderer<SphereObject> {
    @Override
    protected void render(SphereObject sphere, GuiGraphics gui, DeltaTracker partialTick, boolean is3D) {
        VertexConsumer bufferBuilder = gui.bufferSource().getBuffer(APRenderTypes.QUADS_3D_MAP.apply(sphere));

        float r = RenderUtil.getRed(sphere.color), g = RenderUtil.getGreen(sphere.color), b = RenderUtil.getBlue(sphere.color), a = sphere.opacity;
        RenderUtil.drawSphere(gui.pose(), bufferBuilder, sphere.radius, r, g, b, a, sphere.sectors, sphere.stacks);
    }
}
