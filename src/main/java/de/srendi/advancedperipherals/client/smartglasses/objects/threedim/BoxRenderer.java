package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BoxObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.client.event.RenderLevelStageEvent;

import java.util.List;

public class BoxRenderer implements IThreeDObjectRenderer {

    @Override
    public void renderBatch(List<ThreeDimensionalObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view) {
        poseStack.pushPose();

        for (ThreeDimensionalObject obj : batch) {
            BoxObject box = (BoxObject) obj;

            poseStack.pushPose();
            onPreRender(box);

            // TODO: we suppose to use bufferSource instead of Tesselator
            // BufferBuilder bufferBuilder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.solid());
            BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_NORMAL);

            float alpha = box.opacity;
            float red = RenderUtil.getRed(box.color);
            float green = RenderUtil.getGreen(box.color);
            float blue = RenderUtil.getBlue(box.color);

            poseStack.translate(-view.x + box.getX(), -view.y + box.getY(), -view.z + box.getZ());
            RenderUtil.drawBox(poseStack, bufferBuilder, red, green, blue, alpha, box.x, box.y, box.z, box.rotX, box.rotY, box.rotZ, box.maxX, box.maxY, box.maxZ);
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            onPostRender(box);

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
