package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TriangleObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.List;

public class TriangleRenderer implements IThreeDObjectRenderer<TriangleObject> {
    @Override
    public void renderBatch(List<TriangleObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.debugFilledBox());

        for (TriangleObject tri : batch) {
            this.onPreRender(tri);
            poseStack.pushPose();

            float r = RenderUtil.getRed(tri.color), g = RenderUtil.getGreen(tri.color), b = RenderUtil.getBlue(tri.color), a = tri.opacity;

            poseStack.translate(-view.x, -view.y, -view.z);

            Matrix4f mat = poseStack.last().pose();
            buffer.addVertex(mat, tri.x, tri.y, tri.z).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT);
            buffer.addVertex(mat, tri.x2, tri.y2, tri.z2).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT);
            buffer.addVertex(mat, tri.x3, tri.y3, tri.z3).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT);

            poseStack.popPose();
            this.onPostRender(tri);
        }

        bufferSource.endLastBatch();
    }
}
