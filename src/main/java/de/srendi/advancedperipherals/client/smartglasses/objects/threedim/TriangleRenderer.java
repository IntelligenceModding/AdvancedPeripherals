package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TriangleObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public class TriangleRenderer implements IThreeDObjectRenderer<TriangleObject> {
    public static final TriangleRenderer INSTANCE = new TriangleRenderer();

    @Override
    public void renderBatch(List<TriangleObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        for (TriangleObject tri : batch) {
            VertexConsumer buffer = bufferSource.getBuffer(APRenderTypes.TRIANGLE_3D_MAP.apply(tri));

            poseStack.pushPose();

            float r = RenderUtil.getRed(tri.color), g = RenderUtil.getGreen(tri.color), b = RenderUtil.getBlue(tri.color), a = tri.opacity;

            if (tri.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (tri.relativeRotation) {
                    poseStack.mulPose(eyeRotation);
                }
            }
            poseStack.rotateAround(tri.getRotation(), tri.x, tri.y, tri.z);

            Matrix4f mat = poseStack.last().pose();
            buffer.addVertex(mat, tri.x1, tri.y1, tri.z1).setColor(r, g, b, a);
            buffer.addVertex(mat, tri.x2, tri.y2, tri.z2).setColor(r, g, b, a);
            buffer.addVertex(mat, tri.x3, tri.y3, tri.z3).setColor(r, g, b, a);

            poseStack.popPose();
        }
    }
}
