package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.SphereObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;

import java.util.List;

public class SphereRenderer implements IThreeDObjectRenderer<SphereObject> {
    @Override
    public void renderBatch(List<SphereObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        for (SphereObject sphere : batch) {
            VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.QUADS_3D_MAP.apply(sphere));

            poseStack.pushPose();

            float alpha = sphere.opacity;
            float red = RenderUtil.getRed(sphere.color);
            float green = RenderUtil.getGreen(sphere.color);
            float blue = RenderUtil.getBlue(sphere.color);

            if (sphere.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (sphere.relativeRotation) {
                    poseStack.mulPose(eyeRotation);
                }
            }
            poseStack.translate(sphere.x, sphere.y, sphere.z);
            poseStack.mulPose(sphere.getRotation());
            RenderUtil.drawSphere(poseStack, bufferBuilder, sphere.radius, red, green, blue, alpha, sphere.sectors, sphere.stacks);

            poseStack.popPose();
        }
    }
}
