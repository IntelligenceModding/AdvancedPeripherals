package de.srendi.advancedperipherals.client.smartglasses.objects.threedim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.RenderUtil;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.BlockObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;

import java.util.List;

public class BlockRenderer implements IThreeDObjectRenderer<BlockObject> {

    @Override
    public void renderBatch(List<BlockObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view) {
        Level level = event.getCamera().getEntity().level();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer bufferBuilder = bufferSource.getBuffer(RenderType.solid());

        poseStack.pushPose();

        for (BlockObject block : batch) {
            if (block.block == null) {
                return;
            }
            Block blockToRender = BuiltInRegistries.BLOCK.get(block.block);
            if (blockToRender == null) {
                continue;
            }

            this.onPreRender(block);

            poseStack.pushPose();

            poseStack.translate(-view.x + block.getX(), -view.y + block.getY(), -view.z + block.getZ());
            poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(block.rotX), (float) Math.toRadians(block.rotY), (float) Math.toRadians(block.rotZ)));
            poseStack.translate(-0.5f, -0.5f, -0.5f);

            BlockPos blockPos = BlockPos.containing(block.getX(), block.getY(), block.getZ());

            blockRenderer.renderBatched(
                blockToRender.defaultBlockState(),
                blockPos,
                level,
                poseStack,
                bufferBuilder,
                false,
                level.random
            );

            poseStack.popPose();

            // TODO: apply colors and culling settings
            float alpha = block.opacity;
            float red = RenderUtil.getRed(block.color);
            float green = RenderUtil.getGreen(block.color);
            float blue = RenderUtil.getBlue(block.color);

            this.onPostRender(block);
        }

        poseStack.popPose();
        bufferSource.endLastBatch();
    }
}
