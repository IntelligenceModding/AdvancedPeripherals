package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TorusObject;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

public class TorusRenderer implements IThreeDObjectRenderer<TorusObject> {

    @Override
    public void renderBatch(List<TorusObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos) {
        Camera camera = event.getCamera();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer bufferBuilder = bufferSource.getBuffer(RenderType.debugStructureQuads());

        for (TorusObject torus : batch) {
            this.onPreRender(torus);
            poseStack.pushPose();

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            float alpha = torus.opacity;
            float red = RenderUtil.getRed(torus.color);
            float green = RenderUtil.getGreen(torus.color);
            float blue = RenderUtil.getBlue(torus.color);

            if (torus.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (torus.relativeRotation) {
                    poseStack.mulPose(camera.rotation());
                }
            }
            poseStack.translate(torus.x, torus.y, torus.z);
            poseStack.mulPose(torus.getRotation());
            RenderUtil.drawTorus(poseStack, bufferBuilder, torus.majorRadius, torus.minorRadius, red, green, blue, alpha, torus.rings, torus.sides);

            poseStack.popPose();
            this.onPostRender(torus);
        }

        bufferSource.endLastBatch();
    }
}
