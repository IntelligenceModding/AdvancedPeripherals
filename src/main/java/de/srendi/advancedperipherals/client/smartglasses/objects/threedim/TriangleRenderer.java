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

        Matrix4f lastPose = poseStack.last().pose();
        Matrix4f mat = new Matrix4f();

        for (TriangleObject tri : batch) {
            VertexConsumer buffer = bufferSource.getBuffer(APRenderTypes.TRIANGLE_3D_MAP.apply(tri));

            mat.set(lastPose);

            float r = RenderUtil.getRed(tri.color), g = RenderUtil.getGreen(tri.color), b = RenderUtil.getBlue(tri.color), a = tri.opacity;

            if (tri.relativePosition) {
                mat.translate((float) eyePos.x, (float) eyePos.y, (float) eyePos.z);
                if (tri.relativeRotation) {
                    mat.rotate(eyeRotation);
                }
            }
            mat.translate(tri.x, tri.y, tri.z);
            mat.rotate(tri.getRotation());

            float x1 = tri.x1 - tri.x, y1 = tri.y1 - tri.y, z1 = tri.z1 - tri.z;
            float x2 = tri.x2 - tri.x, y2 = tri.y2 - tri.y, z2 = tri.z2 - tri.z;
            float x3 = tri.x3 - tri.x, y3 = tri.y3 - tri.y, z3 = tri.z3 - tri.z;
            buffer.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
            buffer.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
            buffer.addVertex(mat, x3, y3, z3).setColor(r, g, b, a);
        }
    }
}
