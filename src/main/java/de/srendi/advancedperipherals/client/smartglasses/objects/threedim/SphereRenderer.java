package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.SphereObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.List;

public class SphereRenderer implements IThreeDObjectRenderer {

    @Override
    public void renderBatch(List<ThreeDimensionalObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view, BufferBuilder bufferBuilder) {
        poseStack.pushPose();

        for (ThreeDimensionalObject obj : batch) {
            poseStack.pushPose();
            onPreRender(obj);
            VertexConsumer boxVertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));

            SphereObject sphere = (SphereObject) obj;

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            float alpha = sphere.opacity;
            float red = RenderUtil.getRed(sphere.color);
            float green = RenderUtil.getRed(sphere.color);
            float blue = RenderUtil.getRed(sphere.color);

            poseStack.translate(-view.x, -view.y, -view.z);
            RenderUtil.drawSphere(poseStack, boxVertexConsumer, sphere.radius, sphere.x, sphere.y, sphere.z, sphere.rotX, sphere.rotY, sphere.rotZ, red, green, blue, alpha, sphere.sectors, sphere.stacks);
            onPostRender(obj);

            poseStack.popPose();
        }


        poseStack.popPose();

    }
}
