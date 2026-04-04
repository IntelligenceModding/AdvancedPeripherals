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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

public class BlockRenderer implements IThreeDObjectRenderer<BlockObject> {

    @Override
    public void renderBatch(List<BlockObject> batch, RenderLevelStageEvent event, PoseStack poseStack, Vec3 view) {
        Level level = event.getCamera().getEntity().level();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer bufferBuilder = bufferSource.getBuffer(RenderType.solid());

        for (BlockObject block : batch) {
            BlockState blockState = block.getBlockState();
            if (blockState == null) {
                continue;
            }

            this.onPreRender(block);

            poseStack.pushPose();

            poseStack.translate(-view.x, -view.y, -view.z);
            poseStack.translate(block.x - 0.5, block.y - 0.5, block.z - 0.5);
            poseStack.rotateAround(block.getRotation(), 0.5f, 0.5f, 0.5f);

            BlockPos blockPos = BlockPos.containing(block.x, block.y, block.z);

            blockRenderer.renderBatched(
                blockState,
                blockPos,
                level,
                poseStack,
                bufferBuilder,
                false,
                level.random
            );

            poseStack.popPose();

            // TODO: apply colors and culling settings
            // colors may require mixin VertexConsumer.putBulkData
            float alpha = block.opacity;
            float red = RenderUtil.getRed(block.color);
            float green = RenderUtil.getGreen(block.color);
            float blue = RenderUtil.getBlue(block.color);

            this.onPostRender(block);
        }

        bufferSource.endLastBatch();
    }
}
