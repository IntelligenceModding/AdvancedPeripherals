package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.two_dim.RenderableObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.List;

public class BlockRenderer implements IThreeDObjectRenderer {

    @Override
    public void renderBatch(List<RenderableObject> objects, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view, BufferBuilder bufferBuilder) {
        poseStack.pushPose();
        bufferBuilder.begin(RenderType.translucent().mode(), DefaultVertexFormat.POSITION_COLOR_NORMAL);

        for (RenderableObject renderableObject : objects) {
            poseStack.pushPose();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            float alpha = renderableObject.opacity;
            float red = (float) (renderableObject.color >> 16 & 255) / 255.0F;
            float green = (float) (renderableObject.color >> 8 & 255) / 255.0F;
            float blue = (float) (renderableObject.color & 255) / 255.0F;

            poseStack.translate(-view.x +renderableObject.getX(), -view.y + renderableObject.getY(), -view.z + renderableObject.getZ());

            RenderUtil.drawBox(poseStack, bufferBuilder, red, green, blue, alpha, renderableObject.getMaxX(), renderableObject.getMaxY(), renderableObject.getMaxX());
            poseStack.popPose();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());
        poseStack.popPose();
    }
}
