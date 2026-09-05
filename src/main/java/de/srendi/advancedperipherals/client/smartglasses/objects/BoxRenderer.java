package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.BoxObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BoxRenderer extends Simple2DObjectRenderer<BoxObject> {
    private static final RenderUtil.BoxLightMap FULL_BRIGHT = RenderUtil.BoxLightMap.createFullBright();

    @Override
    protected void render(BoxObject box, GuiGraphics gui, DeltaTracker partialTickTraker) {
        VertexConsumer bufferBuilder = gui.bufferSource().getBuffer(APRenderTypes.QUADS_3D_MAP.apply(box));

        Vector4f color = new Vector4f(RenderUtil.getRed(box.color), RenderUtil.getGreen(box.color), RenderUtil.getBlue(box.color), box.opacity);

        RenderUtil.drawBox(
            gui.pose().last().pose(),
            bufferBuilder,
            FULL_BRIGHT,
            color,
            new Vector3f(box.sizeX, box.sizeY, box.sizeZ)
        );
    }
}
