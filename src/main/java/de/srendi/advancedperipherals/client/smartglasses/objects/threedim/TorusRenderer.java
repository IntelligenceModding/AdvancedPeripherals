package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TorusObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;

import java.util.List;

public class TorusRenderer implements IThreeDObjectRenderer<TorusObject> {
    @Override
    public void renderBatch(List<TorusObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        for (TorusObject torus : batch) {
            VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.QUADS_3D_MAP.apply(torus));

            poseStack.pushPose();

            float alpha = torus.opacity;
            float red = RenderUtil.getRed(torus.color);
            float green = RenderUtil.getGreen(torus.color);
            float blue = RenderUtil.getBlue(torus.color);

            if (torus.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (torus.relativeRotation) {
                    poseStack.mulPose(eyeRotation);
                }
            }
            poseStack.translate(torus.x, torus.y, torus.z);
            poseStack.mulPose(torus.getRotation());
            RenderUtil.drawTorus(poseStack, bufferBuilder, torus.majorRadius, torus.minorRadius, red, green, blue, alpha, torus.rings, torus.sides);

            poseStack.popPose();
        }
    }
}
