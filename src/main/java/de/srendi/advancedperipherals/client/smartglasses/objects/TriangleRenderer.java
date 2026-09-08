package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.TriangleObject;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

public class TriangleRenderer extends Simple2DObjectRenderer<TriangleObject> {
    public static final TriangleRenderer INSTANCE = new TriangleRenderer();

    @Override
    protected void render(TriangleObject tri, GuiGraphics gui, float partialTick, boolean is3D) {
        VertexConsumer buffer = gui.bufferSource().getBuffer(APRenderTypes.TRIANGLE_3D_MAP.apply(tri));

        float r = RenderUtil.getRed(tri.color), g = RenderUtil.getGreen(tri.color), b = RenderUtil.getBlue(tri.color), a = tri.opacity;

        Matrix4f mat = gui.pose().last().pose();
        float x1 = tri.x1 - tri.x, y1 = tri.y1 - tri.y, z1 = tri.z1 - tri.z;
        float x2 = tri.x2 - tri.x, y2 = tri.y2 - tri.y, z2 = tri.z2 - tri.z;
        float x3 = tri.x3 - tri.x, y3 = tri.y3 - tri.y, z3 = tri.z3 - tri.z;
        buffer.vertex(mat, x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.vertex(mat, x2, y2, z2).color(r, g, b, a).endVertex();
        buffer.vertex(mat, x3, y3, z3).color(r, g, b, a).endVertex();
    }
}
