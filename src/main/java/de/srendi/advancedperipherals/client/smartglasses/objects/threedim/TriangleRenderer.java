package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TriangleObject;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.List;

public class TriangleRenderer implements IThreeDObjectRenderer<TriangleObject> {
    public static final TriangleRenderer INSTANCE = new TriangleRenderer();

    private static final RenderType TRIANGLE_TYPE = RenderType.create(
        "ap_overlay_triangle",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.TRIANGLE_STRIP,
        1536,
        false,
        true,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false)
    );

    @Override
    public void renderBatch(List<TriangleObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos) {
        Camera camera = event.getCamera();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(TRIANGLE_TYPE);

        for (TriangleObject tri : batch) {
            this.onPreRender(tri);
            poseStack.pushPose();

            float r = RenderUtil.getRed(tri.color), g = RenderUtil.getGreen(tri.color), b = RenderUtil.getBlue(tri.color), a = tri.opacity;

            if (tri.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (tri.relativeRotation) {
                    poseStack.mulPose(camera.rotation());
                }
            }
            poseStack.rotateAround(tri.getRotation(), tri.x, tri.y, tri.z);

            Matrix4f mat = poseStack.last().pose();
            buffer.addVertex(mat, tri.x1, tri.y1, tri.z1).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT);
            buffer.addVertex(mat, tri.x2, tri.y2, tri.z2).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT);
            buffer.addVertex(mat, tri.x3, tri.y3, tri.z3).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT);

            poseStack.popPose();
            this.onPostRender(tri);
        }

        bufferSource.endLastBatch();
    }
}
