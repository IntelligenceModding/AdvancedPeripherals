package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.SphereObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

public class SphereRenderer implements IThreeDObjectRenderer {

    @Override
    public void renderBatch(List<ThreeDimensionalObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view) {
        poseStack.pushPose();

        for (ThreeDimensionalObject obj : batch) {
            SphereObject sphere = (SphereObject) obj;

            poseStack.pushPose();
            onPreRender(sphere);
            BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_NORMAL);

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            float alpha = sphere.opacity;
            float red = RenderUtil.getRed(sphere.color);
            float green = RenderUtil.getGreen(sphere.color);
            float blue = RenderUtil.getBlue(sphere.color);

            poseStack.translate(-view.x, -view.y, -view.z);
            RenderUtil.drawSphere(poseStack, bufferBuilder, sphere.radius, sphere.x, sphere.y, sphere.z, sphere.rotX, sphere.rotY, sphere.rotZ, red, green, blue, alpha, sphere.sectors, sphere.stacks);
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

            onPostRender(sphere);
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
