package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BoxObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.List;

public class BoxRenderer implements IThreeDObjectRenderer {

    @Override
    public void renderBatch(List<ThreeDimensionalObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view, BufferBuilder bufferBuilder) {
        poseStack.pushPose();

        for (ThreeDimensionalObject obj : batch) {
            poseStack.pushPose();
            onPreRender(obj);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_NORMAL);

            BoxObject box = (BoxObject) obj;

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            float alpha = box.opacity;
            float red = RenderUtil.getRed(box.color);
            float green = RenderUtil.getGreen(box.color);
            float blue = RenderUtil.getBlue(box.color);

            poseStack.translate(-view.x + box.getX(), -view.y + box.getY(), -view.z + box.getZ());
            RenderUtil.drawBox(poseStack, bufferBuilder, red, green, blue, alpha, box.x, box.y, box.z, obj.rotX, obj.rotY, obj.rotZ, obj.maxX, obj.maxY, obj.maxZ);
            BufferUploader.drawWithShader(bufferBuilder.end());
            onPostRender(obj);

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
