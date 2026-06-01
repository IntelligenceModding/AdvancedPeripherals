package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TextureObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;
import java.util.function.Function;

public class TextureRenderer implements IThreeDObjectRenderer<TextureObject> {
    @Override
    public void renderBatch(List<TextureObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 eyePos, Quaternionf eyeRotation) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        for (TextureObject obj : batch) {
            Function<ThreeDimensionalObject, RenderType> renderTypesMap = obj.updateAndGetRenderTypes();
            if (renderTypesMap == null) {
                continue;
            }
            VertexConsumer buffer = bufferSource.getBuffer(renderTypesMap.apply(obj));

            poseStack.pushPose();

            if (obj.relativePosition) {
                poseStack.translate(eyePos.x, eyePos.y, eyePos.z);
                if (obj.relativeRotation) {
                    poseStack.mulPose(eyeRotation);
                }
            }
            poseStack.rotateAround(obj.getRotation(), obj.x, obj.y, obj.z);

            Matrix4f mat = poseStack.last().pose();
            buffer.addVertex(mat, obj.x, obj.y, obj.z).setUv(0, 0);
            buffer.addVertex(mat, obj.x + obj.sizeX, obj.y, obj.z).setUv(1, 0);
            buffer.addVertex(mat, obj.x + obj.sizeX, obj.y + obj.sizeY, obj.z).setUv(1, 1);
            buffer.addVertex(mat, obj.x, obj.y + obj.sizeY, obj.z).setUv(0, 1);

            poseStack.popPose();
        }
    }
}
