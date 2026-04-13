package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BoxObject;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class BoxRenderer implements IThreeDObjectRenderer<BoxObject> {
    private static final RenderUtil.BoxLightMap FULL_BRIGHT = RenderUtil.BoxLightMap.createFullBright();

    @Override
    public void renderBatch(List<BoxObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos) {
        Camera camera = event.getCamera();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer bufferBuilder = bufferSource.getBuffer(RenderType.debugStructureQuads());

        for (BoxObject box : batch) {
            this.onPreRender(box);
            poseStack.pushPose();

            Vector4f color = new Vector4f(RenderUtil.getRed(box.color), RenderUtil.getGreen(box.color), RenderUtil.getBlue(box.color), box.opacity);

            if (box.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (box.relativeRotation) {
                    poseStack.mulPose(camera.rotation());
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
            this.onPostRender(box);
        }

        bufferSource.endLastBatch();
    }
}
