package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TextureObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;
import java.util.function.Function;

public class TextureRenderer implements IThreeDObjectRenderer<TextureObject> {
    @Override
    public void renderBatch(List<TextureObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        Matrix4f lastPose = poseStack.last().pose();
        Matrix4f mat = new Matrix4f();

        for (TextureObject obj : batch) {
            Function<ThreeDimensionalObject, RenderType> renderTypesMap = obj.updateAndGetRenderTypes();
            if (renderTypesMap == null) {
                continue;
            }
            VertexConsumer buffer = bufferSource.getBuffer(renderTypesMap.apply(obj));

            mat.set(lastPose);

            float r = RenderUtil.getRed(obj.color), g = RenderUtil.getGreen(obj.color), b = RenderUtil.getBlue(obj.color), a = obj.opacity;

            if (obj.relativePosition) {
                mat.translate((float) eyePos.x, (float) eyePos.y, (float) eyePos.z);
                if (obj.relativeRotation) {
                    mat.rotate(eyeRotation);
                }
            }
            mat.translate(obj.x, obj.y, obj.z);
            mat.rotate(obj.getRotation());

            buffer.vertex(mat, 0, 0, 0).color(r, g, b, a).uv(0, 1).endVertex();
            buffer.vertex(mat, obj.sizeX, 0, 0).color(r, g, b, a).uv(1, 1).endVertex();
            buffer.vertex(mat, obj.sizeX, obj.sizeY, 0).color(r, g, b, a).uv(1, 0).endVertex();
            buffer.vertex(mat, 0, obj.sizeY, 0).color(r, g, b, a).uv(0, 0).endVertex();
        }
    }
}
