package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BoxObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class BoxRenderer implements IThreeDObjectRenderer<BoxObject> {
    private static final RenderUtil.BoxLightMap FULL_BRIGHT = RenderUtil.BoxLightMap.createFullBright();

    @Override
    public void renderBatch(List<BoxObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        for (BoxObject box : batch) {
            VertexConsumer bufferBuilder = bufferSource.getBuffer(APRenderTypes.QUADS_3D_MAP.apply(box));

            poseStack.pushPose();

            Vector4f color = new Vector4f(RenderUtil.getRed(box.color), RenderUtil.getGreen(box.color), RenderUtil.getBlue(box.color), box.opacity);

            if (box.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (box.relativeRotation) {
                    poseStack.mulPose(eyeRotation);
                }
            }
            poseStack.translate(box.x, box.y, box.z);
            poseStack.mulPose(box.getRotation());
            RenderUtil.drawBox(
                poseStack,
                bufferBuilder,
                FULL_BRIGHT,
                color,
                new Vector3f(box.sizeX, box.sizeY, box.sizeZ)
            );
            poseStack.popPose();
        }
    }
}
