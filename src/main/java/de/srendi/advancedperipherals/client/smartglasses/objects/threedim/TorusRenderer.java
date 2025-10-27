package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.TorusObject;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.client.event.RenderLevelStageEvent;

import java.util.List;

public class TorusRenderer implements IThreeDObjectRenderer {

    @Override
    public void renderBatch(List<ThreeDimensionalObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view, BufferBuilder bufferBuilder) {
        poseStack.pushPose();

        for (ThreeDimensionalObject obj : batch) {
            poseStack.pushPose();
            onPreRender(obj);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_NORMAL);

            TorusObject torus = (TorusObject) obj;

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            float alpha = torus.opacity;
            float red = RenderUtil.getRed(torus.color);
            float green = RenderUtil.getGreen(torus.color);
            float blue = RenderUtil.getBlue(torus.color);

            poseStack.translate(-view.x + torus.x, -view.y + torus.y, -view.z + torus.z);
            RenderUtil.drawTorus(poseStack, bufferBuilder, torus.majorRadius, torus.minorRadius, 0, 0, 0, torus.rotX, torus.rotY, torus.rotZ, red, green, blue, alpha, torus.rings, torus.sides);
            BufferUploader.drawWithShader(bufferBuilder.end());
            onPostRender(obj);

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
